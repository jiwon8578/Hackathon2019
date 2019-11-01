package com.example.hackathon2019;


import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private Socket socket;
    BufferedReader in;
    PrintWriter out;
    EditText input;
    Button button;
    TextView output;
    String data;

    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");

        input = (EditText) findViewById(R.id.input);
        button = (Button) findViewById(R.id.button);
        output = (TextView) findViewById(R.id.output);

        StrictMode.enableDefaults();

        input.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                input.getText().clear();
                return false;
            }
        });

        Thread worker = new Thread() {
            public void run() {
                try {
                    socket = new Socket(" 10.101.10.39", 7777);
                    out = new PrintWriter(socket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(
                            socket.getInputStream()));
                    out.print(name + "\n");
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                /*try {
                    while (true) {
                        data = in.readLine();
                        output.post(new Runnable() {
                            @Override
                            public void run() {
                                output.append("\n" + data);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

                while (true) {
                    try {
                        data = in.readLine();
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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        String data = input.getText().toString();
                        Log.w("NETWORK", "" + data);
                        if (data != null) {
                            out.print(data + "\n");
                            out.flush();
                        }

                    }
                }.start();
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
