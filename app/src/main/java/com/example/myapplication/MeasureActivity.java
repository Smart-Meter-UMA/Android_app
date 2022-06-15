package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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


        System.out.println("La response es "+FuncionesBackend.getResponseGetMedidas());
        try {
            JSONArray json = new JSONArray(FuncionesBackend.getResponseGetMedidas());
            //this.power.setText((String)(json.get(json.length()-1));
            JSONObject jsonObject= ((JSONObject) json.get(json.length()-1));
            System.out.println(jsonObject);
            this.power.setText(jsonObject.get("kw").toString() + "KW");
            System.out.println(this.power.getText());

            SimpleDateFormat dfISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            SimpleDateFormat dfTxt = new SimpleDateFormat("dd/MM/yyyy");
            System.out.println("Lo que llega es "+ jsonObject.get("fecha").toString());
            Date date= dfISO.parse(jsonObject.get("fecha").toString());
            System.out.println("Date: "+ date);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int month = cal.get(Calendar.MONTH);


            /*
            try {
                date = df.parse(jsonObject.get("fecha").toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
             */

            this.fecha.setText(dfTxt.format(date));

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

        // Falta asignar el valor a la potencia y a la fecha!!

        this.correo.setText(this.correo.getText().toString().replace("CORREO", FuncionesBackend.getEmailGoogle()));


    }
}