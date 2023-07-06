package es.uma.smartmeter;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import es.uma.smartmeter.utils.FuncionesBackend;
import es.uma.smartmeter.utils.NetworkManager;

public class MeasureActivity extends AppCompatActivity {
    public static final String TAG = "SmartMeter-Measure";

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


        NetworkManager.getInstance(this).newMeasurementsRequest(response -> {
            System.out.println("La response es " + response.toString());
            try {
                JSONObject jsonObject = ((JSONObject) response.get(response.length() - 1));
                System.out.println(jsonObject);

                this.power.setText(jsonObject.get("kw") + "KW");
                System.out.println(this.power.getText());

                SimpleDateFormat dfISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                SimpleDateFormat dfTxt = new SimpleDateFormat("dd/MM/yyyy");
                System.out.println("Lo que llega es " + jsonObject.get("fecha"));
                Date date = dfISO.parse(jsonObject.get("fecha").toString());
                System.out.println("Date: " + date);

                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                this.fecha.setText(dfTxt.format(date));

            } catch (JSONException | ParseException | NullPointerException e) {
                e.printStackTrace();
            }
        }, TAG);

        this.correo.setText(this.correo.getText().toString().replace("CORREO", FuncionesBackend.getEmailGoogle()));


    }

    @Override
    protected void onStop() {
        super.onStop();

        NetworkManager.getInstance(this).cancelAllRequests(TAG);
    }
}