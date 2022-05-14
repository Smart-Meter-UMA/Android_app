package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button bLogin;
    private Button bConnect;
    private Button bMeasure;
    private Button bGraficas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.bLogin=findViewById(R.id.bLoginSimulate);
        this.bConnect=findViewById(R.id.bConnect);
        this.bGraficas=findViewById(R.id.bMedidasGrafica);
        this.bMeasure=findViewById(R.id.bMedida);

        this.bGraficas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MeasuresGraphActivity.class);
                startActivity(i);
            }
        });

        this.bMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),MeasureActivity.class);
                startActivity(i);
            }
        });
        this.bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);

            }
        });

    }
}