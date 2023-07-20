package es.uma.smartmeter.bluetooth.operation;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class BluetoothWriteOperation extends BluetoothOperation {
    private final UUID service;
    private final UUID characteristic;
    private final String data;

    public BluetoothWriteOperation(BluetoothDevice device, UUID service, UUID characteristic, String data) {
        super(device);
        this.service = service;
        this.characteristic = characteristic;
        this.data = data;
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        Log.d("BLE", "writing to " + characteristic);
        BluetoothGattCharacteristic gattCharacteristic = gatt.getService(service).getCharacteristic(characteristic);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            gatt.writeCharacteristic(gattCharacteristic, data.getBytes(StandardCharsets.UTF_8), BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        } else {
            gattCharacteristic.setValue(data.getBytes(StandardCharsets.UTF_8));
            gatt.writeCharacteristic(gattCharacteristic);
        }
    }
}
