package com.blackboxembedded.WunderLINQLauncher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private final static String TAG = "HomeActivity";

    PackageManager packageManager;
    private SharedPreferences sharedPrefs;
    public static List<AppInfo> apps;
    GridView grdView;
    public static ArrayAdapter<AppInfo> adapter;

    LinearLayout containAppDrawer;
    RelativeLayout ContainerHome;

    boolean floatBallLaunched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        containAppDrawer = (LinearLayout) findViewById(R.id.containAppDrawer);
        ContainerHome = (RelativeLayout) findViewById(R.id.ContainerHome);
        HideAppDrawer(false);
        apps = null;
        adapter = null;
        loadApps();
        loadListView();
        addGridListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!floatBallLaunched) {
            floatBallLaunched = true;
            Intent intent = packageManager.getLaunchIntentForPackage("com.huxq17.example.floatball");
            if (intent != null) {
                HomeActivity.this.startActivity(intent);
            }
        }
    }

    public void addGridListeners() {
        try {
            grdView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = packageManager.getLaunchIntentForPackage(apps.get(i).name.toString());
                    HomeActivity.this.startActivity(intent);
                }
            });
        } catch (Exception ex) {
            Toast.makeText(HomeActivity.this, ex.getMessage().toString() + " Grid", Toast.LENGTH_LONG).show();
            Log.e("Error Grid", ex.getMessage().toString() + " Grid");
        }
    }

    private void loadListView() {

        try {
            grdView = (GridView) findViewById(R.id.grd_allApps);
            if (adapter == null) {
                adapter = new ArrayAdapter<AppInfo>(this, R.layout.grd_items, apps) {

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {

                        ViewHolderItem viewHolder = null;

                        if (convertView == null) {
                            convertView = getLayoutInflater().inflate(
                                    R.layout.grd_items, parent, false
                            );
                            viewHolder = new ViewHolderItem();
                            viewHolder.icon = (ImageView) convertView.findViewById(R.id.img_icon);
                            viewHolder.label = (TextView) convertView.findViewById(R.id.txt_label);

                            convertView.setTag(viewHolder);
                        } else {
                            viewHolder = (ViewHolderItem) convertView.getTag();
                        }

                        AppInfo appInfo = apps.get(position);

                        if (appInfo != null) {
                            viewHolder.icon.setImageDrawable(appInfo.icon);
                            viewHolder.label.setText(appInfo.label);
                        }
                        return convertView;
                    }

                    final class ViewHolderItem {
                        ImageView icon;
                        TextView label;
                    }
                };
            }

            grdView.setAdapter(adapter);
        } catch (Exception ex) {
            Toast.makeText(HomeActivity.this, ex.getMessage().toString() + " loadListView", Toast.LENGTH_LONG).show();
            Log.e("Error loadListView", ex.getMessage().toString() + " loadListView");
        }

    }

    private void loadApps() {
        try {
            if (packageManager == null)
                packageManager = getPackageManager();
            if (apps == null) {
                apps = new ArrayList<AppInfo>();

                Intent i = new Intent(Intent.ACTION_MAIN, null);
                i.addCategory(Intent.CATEGORY_LAUNCHER);

                List<ResolveInfo> availableApps = packageManager.queryIntentActivities(i, 0);
                for (ResolveInfo ri : availableApps) {
                    AppInfo appinfo = new AppInfo();
                    appinfo.label = ri.loadLabel(packageManager);
                    appinfo.name = ri.activityInfo.packageName;
                    appinfo.icon = ri.activityInfo.loadIcon(packageManager);
                    Log.d("WLQL", "name: " + ri.activityInfo.packageName);
                    apps.add(appinfo);
                }
            }

        } catch (Exception ex) {
            Toast.makeText(HomeActivity.this, ex.getMessage().toString() + " loadApps", Toast.LENGTH_LONG).show();
            Log.e("Error loadApps", ex.getMessage().toString() + " loadApps");
        }
    }

    public void showApps(View v) {
        HideAppDrawer(true);
    }

    public void showWunderLINQ(View v) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.blackboxembedded.WunderLINQ");
        if (launchIntent != null) {
            startActivity(launchIntent);//null pointer check in case package name was not found
        }
    }

    public void showBT(View v) {
        startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
    }

    public void showStorage(View v) {

        String fileManager = sharedPrefs.getString("filemanager_key", "Default");
        if (fileManager.equals("Default")) {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.mediatek.filemanager");
            if (launchIntent != null) {
                startActivity(launchIntent);//null pointer check in case package name was not found
            } else {
                Log.d(TAG,"Filemanger: PROBLEM Launching Mediatek File Manager");
            }
        } else {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(fileManager);
            if (launchIntent != null) {
                startActivity(launchIntent);//null pointer check in case package name was not found
            } else {
                Log.d(TAG,"Filemanger: PROBLEM Launching selected File Manager");
            }
        }
    }

    public void showAppSettings(View v) {
        Intent settingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    public void showAndroidSettings(View v) {
        startActivity(new Intent(Settings.ACTION_SETTINGS));
    }

    public void HideAppDrawer(Boolean visibility) {
        if (visibility) {
            containAppDrawer.setVisibility(View.VISIBLE);
            ContainerHome.setVisibility(View.GONE);
        } else {
            containAppDrawer.setVisibility(View.GONE);
            ContainerHome.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
            HideAppDrawer(false);
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        HideAppDrawer(false);
    }

}
