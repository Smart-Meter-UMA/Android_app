package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import classes.FuncionesBackend;

public class MedicionesDatosActivity extends AppCompatActivity {

    private TextView tMaximo;
    private TextView tMinimo;
    private TextView tMedia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediciones_datos);

        this.tMaximo=findViewById(R.id.tMaximo);
        this.tMinimo=findViewById(R.id.tMinimo);
        this.tMedia=findViewById(R.id.tMedia);

        String medidas = FuncionesBackend.getResponseGetMedidas();

        try {
            JSONArray json = new JSONArray(medidas);

            String maximo = calculaMax(json);
            String minimo = calculaMin( json);
            String media = calculaMedia(json);

            this.tMedia.setText(media+" kw");
            this.tMinimo.setText(minimo+" kw");
            this.tMaximo.setText(maximo + " kw");




        } catch (JSONException  e) {
            e.printStackTrace();
        }



    }

    private String calculaMedia(JSONArray json) throws JSONException {

        String media = "0";

        double suma = 0;
        for(int i=0; i<json.length();i++){
            JSONObject jsonObject = json.getJSONObject(i);
            suma =(double) jsonObject.get("kw") + suma;
        }
        suma = suma/json.length();
        media = String.valueOf(suma);

        return media;

    }

    private String calculaMin(JSONArray json) throws JSONException {
        String minimo = "0";
        double min = Float.MAX_VALUE;
        for(int i=0; i<json.length();i++){
            JSONObject jsonObject = json.getJSONObject(i);
            if((double)jsonObject.get("kw") < min) min= (double)jsonObject.get("kw");
        }

        minimo= String.valueOf(min);
        return minimo;
    }

    private String calculaMax(JSONArray json) throws JSONException {
        String maximo = "0";

        double max = Float.MIN_VALUE;
        for(int i=0; i<json.length();i++){
            JSONObject jsonObject = json.getJSONObject(i);
            if((double)jsonObject.get("kw") > max) max= (double)jsonObject.get("kw");
        }

        maximo= String.valueOf(max);
        return maximo;

    }
}