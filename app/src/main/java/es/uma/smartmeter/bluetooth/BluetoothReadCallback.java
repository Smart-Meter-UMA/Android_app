package es.uma.smartmeter.bluetooth;

public interface BluetoothReadCallback {
    void call(byte[] characteristic);
}
