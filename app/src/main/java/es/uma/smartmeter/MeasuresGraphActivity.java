package es.uma.smartmeter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import es.uma.smartmeter.utils.FuncionesBackend;
import es.uma.smartmeter.utils.NetworkManager;

public class MeasuresGraphActivity extends AppCompatActivity {
    public static final String TAG = "SmartMeter-MeasuresGraph";

    private GraphView graphView;
    private Button bDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measures_graph);
        /*
        El botón bDatos lanza la actividad MedicionesDatosActivity, que muestra parámetros como el máximo, mínimo y la media.
         */
        this.bDatos = findViewById(R.id.bDatos);
        this.bDatos.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), MedicionesDatosActivity.class);
            i.putExtra("mes", "TODOS");
            startActivity(i);
        });
        graphView = findViewById(R.id.graphView);
        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    Format formatter = new SimpleDateFormat("dd/MM");
                    return formatter.format(new Date((long) value));
                }
                return super.formatLabel(value, isValueX);
            }
        });
        graphView.getGridLabelRenderer().setTextSize(28);


        addData();


        graphView.setTitle(" \n Lista de mediciones históricas para " + FuncionesBackend.getEmailGoogle());
        graphView.setTitleTextSize(28);

    }

    @Override
    protected void onStop() {
        super.onStop();

        NetworkManager.getInstance(this).cancelAllRequests(TAG);
    }

    /*
    NO ENTIENDO QUÉ HACE, ESPERO QUE FUNCIONE
     */
    private void addData() {
        NetworkManager.getInstance(getApplicationContext()).newMeasurementsRequest(response -> {
            try {
                System.out.println("Medidas: " + response.toString());
                DataPoint[] array = new DataPoint[5];
                int j = 0;
                for (int i = response.length() - 1; i > response.length() - 6; i--) {
                    JSONObject jsonObject = ((JSONObject) response.get(i));
                    double d = jsonObject.getDouble("kw");

                    SimpleDateFormat dfISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    SimpleDateFormat dfTxt = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = dfISO.parse(jsonObject.get("fecha").toString());
                    System.out.println(date.getTime());

                    Date dateFinal = new Date(date.getTime());
                    System.out.println(dateFinal);
                    System.out.println("Los valores son " + dateFinal + " : " + d);
                    DataPoint dt = new DataPoint(dateFinal, d);
                    array[j] = dt;
                    j++;
                }

                showArray(array);
                List<DataPoint> list = Arrays.asList(array);
                System.out.println(list);
                Collections.reverse(list);
                System.out.println(list);
                array = list.toArray(array);
                System.out.println("Length" + array.length);
                showArray(array);
                LineGraphSeries<DataPoint> points = new LineGraphSeries<>(array);
                graphView.addSeries(points);
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }, TAG);
    }

    private void showArray(DataPoint[] array) {
        System.out.println("Length " + array.length);
        for (int i = 0; i < 5; i++) {
            System.out.print("DataPointX: " + array[i].getX() + ";");
            System.out.println("DataPointY: " + array[i].getY());
        }
    }

}