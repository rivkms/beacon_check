package com.example.paran;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public class MainActivity extends AppCompatActivity implements AutoPermissionsListener {

    TextView textView;

    private BeaconManager beaconManager;

    String beaconUUID = "c4734b66-203c-4b2b-a2bb-4d794d14ec62"; // beacon -uuid

    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.tv_message);

        AutoPermissions.Companion.loadAllPermissions(this, 101); // AutoPermissions

        beaconManager = BeaconManager.getInstanceForApplication(this);
        //beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                //if (!beacons.isEmpty()) {
                if (beacons.size()>0) {
                    Beacon beacon = beacons.iterator().next();
                    Log.i(TAG, "The first beacon I see is about " + beacon.getDistance() + " meters away.");
                }else{
                    Log.i(TAG, "Nothing Seen.");
                }
            }
        });

        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw a beacon for the first time!");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "didEnterRegion - 비콘 연결됨", Toast.LENGTH_SHORT).show();
                        textView.setText("Beacon connected");
                    }
                });
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see a beacon");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "didExitRegion - 비콘 연결 끊김", Toast.LENGTH_SHORT).show();
                        textView.setText("Beacon disconnected");
                    }
                });
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: " + state);
            }
        });

        try {
            beaconManager.startMonitoring(new Region("beacon", Identifier.parse(beaconUUID), null, null));
            beaconManager.startRangingBeacons(new Region("beacon", Identifier.parse(beaconUUID), null, null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }// onCreate()..

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDenied(int i, String[] strings) {
    }

    @Override
    public void onGranted(int i, String[] strings) {
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
}// MainActivity class..