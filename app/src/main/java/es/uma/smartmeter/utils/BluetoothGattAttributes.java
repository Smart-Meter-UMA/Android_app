package es.uma.smartmeter.utils;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class BluetoothGattAttributes {
    private static final HashMap<String, String> attributes = new HashMap<>();

    public static String UUID_SERVICE = "534d4d54-d32a-11ec-9d64-0242ac120002";
    public static String UUID_INTENSITY = "49303030-d32a-11ec-9d64-0242ac120002";
    public static String UUID_VOLTAGE = "56303030-d32a-11ec-9d64-0242ac120002";
    public static String UUID_POWER = "50303030-d32a-11ec-9d64-0242ac120002";
    public static String UUID_INTERVAL = "4956414c-d32a-11ec-9d64-0242ac120002";
    public static String UUID_TOKEN = "544f4b4e-d32a-11ec-9d64-0242ac120002";
    public static String UUID_SSID = "6bfe5343-d32a-11ec-9d64-0242ac120002";
    public static String UUID_PASSWORD = "760a51b2-d32a-11ec-9d64-0242ac120002";
    public static String UUID_STATUS = "8bdb959e-d32a-11ec-9d64-0242ac120002";

    static {
        // Sample Services.
        attributes.put("00001800-0000-1000-8000-00805f9b34fb", "Generic Access Service");
        attributes.put("00001801-0000-1000-8000-00805f9b34fb", "Generic Attribute");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180f-0000-1000-8000-00805f9b34fb", "Battery Service");
        // Default Characteristics.
        attributes.put("00002a00-0000-1000-8000-00805f9b34fb", "Device Name");
        attributes.put("00002a01-0000-1000-8000-00805f9b34fb", "Appearance");
        attributes.put("00002a02-0000-1000-8000-00805f9b34fb", "Peripheral Privacy Flag");
        attributes.put("00002a04-0000-1000-8000-00805f9b34fb", "Peripheral Preferred Connection Parameters");
        attributes.put("00002a05-0000-1000-8000-00805f9b34fb", "Service Change");
        attributes.put("00002a19-0000-1000-8000-00805f9b34fb", "Battery Level");
        attributes.put("00002a23-0000-1000-8000-00805f9b34fb", "System ID");
        attributes.put("00002a24-0000-1000-8000-00805f9b34fb", "Model Number String");
        attributes.put("00002a25-0000-1000-8000-00805f9b34fb", "Serial Number String");
        attributes.put("00002a26-0000-1000-8000-00805f9b34fb", "Firmware Revision String");
        attributes.put("00002a27-0000-1000-8000-00805f9b34fb", "Hardware Revision String");
        attributes.put("00002a28-0000-1000-8000-00805f9b34fb", "Software Revision String");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
        attributes.put("00002a50-0000-1000-8000-00805f9b34fb", "PnP ID");
        // Vendor Services.
        attributes.put(UUID_SERVICE, "Smart Meter UMA Service");
        // Vendor Characteristics.
        attributes.put(UUID_INTENSITY, "Measured Intensity");
        attributes.put(UUID_VOLTAGE, "Measured Voltage");
        attributes.put(UUID_POWER, "Measured Power");
        attributes.put(UUID_INTERVAL, "Measuring Interval");
        attributes.put(UUID_TOKEN, "Device Token");
        attributes.put(UUID_SSID, "WiFi SSID");
        attributes.put(UUID_PASSWORD, "WiFi Password");
        attributes.put(UUID_STATUS, "WiFi Status");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
