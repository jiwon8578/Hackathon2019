package com.example.hackathon2019;


import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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


    double curLat;
    double curLng;

    String[] loc;
    String lat;
    String lng;
    Double dlat;
    Double dlng;
    Double totallat = 0.0;
    Double totallng = 0.0;
    int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");

        share = (Button) findViewById(R.id.shareButton);
        button = (Button) findViewById(R.id.button);
        output = (TextView) findViewById(R.id.output);

        StrictMode.enableDefaults();


        curLat = 37.5443003;
        curLng = 126.9722328;

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
}
