package es.uma.smartmeter;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import es.uma.smartmeter.utils.FuncionesBackend;
import es.uma.smartmeter.utils.NetworkManager;

public class DeviceControlBLEActivity extends AppCompatActivity {

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String TAG = "SmartMeter-DeviceControlBLE";

    private String mDeviceName;
    private String mDeviceAddress;

    private TextView tName;
    private TextView tAddress;

    private BluetoothLeService mBluetoothLeService;
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

        NetworkManager.getInstance(getApplicationContext()).newHomesRequest(response -> {
            try {
                String[] items = new String[response.length()];
                for (int i = 0; i < response.length(); i++) {
                    items[i] = ((JSONObject) response.get(0)).getString("nombre");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, items);
                spinner.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, TAG);

        bSendaData = findViewById(R.id.bSendData);
        bSendaData.setOnClickListener(view -> NetworkManager.getInstance(getApplicationContext()).newHomesRequest(response -> {
            try {
                int index = spinner.getSelectedItemPosition();
                JSONObject object = (JSONObject) response.get(index);
                NetworkManager.getInstance(this).newRegisterDeviceRequest(tName.getText().toString(), object, res -> {
                    try {
                        FuncionesBackend.setTokenDispositivo(res.getString("token"));
                        Toast.makeText(getApplicationContext(), "Smart meter configurado", Toast.LENGTH_SHORT).show();

                        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
                        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, TAG);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, TAG));
    }

    @Override
    protected void onStop() {
        super.onStop();

        NetworkManager.getInstance(this).cancelAllRequests(TAG);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}