package com.example.hackathon2019;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NameActivity extends AppCompatActivity {

    Button button;
    EditText name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        button = (Button)findViewById(R.id.button);
        name = (EditText)findViewById(R.id.name);

        name.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                name.getText().clear();
                return false;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("name", name.getText().toString());
                Intent intent = new Intent(NameActivity.this, MainActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

    }
}
