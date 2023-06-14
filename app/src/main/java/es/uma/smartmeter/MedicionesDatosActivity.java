package es.uma.smartmeter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import es.uma.smartmeter.utils.FuncionesBackend;

public class MedicionesDatosActivity extends AppCompatActivity {

    private TextView tMaximo;
    private TextView tMinimo;
    private TextView tMedia;
    private Button bFiltrar;
    private Spinner spMeses;
    private String mes;
    private int mesNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediciones_datos);

        this.mes = getIntent().getStringExtra("mes");
        System.out.println("El mes es " + mes);
        switch (mes) {
            case "ENERO":
                mesNumber = 0;
                break;
            case "FEBRERO":
                mesNumber = 1;
                break;
            case "MARZO":
                mesNumber = 2;
                break;
            case "ABRIL":
                mesNumber = 3;
                break;
            case "MAYO":
                mesNumber = 4;
                break;
            case "JUNIO":
                mesNumber = 5;
                break;
            case "JULIO":
                mesNumber = 6;
                break;
            case "AGOSTO":
                mesNumber = 7;
                break;
            case "SEPTIEMBRE":
                mesNumber = 8;
                break;
            case "OCTUBRE":
                mesNumber = 9;
                break;
            case "NOVIEMBRE":
                mesNumber = 10;
                break;
            case "DICIEMBRE":
                mesNumber = 11;
                break;
            default:
                mesNumber = -1;
                break;
        }


        /*
        Asigna a cada textView su vista, además de al botón Filtrar y el Spinner de los Meses
         */
        this.tMaximo = findViewById(R.id.tMaximo);
        this.tMinimo = findViewById(R.id.tMinimo);
        this.tMedia = findViewById(R.id.tMedia);
        this.bFiltrar = findViewById(R.id.bFiltrar);
        this.spMeses = findViewById(R.id.spMeses);

        String medidas = FuncionesBackend.getResponseGetMedidas();
        String[] items = new String[]{
                "TODOS",
                "ENERO",
                "FEBRERO",
                "MARZO",
                "ABRIL",
                "MAYO",
                "JUNIO",
                "JULIO",
                "AGOSTO",
                "SEPTIEMBRE",
                "OCTUBRE",
                "NOVIEMBRE",
                "DICIEMBRE"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, items);
        spMeses.setAdapter(adapter);

        try {
            JSONArray json = new JSONArray(medidas);

            if (!mes.equals("TODOS")) {
                System.out.println("Filtramos");
                json = filtraMes(json);
            }
            String maximo = calculaMax(json);
            String minimo = calculaMin(json);
            String media = calculaMedia(json);

            this.tMedia.setText(media);
            this.tMinimo.setText(minimo);
            this.tMaximo.setText(maximo);

            bFiltrar.setOnClickListener(view -> {
                Intent i = new Intent(getApplicationContext(), MedicionesDatosActivity.class);
                i.putExtra("mes", spMeses.getSelectedItem().toString());
                startActivity(i);
                finish();
            });

        } catch (JSONException | ParseException | NullPointerException e) {
            e.printStackTrace();
        }


    }

    private JSONArray filtraMes(JSONArray json) throws JSONException, ParseException {

        JSONArray res = new JSONArray();
        List<Integer> indices = new ArrayList<>();
        System.out.println("Tu json es " + json.toString());
        for (int i = 0; i < json.length(); i++) {
            System.out.println("i vale " + i);
            JSONObject jsonObject = json.getJSONObject(i);
            System.out.println("El object es " + jsonObject.toString());
            SimpleDateFormat dfISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date date = dfISO.parse(jsonObject.get("fecha").toString());
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int month = cal.get(Calendar.MONTH);
            System.out.println("El month es" + month);
            System.out.println("Nuestro month es" + this.mesNumber);
            if (month == this.mesNumber) {
                System.out.println("Es igual, add");
                // Tienes que buscar cual es el object a borrar
                res.put(jsonObject);
            }
            System.out.println("La lista tiene " + indices);
        }
        System.out.println("El json al final es " + res);
        return res;
    }


    /*
    Métodos para calcular la media el máximo y el mínimo.
     */
    private String calculaMedia(JSONArray json) throws JSONException {

        String media;

        double suma = 0;
        for (int i = 0; i < json.length(); i++) {
            JSONObject jsonObject = json.getJSONObject(i);
            suma = (double) jsonObject.get("kw") + suma;
        }
        if (json.length() != 0) {
            suma = suma / json.length();
            media = suma + " kw";
        } else {
            media = "No se ha podido calcular";
        }

        return media;

    }

    private String calculaMin(JSONArray json) throws JSONException {
        String minimo;
        double min = Float.MAX_VALUE;
        for (int i = 0; i < json.length(); i++) {
            JSONObject jsonObject = json.getJSONObject(i);
            if ((double) jsonObject.get("kw") < min) min = (double) jsonObject.get("kw");
        }
        if (min == (Float.MAX_VALUE)) {
            minimo = "No se ha podido obtener";
        } else {
            minimo = min + " kw";

        }
        return minimo;
    }

    private String calculaMax(JSONArray json) throws JSONException {
        String maximo;

        double max = Float.MIN_VALUE;
        for (int i = 0; i < json.length(); i++) {
            JSONObject jsonObject = json.getJSONObject(i);
            if ((double) jsonObject.get("kw") > max) max = (double) jsonObject.get("kw");
        }
        if (max == (Float.MIN_VALUE)) {
            maximo = "No se ha podido obtener";
        } else {
            maximo = max + " kw";
        }
        return maximo;

    }
}