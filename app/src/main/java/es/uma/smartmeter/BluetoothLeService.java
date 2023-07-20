package es.uma.smartmeter;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import es.uma.smartmeter.bluetooth.operation.BluetoothDisconnectOperation;
import es.uma.smartmeter.bluetooth.operation.BluetoothOperation;
import es.uma.smartmeter.bluetooth.operation.BluetoothWriteOperation;
import es.uma.smartmeter.utils.FuncionesBackend;
import es.uma.smartmeter.utils.BluetoothGattAttributes;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();
    private final IBinder mBinder = new LocalBinder();
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /*@Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }*/

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        System.out.println("LLega initialize");
        return true;
    }

    public boolean sendData(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        List<BluetoothOperation> operations = new ArrayList<>();
        operations.add(new BluetoothWriteOperation(device, BluetoothGattAttributes.UUID_SERVICE, BluetoothGattAttributes.UUID_TOKEN, FuncionesBackend.getTokenDispositivo()));
        operations.add(new BluetoothWriteOperation(device, BluetoothGattAttributes.UUID_SERVICE, BluetoothGattAttributes.UUID_SSID, FuncionesBackend.getWifi(getApplicationContext())));
        operations.add(new BluetoothWriteOperation(device, BluetoothGattAttributes.UUID_SERVICE, BluetoothGattAttributes.UUID_PASSWORD, FuncionesBackend.getPassword()));
        operations.add(new BluetoothDisconnectOperation(device));
        es.uma.smartmeter.bluetooth.BluetoothManager.getInstance(this).queue(operations);
        Log.d(TAG, "Trying to send data.");
        return true;
    }

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }
}
