/**
 * 
 */
package com.cybern.util;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;

/**
 * @author amitkumarsah
 * 
 */
public class ShowVisualizer {

	private Activity mAct;
	private Visualizer mVisualizer;
	private VisualizerView mVisualizerView;

	// private static final float VISUALIZER_HEIGHT_DIP = 50f;

	public ShowVisualizer(Activity act) {
		mAct = act;
	}

	public void realseVisualizer() {
		if (mVisualizer != null)
			mVisualizer.release();
	}

	public Visualizer setupVisualizerFxAndUI(int layout_id,
			MediaPlayer mediaplayer) {
		if (mediaplayer == null)
			return null;

		// Create a VisualizerView (defined below), which will render the
		// simplified audio
		// wave form to a Canvas.
		mVisualizerView = (VisualizerView) mAct.findViewById(layout_id);
		// mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
		// ViewGroup.LayoutParams.FILL_PARENT,
		// (int) (VISUALIZER_HEIGHT_DIP * mAct.getResources()
		// .getDisplayMetrics().density)));

		// layout.addView(mVisualizerView);// Linear Layout

		// Create the Visualizer object and attach it to our media player.
		mVisualizer = new Visualizer(mediaplayer.getAudioSessionId());
		mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
		mVisualizer.setDataCaptureListener(
				new Visualizer.OnDataCaptureListener() {
					public void onWaveFormDataCapture(Visualizer visualizer,
							byte[] bytes, int samplingRate) {
						mVisualizerView.updateVisualizer(bytes);
					}

					public void onFftDataCapture(Visualizer visualizer,
							byte[] bytes, int samplingRate) {
					}
				}, Visualizer.getMaxCaptureRate() / 2, true, false);
		return mVisualizer;
	}

	public void enableVisualizer(boolean enable) {
		if (mVisualizer != null)
			mVisualizer.setEnabled(enable);
	}
}
