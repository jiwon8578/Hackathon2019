package com.example.hackathon2019;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.skt.Tmap.TMapView;

public class MainActivity extends AppCompatActivity {

    LinearLayout linearLayoutTmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);
        TMapView tmapView = new TMapView(this);

        tmapView.setSKTMapApiKey("42856b74-3604-474f-ad72-df6d1b5aaca0");
        linearLayoutTmap.addView(tmapView);
    }
}
