package es.uma.smartmeter.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import es.uma.smartmeter.bluetooth.operation.BluetoothOperation;
import es.uma.smartmeter.bluetooth.operation.BluetoothReadOperation;

@SuppressLint("MissingPermission")
public class BluetoothManager {
    private static BluetoothManager instance;
    private final Context ctx;

    private final ConcurrentLinkedQueue<BluetoothOperation> queue;
    private final ConcurrentHashMap<String, BluetoothGatt> gatts;

    private BluetoothOperation currentOperation;

    private final BluetoothGattCallback callback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("BLE", "Gatt connected to device " + gatt.getDevice().getAddress());
                gatts.put(gatt.getDevice().getAddress(), gatt);
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("BLE", "Disconnected from gatt server " + gatt.getDevice().getAddress() + ", newState: " + newState);
                gatts.remove(gatt.getDevice().getAddress());
                setCurrentOperation(null);
                gatt.close();
                drive();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.d("BLE", "services discovered, status: " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                execute(gatt, currentOperation);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                ((BluetoothReadOperation) currentOperation).onRead(characteristic);
            }
            setCurrentOperation(null);
            drive();
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.d("BLE", "Characteristic " + characteristic.getUuid() + "written to on device " + gatt.getDevice().getAddress());
            setCurrentOperation(null);
            drive();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.e("BLE", "Characteristic " + characteristic.getUuid() + "was changed, device: " + gatt.getDevice().getAddress());
        }
    };

    private BluetoothManager(Context context) {
        // getApplicationContext() es clave, evita que se filtre la
        // Activity o BroadcastReceiver si alguien pasa uno de ellos.
        ctx = context.getApplicationContext();
        queue = new ConcurrentLinkedQueue<>();
        gatts = new ConcurrentHashMap<>();
        currentOperation = null;
    }

    public static synchronized BluetoothManager getInstance(Context context) {
        if (instance == null) {
            instance = new BluetoothManager(context);
        }
        return instance;
    }

    public synchronized void queue(BluetoothOperation operation) {
        queue.add(operation);
        Log.v("BLE", "Queueing Gatt operation, size will now become: " + queue.size());
        drive();
    }

    private synchronized void drive() {
        if(currentOperation != null) {
            Log.e("BLE", "tried to drive, but currentOperation was not null, " + currentOperation);
            return;
        }
        if(queue.size() == 0) {
            Log.v("BLE", "Queue empty, drive loop stopped.");
            currentOperation = null;
            return;
        }

        final BluetoothOperation operation = queue.poll();

        if (operation == null) {
            Log.e("BLE", "tried to drive, but queued operation was null");
            return;
        }

        Log.v("BLE", "Driving Gatt queue, size will now become: " + queue.size());
        setCurrentOperation(operation);

        // TODO: TIMEOUT

        final BluetoothDevice device = operation.getDevice();
        if(gatts.containsKey(device.getAddress())) {
            execute(gatts.get(device.getAddress()), operation);
        } else {
            device.connectGatt(ctx, false, callback);
        }
    }

    public synchronized void setCurrentOperation(BluetoothOperation currentOperation) {
        this.currentOperation = currentOperation;
    }

    private void execute(BluetoothGatt gatt, BluetoothOperation operation) {
        if (operation != currentOperation) {
            return;
        }
        operation.execute(gatt);
    }

    public BluetoothGatt getGatt(BluetoothDevice device) {
        return gatts.get(device.getAddress());
    }

    public void queue(List<BluetoothOperation> operations) {
        operations.forEach(this::queue);
    }
}
