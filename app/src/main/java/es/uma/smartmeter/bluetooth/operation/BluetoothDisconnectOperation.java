package es.uma.smartmeter.bluetooth.operation;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

@SuppressLint("MissingPermission")
public class BluetoothDisconnectOperation extends BluetoothOperation {

    public BluetoothDisconnectOperation(BluetoothDevice device) {
        super(device);
    }

    @Override
    public void execute(BluetoothGatt gatt) {
        gatt.disconnect();
    }
}
