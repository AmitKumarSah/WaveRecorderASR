/**
 * 
 */
package com.cybern.waverecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

/**
 * @author amitkumarsah
 * 
 */
public class WaveRecorder {

	/**
	 * Files Settings
	 */
	private static String AUDIO_RECORDER_FOLDER = "AksharRecorder";
	private static String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";

	private static String AUDIOFILENAME = "";
	private static int BytesPerElement = 2; // 2 bytes in 16bit format

	/**
	 * Recorder Setting
	 */
	private static int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private static int RECORDER_BPP = 16;
	private static int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	private static int RECORDER_SAMPLERATE = 16000;
	private static final String Tag = "WaveRecorder";

	/**
	 * @return the AUDIOFILENAME
	 */
	public static String getAudioFileName() {
		return AUDIOFILENAME;

	}

	public static void setAudioFileName(String fname) {
		AUDIOFILENAME = fname;
		Log.i(Tag, "AUDIOFILENAME set as " + AUDIOFILENAME);

	}

	public static void setAudioFolder(String folder) {
		AUDIO_RECORDER_FOLDER = folder;
	}

	public static boolean setParameters(int samplerate, int channels,
			int encoding, int bpp) {
		RECORDER_SAMPLERATE = samplerate;
		RECORDER_CHANNELS = channels;
		RECORDER_AUDIO_ENCODING = encoding;
		RECORDER_BPP = bpp;
		return true;
	}

	/**
	 * Basic Variables
	 */
	private int bufferSize = 0;
	public boolean mIsPlaying = false;
	/**
	 * Boolean Flags to controls the operations
	 * 
	 */
	public boolean mIsRecording = false;
	private AudioRecord recorder = null;

	private Thread recordingThread = null;

	/**
	 * 
	 */
	public WaveRecorder() {

	}

	/**
	 * copy wave file and save it
	 */
	private void copyToWaveFile() {

		String tempFilename = getTempFilename();
		String filename = getAudioFileName();
		copyWaveFile(tempFilename, filename);

	}

	public void copyWaveFile(String inFilename, String outFilename) {
		LogI("copyWaveFile_infile=" + inFilename);
		LogI("copyWaveofile_outfile=" + outFilename);

		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 36;
		long longSampleRate = RECORDER_SAMPLERATE;
		int channels = 2;
		if (RECORDER_CHANNELS == AudioFormat.CHANNEL_IN_MONO)
			channels = 1;

		long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels / 8;

		byte[] data = new byte[bufferSize];

		try {
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(outFilename);
			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 36;

			LogI("CopyWaveFle_File size: " + totalDataLen);

			WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
					longSampleRate, channels, byteRate);

			while (in.read(data) != -1) {
				out.write(data);
			}

			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteTempFile() {
		File file = new File(getTempFilename());
		if (file.delete())
			LogI("File got deleted");
		else
			LogI("File deletion failed");

	}

	public byte[] gainAudio(short data[], int len, float gain) {
		for (int i = 0; i < len; i++) {
			data[i] = (short) Math.min((int) (data[i] * gain),
					(int) Short.MAX_VALUE);
		}
		return short2byte(data);
	}

	public String getTempFilename() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, AUDIO_RECORDER_FOLDER);
		if (!file.exists()) {
			file.mkdirs();
		}
		return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
	}

	/**
	 * Initilize the recording Akshar Recording
	 */
	public void initRecording() {
		bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
				RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
		Log.i(Tag, "BufferSize=" + bufferSize);

	}

	private void LogI(String msg) {
		Log.i(Tag, msg);
	}

	public String setTempFilename() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, AUDIO_RECORDER_FOLDER);

		if (!file.exists()) {
			file.mkdirs();
		}
		File tempFile = new File(filepath, AUDIO_RECORDER_TEMP_FILE);

		if (tempFile.exists())
			tempFile.delete();  
		return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);

	}

	public byte[] short2byte(short[] sData) {
		int shortArrsize = sData.length;
		byte[] bytes = new byte[shortArrsize * 2];
		for (int i = 0; i < shortArrsize; i++) {
			bytes[i * 2] = (byte) (sData[i] & 0x00FF);
			bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
			sData[i] = 0;
		}
		return bytes;

	}

	/**
	 * start recording Akshar recording
	 */
	public void startRecord(final float gain) {
		if (!mIsPlaying && !mIsRecording) {
			mIsRecording = true;
			initRecording();
			recorder = new AudioRecord(
					MediaRecorder.AudioSource.VOICE_COMMUNICATION,
					RECORDER_SAMPLERATE, RECORDER_CHANNELS,
					RECORDER_AUDIO_ENCODING, bufferSize * BytesPerElement);
			setTempFilename();// Temp File will be created
			if (recorder.getState() == 1) {
				recorder.startRecording();
				recordingThread = new Thread(new Runnable() {
					@Override
					public void run() {
						writeAudioDataToFileWithGain(gain);
					}
				}, "AudioRecorder Thread");
				recordingThread.start();
			} else
				Log.i(Tag, "Recorder is not initilzed");

		} else
			Log.i(Tag, "StartRecord_Player or record already on");

	}

	/**
	 * stop recording Akshar
	 */
	public void stopRecord() {
		if (null != recorder && mIsRecording) {
			LogI("stop");
			mIsRecording = false;
			recorder.stop();
			recorder.release();
			recorder = null;
			recordingThread = null;
			copyToWaveFile();
			deleteTempFile();
			mIsRecording = false;
			LogI("stop");
		}
	}

	/**
	 * @return the mIsRecording
	 */
	public boolean ismIsRecording() {
		return mIsRecording;
	}

	/**
	 * @param mIsRecording the mIsRecording to set
	 */
	public void setmIsRecording(boolean mIsRecording) {
		this.mIsRecording = mIsRecording;
	}

	public void writeAudioDataToFileWithGain(float gain) {
		short data[] = new short[bufferSize];
		String filename = getTempFilename();
		LogI("writeAuidDataGain_temp file=" + filename);
		FileOutputStream os = null;

		try {
			os = new FileOutputStream(filename);

		} catch (FileNotFoundException e) {
			//  
			e.printStackTrace();
		}
		int read = 0;
		if (null != os) {
			while (mIsRecording) {

				read = recorder.read(data, 0, bufferSize);

				if (AudioRecord.ERROR_INVALID_OPERATION != read) {
					try {
						// os.write(gainAudio(data, read, gain));
						os.write(gainAudio(data, read, gain), 0, bufferSize
								* BytesPerElement);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else
					Log.i(Tag, "Error Invalid Operation");
			}

			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			Log.i("Gain", "OS FILE IS NULL");
	}

	/**
	 * 
	 * @param out
	 * @param totalAudioLen
	 * @param totalDataLen
	 * @param longSampleRate
	 * @param channels
	 * @param byteRate
	 * @throws IOException
	 */
	public void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
			long totalDataLen, long longSampleRate, int channels, long byteRate)
			throws IOException {

		byte[] header = new byte[44];

		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (2 * 16 / 8); // block align
		header[33] = 0;
		header[34] = (byte) RECORDER_BPP; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

		out.write(header, 0, 44);
	}
	public boolean cancelRecord() {
		LogI("cancelRecord called");
		if (null != recorder) {
			mIsRecording = false;

			recorder.stop();
			recorder.release();

			recorder = null;
			recordingThread = null;
		}
		deleteTempFile();

		return true;
	}

}
