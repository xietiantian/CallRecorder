package bupt.tiantian.callrecorder.callrecorder;

import android.os.Bundle;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import bupt.tiantian.callrecorder.R;
import bupt.tiantian.callrecorder.database.CallLog;
import bupt.tiantian.callrecorder.database.Database;
import bupt.tiantian.callrecorder.receivers.MyAlarmReceiver;
import bupt.tiantian.callrecorder.services.CleanupService;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by tiantian on 17-2-13.
 */
public class SettingsActivity extends AppCompatActivity
        implements SettingsFragment.OnFragmentInteractionListener {

    private TextView tvTotalFilesSize;
    private TextView tvTotalFiles;
    private TextView tvTotalFolderSize;
    private Button btnCleanNow;
    private String mStorgePath = null;
    boolean permissionWriteExternal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permissionWriteExternal = getIntent().getBooleanExtra("write_external", false);

        setContentView(R.layout.activity_settings);

        tvTotalFiles = (TextView) findViewById(R.id.tvTotalFiles);
        tvTotalFilesSize = (TextView) findViewById(R.id.tvTotalFilesSize);
        tvTotalFolderSize = (TextView) findViewById(R.id.tvTotalFolderSize);
        btnCleanNow = (Button) findViewById(R.id.btnCleanNow);

        // Now, count the recordings
        ArrayList<CallLog> allCalls = Database.getInstance(getApplicationContext()).getAllCalls();
        String str = tvTotalFiles.getText().toString();
        str = String.format(str, allCalls.size());
        tvTotalFiles.setText(Html.fromHtml(str));

        // Get the length of each file...
        long length = 0;
        for (CallLog call : allCalls) {
            File file = new File(call.getPathToRecording());
            length += file.length();
        }
        str = tvTotalFilesSize.getText().toString();
        str = String.format(str, length / 1024);
        tvTotalFilesSize.setText(Html.fromHtml(str));

        if (mStorgePath != null) {
            calcFreeSpace(mStorgePath);
        }

        btnCleanNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CleanupService.startCleaning(SettingsActivity.this);
            }
        });
    }


    @Override
    public void onStorgeLocationChanged(String path) {
        mStorgePath = path;
        calcFreeSpace(path);
    }

    @Override
    public void onStorgeLocationInit(String path) {
        mStorgePath = path;
    }

    private void calcFreeSpace(String path) {
        // http://stackoverflow.com/questions/3394765/how-to-check-available-space-on-android-device-on-mini-sd-card
        StatFs stat = new StatFs(path);
        long bytesTotal = 0;
        long bytesAvailable = 0;
        float megAvailable = 0;
        long megTotalAvailable = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytesTotal = stat.getBlockSizeLong() * stat.getBlockCountLong();
            bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        } else {
            bytesTotal = (long) stat.getBlockSize() * (long) stat.getBlockCount();
            bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
        }
        megAvailable = bytesAvailable / 1048576;
        megTotalAvailable = bytesTotal / 1048576;

        // Free Space
        String str = getString(R.string.pref_folder_total_folder_size);
        str = String.format(str, megAvailable);
        tvTotalFolderSize.setText(Html.fromHtml(str));
    }

    @Override
    protected void onStop() {
        final AppPreferences.OlderThan olderThan = AppPreferences.getInstance(this).getOlderThan();
        if (olderThan != AppPreferences.OlderThan.NEVER) {
            MyAlarmReceiver.setAlarm(SettingsActivity.this);
        } else {
            MyAlarmReceiver.cancleAlarm(SettingsActivity.this);
        }
        super.onStop();
    }
}
