package es.uma.smartmeter;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import es.uma.smartmeter.list.DeviceListAdapter;
import es.uma.smartmeter.utils.BluetoothGattAttributes;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
@SuppressWarnings("MissingPermission")
public class DeviceScanActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_SCAN_BT = 101;
    // Stops scanning after 4 seconds.
    private static final long SCAN_PERIOD = 4000;
    private RecyclerView mDeviceList;
    private TextView mScanText;
    private ImageView mBluetoothCircle;
    private DeviceListAdapter mDeviceListAdapter;
    // Device scan callback.
    private final ScanCallback mCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            System.out.println("Llega aqui");

            if (result == null || result.getDevice() == null || TextUtils.isEmpty(result.getDevice().getName()))
                return;

            runOnUiThread(() -> {
                mDeviceListAdapter.addDevice(result.getDevice(), result.getScanRecord().getBytes());
            });
        }
    };
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private final Runnable mAnimation = new Runnable() {
        @Override
        public void run() {
            mBluetoothCircle.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(1000).withEndAction(() -> {
                mBluetoothCircle.setScaleX(1f);
                mBluetoothCircle.setScaleY(1f);
                mBluetoothCircle.setAlpha(1f);
            });
            mHandler.postDelayed(this, 1250);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);

        mHandler = new Handler();

        mDeviceList = findViewById(R.id.deviceList);
        mDeviceList.setHasFixedSize(false);
        mDeviceList.setContextClickable(true);
        mDeviceList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mDeviceList.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.scanAction).setOnClickListener(view -> {
            if (!mScanning) {
                mDeviceListAdapter.clear();
                scanLeDevice(true);
            } else {
                scanLeDevice(false);
            }
        });

        mScanText = findViewById(R.id.scanText);
        mBluetoothCircle = findViewById(R.id.bluetooth_circle);

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.error_ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build());

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.BLUETOOTH, android.Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_SCAN_BT);
            }
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.BLUETOOTH, android.Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_SCAN_BT);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_SCAN_BT);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_SCAN_BT) {
            Arrays.stream(grantResults).forEach(result -> {
                if (result == RESULT_CANCELED) {
                    finish();
                }
            });

            startActivity(getIntent());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        // Initializes list view adapter.
        mDeviceListAdapter = new DeviceListAdapter(new ArrayList<>(), v -> {
            if (mScanning) {
                mBluetoothAdapter.getBluetoothLeScanner().stopScan(mCallback);
                mHandler.removeCallbacks(mAnimation);
                mScanning = false;
            }
            finish();
        });
        mDeviceList.setAdapter(mDeviceListAdapter);
        scanLeDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mDeviceListAdapter.clear();
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            ScanFilter filter = new ScanFilter.Builder().setServiceUuid(new ParcelUuid(BluetoothGattAttributes.UUID_SERVICE)).build();
            ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build();
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(() -> {
                mScanning = false;
                mScanText.setText(R.string.rescan);
                mBluetoothAdapter.getBluetoothLeScanner().stopScan(mCallback);
                mHandler.removeCallbacks(mAnimation);
            }, SCAN_PERIOD);

            mScanning = true;
            mScanText.setText(R.string.scanning);
            mBluetoothAdapter.getBluetoothLeScanner().startScan(Collections.singletonList(filter), settings, mCallback);
            mAnimation.run();
        } else {
            mScanning = false;
            mScanText.setText(R.string.rescan);
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(mCallback);
            mHandler.removeCallbacks(mAnimation);
        }
    }
}