package com.blackboxembedded.WunderLINQLauncher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends PreferenceActivity{

    private final static String TAG = "SettingsActivity";

    private static PackageManager packageManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (packageManager == null)
            packageManager = getPackageManager();

        getFragmentManager().beginTransaction().replace(android.R.id.content, new UserSettingActivityFragment()).commit();
    }

    public static class UserSettingActivityFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
    {
        @Override
        public void onStop() {
            super.onStop();
        }

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            Preference versionField = findPreference("version_key");
            versionField.setSummary(BuildConfig.VERSION_NAME);

            Preference company = findPreference("company_key");
            company.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String url = getString(R.string.company_url);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    return false;
                }
            });

            Preference wunderlinq = findPreference("wunderlinq_key");
            wunderlinq.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String url = getString(R.string.company_url);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    return false;
                }
            });

            final ListPreference fileManagerListPreference = (ListPreference) findPreference("filemanager_key");
            // THIS IS REQUIRED IF YOU DON'T HAVE 'entries' and 'entryValues' in your XML
            setListPreferenceData(fileManagerListPreference);

            fileManagerListPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    setListPreferenceData(fileManagerListPreference);
                    return false;
                }
            });
            fileManagerListPreference.setSummary(fileManagerListPreference.getEntry());
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            final ListPreference fileManagerListPreference = (ListPreference) findPreference("filemanager_key");
            fileManagerListPreference.setSummary(fileManagerListPreference.getEntry());
        }

        protected static void setListPreferenceData(ListPreference lp) {
            List<CharSequence> appLabels = new ArrayList<CharSequence>();
            List<CharSequence> appNames = new ArrayList<CharSequence>();
            appLabels.add("Default");
            appNames.add("Default");
            Intent i = new Intent(Intent.ACTION_MAIN, null);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> availableApps = packageManager.queryIntentActivities(i, 0);
            for (ResolveInfo ri : availableApps) {
                AppInfo appinfo = new AppInfo();
                appinfo.label = ri.loadLabel(packageManager);
                appinfo.name = ri.activityInfo.packageName;
                Log.d("WLQL", "name: " + ri.activityInfo.packageName);
                appLabels.add(ri.loadLabel(packageManager));
                appNames.add(ri.activityInfo.packageName);
            }

            CharSequence[] entries = appLabels.toArray(new CharSequence[appLabels.size()]);
            CharSequence[] entryValues = appNames.toArray(new CharSequence[appNames.size()]);
            lp.setEntries(entries);
            lp.setDefaultValue("Default");
            lp.setEntryValues(entryValues);
        }
    }

}
