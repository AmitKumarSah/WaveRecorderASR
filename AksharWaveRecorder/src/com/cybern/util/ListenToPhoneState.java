/**
 * 
 */
package com.cybern.util;

/**
 * @author amitkumarsah
 *
 */
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

//Makes sure the audio is paused for incoming/outgoing phone calls
public class ListenToPhoneState extends PhoneStateListener {

    private boolean pausedForPhoneCall = false;
	private UIManager uiManager;

    public ListenToPhoneState(UIManager manager){
        uiManager = manager;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                resumeInAndroid();
                return;
            case TelephonyManager.CALL_STATE_OFFHOOK: 
                pauseInAndroid();               
                return;
            case TelephonyManager.CALL_STATE_RINGING: 
                pauseInAndroid();               
                return;
        }
    }

    private void resumeInAndroid(){
        if(pausedForPhoneCall == true) {
            pausedForPhoneCall=false;
            uiManager.waitForPhoneCall(false);
        }
    }

    private void pauseInAndroid(){
        if(pausedForPhoneCall == false){
            pausedForPhoneCall=true;
            uiManager.waitForPhoneCall(true);
        }
    }

    String stateName(int state) {
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE: return "Idle";
            case TelephonyManager.CALL_STATE_OFFHOOK: return "Off hook";
            case TelephonyManager.CALL_STATE_RINGING: return "Ringing";
        }
        return Integer.toString(state);
    }
}

// ListenToPhoneState listener = new ListenToPhoneState(userInterface);
// TelephonyManager tManager = (TelephonyManager)
// getSystemService(Context.TELEPHONY_SERVICE);
// if(tManager != null)
// tManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
