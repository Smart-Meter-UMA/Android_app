package es.uma.smartmeter.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;

public interface BluetoothCharacteristicChangeListener {
    public void onCharacteristicChanged(String deviceAddress, BluetoothGattCharacteristic characteristic);
}
