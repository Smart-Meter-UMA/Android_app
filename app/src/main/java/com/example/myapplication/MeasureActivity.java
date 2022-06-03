package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import classes.FuncionesBackend;

public class MeasureActivity extends AppCompatActivity {

    private TextView power;
    private TextView fecha;
    private TextView correo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        this.correo = findViewById(R.id.tCorreo);
        this.fecha = findViewById(R.id.tFecha);
        this.power = findViewById(R.id.tPower);

        // Falta asignar el valor a la potencia y a la fecha!!

        this.correo.setText(this.correo.getText().toString().replace("CORREO", FuncionesBackend.getEmailGoogle()));


    }
}