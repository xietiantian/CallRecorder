package bupt.tiantian.callrecorder.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Internal BroadcastReceiver to handle
 * {@link LocalBroadcastActions}
 */

public class MyLocalBroadcastReceiver extends BroadcastReceiver {

    public interface OnNewRecordingListener{
        void OnBroadcastReceived(Intent intent);
    }

    OnNewRecordingListener listener;

    public MyLocalBroadcastReceiver(OnNewRecordingListener listener) {
        this.listener = listener;
    }

    public MyLocalBroadcastReceiver() {
    }

    public void setListener( OnNewRecordingListener listener){
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(null!=listener) listener.OnBroadcastReceived(intent);
    }
}
