package es.uma.smartmeter.list;


import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.uma.smartmeter.DeviceControlBLEActivity;
import es.uma.smartmeter.R;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

    private final List<DeviceListItem> mDeviceList;
    private final View.OnClickListener mListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView deviceType;
        private final TextView deviceName;
        private final TextView deviceAddress;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            //Log.d(TAG, "Element " + getAdapterPosition() + " clicked."));
            deviceType = v.findViewById(R.id.device_type);
            deviceName = v.findViewById(R.id.device_name);
            deviceAddress = v.findViewById(R.id.device_address);
        }

        public View getView() {
            return itemView;
        }

        public ImageView getDeviceType() {
            return deviceType;
        }

        public TextView getDeviceName() {
            return deviceName;
        }

        public TextView getDeviceAddress() {
            return deviceAddress;
        }
    }

    public DeviceListAdapter(List<DeviceListItem> deviceList, View.OnClickListener listener) {
        mDeviceList = deviceList;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        BluetoothDevice device = mDeviceList.get(position).getDevice();
        final String deviceName = device.getName();
        if (!TextUtils.isEmpty(deviceName)) {
            holder.getDeviceName().setText(deviceName);
        } else {
            holder.getDeviceName().setText(R.string.unknown_device);
        }
        holder.getDeviceAddress().setText(device.getAddress());

        if (mDeviceList.get(position).getAppearance() == 0x0557) {
            holder.getDeviceType().setImageResource(R.drawable.smart_meter_icon);
        } else {
            holder.getDeviceType().setImageResource(R.drawable.unknown_device_icon);
        }

        holder.getView().setOnClickListener(v1 -> {
            final BluetoothDevice bluetoothDevice = mDeviceList.get(position).getDevice();
            if (bluetoothDevice == null) return;
            final Intent intent = new Intent(v1.getContext(), DeviceControlBLEActivity.class);
            intent.putExtra(DeviceControlBLEActivity.EXTRAS_DEVICE_NAME, bluetoothDevice.getName());
            intent.putExtra(DeviceControlBLEActivity.EXTRAS_DEVICE_ADDRESS, bluetoothDevice.getAddress());
            mListener.onClick(v1);
            v1.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mDeviceList.size();
    }

    public void addDevice(BluetoothDevice device, byte[] scanRecord) {
        DeviceListItem item = new DeviceListItem(device);
        if (!mDeviceList.contains(item)) {
            item.obtainAppearance(scanRecord);
            mDeviceList.add(item);
            notifyItemInserted(mDeviceList.size() - 1);
        }
    }

    public void clear() {
        int count = mDeviceList.size();
        mDeviceList.clear();
        notifyItemRangeRemoved(0,  count);
    }
}