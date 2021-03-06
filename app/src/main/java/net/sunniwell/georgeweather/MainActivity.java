package net.sunniwell.georgeweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "jpd-AppCompatActivity";
    private void fileTest() {
        File file = new File("/data/data/net.sunniwell.georgeweather/shared_prefs");
        if (file.exists()) {
            Log.d(TAG, "fileTest: file exist..");
            File[] files = file.listFiles();
            for(File f : files) {
                Log.d(TAG, "fileTest: name:" + f.getName());
            }
        } else {
            Log.d(TAG, "fileTest: not exist.");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");
//        fileTest();
        SQLiteDatabase database = Connector.getDatabase();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if(pref.getString("weather", null) != null) {
            Log.d(TAG, "onCreate: alreadyChooseCity");
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
