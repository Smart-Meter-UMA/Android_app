package es.uma.smartmeter.list;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import androidx.annotation.Nullable;

public class DeviceListItem {
    private final BluetoothDevice device;
    private short appearance;

    public DeviceListItem(BluetoothDevice device){
        this.device = device;
        this.appearance = 0;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public short getAppearance() {
        return appearance;
    }

    public void obtainAppearance(byte[] scanRecord) {
        int offset = 0;
        while (offset < (scanRecord.length - 2)) {
            int len = scanRecord[offset++];
            if (len == 0)
                break;

            int type = scanRecord[offset++];
            if (type == 0x19 && len == 3) { // Appearance
                appearance = (short) (scanRecord[offset++] & 0xFF);
                appearance |= (scanRecord[offset++] << 8);
                Log.i("BLE", "Received Appearance: " + appearance);
                break;
            } else {
                offset += (len - 1);
            }
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof DeviceListItem && device.equals(((DeviceListItem) obj).device);
    }
}
