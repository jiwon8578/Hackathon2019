package com.example.hackathon2019;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private Socket socket;
    BufferedReader in;
    PrintWriter out;
    Button share;
    Button button;
    TextView output;
    String data;

    String name;


    static double curLat;
    static double curLng;

    String[] loc;
    String lat;
    String lng;
    Double dlat;
    Double dlng;
    Double totallat = 0.0;
    Double totallng = 0.0;
    int count = 0;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //위치 퍼미션
        if(!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }

        GpsTracker gpsTracker = new GpsTracker(MainActivity.this);
        curLat = gpsTracker.getLatitude();
        curLng = gpsTracker.getLongitude();

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");

        share = (Button) findViewById(R.id.shareButton);
        button = (Button) findViewById(R.id.button);
        output = (TextView) findViewById(R.id.output);

        StrictMode.enableDefaults();


        //curLat = 37.5443003;
        //curLng = 126.9722328;

        share.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

            }
        });

        Thread worker = new Thread() {
            public void run() {
                try {
                    socket = new Socket("172.20.10.2", 5555);
                    out = new PrintWriter(socket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(
                            socket.getInputStream()));
                    out.print(name+"\n");
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                while (true) {
                    try {
                        data = in.readLine();
                        loc = data.split(">>");
                        lat = loc[1].split(",")[0];
                        lng = loc[1].split(",")[1];
                        dlat = Double.parseDouble(lat);
                        dlng = Double.parseDouble(lng);
                        totallat += dlat;
                        totallng += dlng;
                        count++;

                            output.post(new Runnable() {
                                @Override
                                public void run() {
                                    output.append("\n" + data);

                                }
                            });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        worker.start();

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        String data = String.valueOf(curLat) + "," + String.valueOf(curLng);
                        Log.w("NETWORK", "" + data);
                        if (data != null) {
                            out.print(data + "\n");
                            out.flush();
                        }

                    }
                }.start();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude", totallat/count);
                bundle.putDouble("longitude", totallng/count);
                Intent intent = new Intent(MainActivity.this, MapPoi.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                //Toast.makeText(getApplicationContext(), String.valueOf(totallat), Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), String.valueOf(totallng), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    //참고한 사이트: https://webnautes.tistory.com/1315
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length) { //요청 코드가 PERMISSONS_REQUEST_CODE이고, 요청한 퍼시면 개수만큼 수신되었다면
            boolean check_result = true;
            for(int result : grantResults) {
                if(result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if(check_result) {
            } else {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0]) || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정에서 퍼미션을 허용해야 합니다.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    void checkRunTimePermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
