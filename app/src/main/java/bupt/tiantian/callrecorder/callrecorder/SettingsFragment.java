package bupt.tiantian.callrecorder.callrecorder;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bupt.tiantian.callrecorder.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends PreferenceFragment {

    private OnFragmentInteractionListener mListener;
    private ListPreference dirList;
    private ListPreference cleanList;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        Context context = getActivity();

        dirList = (ListPreference) findPreference("FilesDirectoryNew");
        List<String> list = new ArrayList<String>();
        String folderName = context.getString(R.string.folder_name);

        if (context instanceof SettingsActivity) {
            if (((SettingsActivity) context).permissionWriteExternal) {
                //加入扩展卡存储目录(卸载后不会删除，需要权限)
                String externalDirPath = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath())
                        .append("/").append(folderName).append("/").toString();
                File externalDir = new File(externalDirPath);
                if(externalDir.exists() || externalDir.mkdir()){
                    list.add(externalDirPath);
                }
            }
        }

        //加入扩展卡存储目录（卸载后会删除，KITKAT后的版本不需要权限)
        File[] externalFilesDirs = new ContextCompat().getExternalFilesDirs(context, null);
        for (File file : externalFilesDirs) {
            list.add(file.getAbsolutePath());
        }

        //加入手机内存存储目录（卸载后会删除，不需要权限)
        File filesDir = context.getFilesDir();
        list.add(filesDir.getAbsolutePath());

        String[] entriesArray = new String[list.size()];
        list.toArray(entriesArray);
        dirList.setEntries(entriesArray);
        dirList.setEntryValues(entriesArray);


        dirList.setDefaultValue(entriesArray[0]);
        if (dirList.getEntry() == null) {//首次打开时value值为“1”不是合法路径，先用getEntry判断一下
            dirList.setValue(entriesArray[0]);//value值不是entryValueArray中的值，将value重新设为默认值
        }

        String dirString = dirList.getValue();
        dirList.setSummary(dirString);
        if (mListener != null) {
            mListener.onStorgeLocationInit(dirString);
        }
        dirList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                dirList.setSummary((String) newValue);
                if (mListener != null) {
                    mListener.onStorgeLocationChanged((String) newValue);
                }
                return true;
            }
        });

        cleanList = (ListPreference) findPreference("OlderThan");
        cleanList.setSummary(cleanList.getEntry());
        cleanList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                cleanList.setValue((String) newValue);
                cleanList.setSummary(cleanList.getEntry());
                return false;//因为已经setValue所以返回false
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    //SDK API<23时，onAttach(Context)不执行，需要使用onAttach(Activity)。
    // Fragment自身的Bug，v4的Fragment没有此问题
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (activity instanceof OnFragmentInteractionListener) {
                mListener = (OnFragmentInteractionListener) activity;
            } else {
                throw new RuntimeException(activity.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onStorgeLocationChanged(String path);

        void onStorgeLocationInit(String path);
    }
}
