package es.uma.smartmeter.bluetooth.operation;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import java.util.UUID;

import es.uma.smartmeter.bluetooth.BluetoothReadCallback;

@SuppressLint("MissingPermission")
public class BluetoothReadOperation extends BluetoothOperation {
    private final UUID service;
    private final UUID characteristic;
    private final BluetoothReadCallback callback;

    public BluetoothReadOperation(BluetoothDevice device, UUID service, UUID characteristic, BluetoothReadCallback callback) {
        super(device);
        this.service = service;
        this.characteristic = characteristic;
        this.callback = callback;
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        Log.d("BLE", "writing to " + characteristic);
        BluetoothGattCharacteristic gattCharacteristic = gatt.getService(service).getCharacteristic(characteristic);
        gatt.readCharacteristic(gattCharacteristic);
    }

    public void onRead(BluetoothGattCharacteristic gattCharacteristic) {
        callback.call(gattCharacteristic.getValue());
    }
}
