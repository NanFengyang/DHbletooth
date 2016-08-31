package com.yyt.blue.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by yangyoutao on 2016/8/29.
 */
public abstract class BleToothCallBack {
    /**
     * 超时
     */
    void onTimeout(int type) {

    }

    /**
     * Failure
     */
    void onFailure(String error) {

    }

    /**
     * 查找蓝牙设备
     */
    void onScanBleTooth(BluetoothDevice device, int rssi, byte[] scanRecord) {

    }

    /**
     * 设备连接成功
     */
    void onConnectSuccess(BluetoothDevice device,BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic Characteristic) {

    }

    /**
     * 接受设备的值
     *
     * @param value
     */
    void onCharacteristicChangedValue(String value) {

    }
}
