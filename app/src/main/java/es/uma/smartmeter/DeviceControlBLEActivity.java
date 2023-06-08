package es.uma.smartmeter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import es.uma.smartmeter.utils.FuncionesBackend;

public class DeviceControlBLEActivity extends AppCompatActivity {

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";


    private String mDeviceName;
    private String mDeviceAddress;

    private TextView tName;
    private TextView tAddress;


    private BluetoothLeService mBluetoothLeService;

    private Button bSendaData;
    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control_custom);

        Toast.makeText(this, "Smart meter encontrado =D", Toast.LENGTH_LONG).show();

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        tName = findViewById(R.id.tNameSmartMeter);
        tName.setText(mDeviceName);

        tAddress = findViewById(R.id.tAddressDispositivo);
        tAddress.setText(mDeviceAddress);

        this.spinner = findViewById(R.id.spinner);


        try {
            JSONArray json = new JSONArray(FuncionesBackend.getGetResponseGetHogares());
            //this.power.setText((String)(json.get(json.length()-1));
            JSONObject jsonObject;
            String[] items = new String[json.length()];
            System.out.println(FuncionesBackend.getGetResponseGetHogares());
            for (int i = 0; i < json.length(); i++) {
                jsonObject = ((JSONObject) json.get(0));
                items[i] = (String) jsonObject.get("nombre");
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, items);
            spinner.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        bSendaData = findViewById(R.id.bSendData);
        bSendaData.setOnClickListener(view -> {


            JSONArray json;
            try {
                json = new JSONArray(FuncionesBackend.getGetResponseGetHogares());
                int index = spinner.getSelectedItemPosition();
                JSONObject object = (JSONObject) json.get(index);
                FuncionesBackend.postInfo(tName.getText().toString(), object);
                Toast.makeText(getApplicationContext(), "Smart meter configurado", Toast.LENGTH_SHORT).show();

                while (FuncionesBackend.getTokenDispositivo() == null) {
                    System.out.println("Espera activa al token");
                }
                Intent gattServiceIntent = new Intent(getApplicationContext(), BluetoothLeService.class);
                bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

                //Intent i = new Intent(getApplicationContext(), MainActivity.class);
                //startActivity(i);

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }


        });


    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            System.out.println("Llega aqui");
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                //Log.e(TAG, "Unable to initialize Bluetooth");
                System.out.println("Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            System.out.println("Conexión a Bluetooth");
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }

    };


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}