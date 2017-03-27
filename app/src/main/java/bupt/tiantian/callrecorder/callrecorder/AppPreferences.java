package bupt.tiantian.callrecorder.callrecorder;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;

import java.io.File;

/**
 * A Singleton
 * Wrapper around the default SharedPreferences so we can easily set and read our settings
 */
public class AppPreferences {
    private static AppPreferences instance = null;
    private SharedPreferences preferences;
    private String defaultStoragePath;

    /**
     * Must be called once on app startup
     *
     * @param context - application context
     * @return this
     */
    public static AppPreferences getInstance(Context context) {
        if (instance == null) {
            if (context == null) {
                throw new IllegalStateException(AppPreferences.class.getSimpleName() +
                        " is not initialized, call getInstance(Context) with a VALID Context first.");
            }
            instance = new AppPreferences(context.getApplicationContext());
        }
        return instance;
    }

    private AppPreferences(Context context) {
        // From Android sources for getDefaultSharedPreferences
        preferences = context.getSharedPreferences(
                context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        defaultStoragePath = new ContextCompat().getExternalFilesDirs(context, null)[0].getAbsolutePath();
    }

    /**
     * Is Recording Enabled
     *
     * @return true/false
     */
    public boolean isRecordingEnabled() {
        return preferences.getBoolean("RecordingEnabled", true);
    }

    /**
     * Set Recording is Enabled
     *
     * @param enabled true/false
     */
    public void setRecordingEnabled(boolean enabled) {
        preferences.edit().putBoolean("RecordingEnabled", enabled).commit();
    }


    /**
     * Record incoming calls if {@link #isRecordingEnabled() isRecordingEnabled}
     *
     * @return true/false
     */
    public boolean isRecordingIncomingEnabled() {
        return preferences.getBoolean("RecordingIncomingEnabled", true);
    }

    /**
     * Set Recording is Enabled for incoming calls
     *
     * @param enabled true/false
     */
    public void setRecordingIncomingEnabled(boolean enabled) {
        preferences.edit().putBoolean("RecordingIncomingEnabled", enabled).commit();
    }

    /**
     * Record outgoing calls if {@link #isRecordingEnabled() isRecordingEnabled}
     *
     * @return true/false
     */
    public boolean isRecordingOutgoingEnabled() {
        return preferences.getBoolean("RecordingOutgoingEnabled", true);
    }

    /**
     * Set Recording is Enabled for outgoing calls
     *
     * @param enabled true/false
     */
    public void setRecordingOutgoingEnabled(boolean enabled) {
        preferences.edit().putBoolean("RecordingOutgoingEnabled", enabled).commit();
    }

    /**
     * Get the location to store the recordings too...
     * TODO: make configurable
     *
     * @return directory location
     */
    public File getFilesDirectory() {
        // might make this configurable....
        String filesDir = preferences.getString("FilesDirectoryNew", defaultStoragePath);
        File myDir = new File(filesDir);
        if (myDir.exists() || myDir.mkdirs()) {
            return myDir;
        }
        return new File(defaultStoragePath);
    }

    public enum OlderThan {
        NEVER,
        DAILY,
        THREE_DAYS,
        WEEKLY,
        MONTHLY,
        QUARTERLY,
        YEARLY
    }


    /**
     * get the Older Than Days for recording cleanup
     *
     * @return 0 if not set
     */

    public OlderThan getOlderThan() {
        String name = preferences.getString("OlderThan", OlderThan.NEVER.name());
        return OlderThan.valueOf(name);
    }

    /**
     * Set the Older Than date for recording cleanup
     *
     * @param olderThan NEVER default
     */

    public void setOlderThan(OlderThan olderThan) {
        preferences.edit().putString("OlderThan", olderThan.name()).commit();
    }


}
