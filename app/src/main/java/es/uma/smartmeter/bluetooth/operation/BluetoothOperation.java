package es.uma.smartmeter.bluetooth.operation;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

public abstract class BluetoothOperation {
    private final BluetoothDevice device;

    public BluetoothOperation(BluetoothDevice device) {
        this.device = device;
    }

    public abstract void execute(BluetoothGatt gatt);

    public BluetoothDevice getDevice() {
        return device;
    }
}
