/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.uma.smartmeter;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
// TODO: Cambiar la ListActivity por RecyclerView o algo más eficiente.
public class DeviceScanActivity extends ListActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private final Toast toast = null;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    // Device scan callback.
    private final BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    System.out.println("Llega aqui");

                    runOnUiThread(() -> {
                        System.out.println("El device es" + device.getName());
                        mLeDeviceListAdapter.addDevice(device);
                    });
                }
            };
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    public void hideToast() {
        if (this.toast != null)
            this.toast.cancel();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        Toast.makeText(this, "Buscando smart meter", Toast.LENGTH_SHORT).show();
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            finish();
        }

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build());

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
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
        mLeDeviceListAdapter.clear();
    }

    private void scanLeDevice(final boolean enable) {

        final ScanCallback scanCallback = new ScanCallback() {
            private boolean load = false;

            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                BluetoothDevice btDevice = result.getDevice();
                if (btDevice.getName() != null) {
                    if (btDevice.getName().equals("SmartMeter") && !load) {
                        System.out.println("Dispositivo " + btDevice.getName());
                        load = true;
                        final Intent intent = new Intent(getApplicationContext(), DeviceControlBLEActivity.class);
                        intent.putExtra(DeviceControlBLEActivity.EXTRAS_DEVICE_NAME, btDevice.getName());
                        intent.putExtra(DeviceControlBLEActivity.EXTRAS_DEVICE_ADDRESS, btDevice.getAddress());
                        startActivity(intent);
                        hideToast();
                    }

                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);

                System.out.println("BLE// onBatchScanResults");
                for (ScanResult sr : results) {
                    Log.i("ScanResult - Results", sr.toString());
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);

                System.out.println("BLE// onScanFailed");
                Log.e("Scan Failed", "Error Code: " + errorCode);
            }
        };

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(() -> {
                mScanning = false;
                //mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mBluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
                invalidateOptionsMenu();
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
            System.out.println("Llega al scan");
        } else {
            mScanning = false;
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
        }
        invalidateOptionsMenu();
    }

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter {
        private final ArrayList<BluetoothDevice> mLeDevices;
        private final LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<>();
            mInflator = DeviceScanActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }
    }
}