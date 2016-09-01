package com.yyt.blue.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.litesuits.bluetooth.LiteBleGattCallback;
import com.litesuits.bluetooth.LiteBluetooth;
import com.litesuits.bluetooth.exception.BleException;
import com.litesuits.bluetooth.exception.hanlder.DefaultBleExceptionHandler;
import com.litesuits.bluetooth.log.BleLog;
import com.litesuits.bluetooth.scan.PeriodScanCallback;
import com.litesuits.bluetooth.utils.BluetoothUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yangyoutao on 2016/8/29.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleToothHelper {
    private final String TAG = BleToothHelper.class.getSimpleName();
    public String UUID_SERVICE = "0000fff0-0000-1000-8000-00805f9b34fb";
    public String UUID_Characteristic = "0000fff3-0000-1000-8000-00805f9b34fb";
    /**
     * 蓝牙主要操作对象，建议单例。
     */
    private static LiteBluetooth mLiteBluetooth;
    /**
     * 默认异常处理器
     */
    private DefaultBleExceptionHandler mBleExceptionHandler;
    private final static int TIME_OUT_SCAN = 10000;//默认扫描时间20秒
    public static int TIME_OUT_SCAN_TYPE = 1;//查询超时
    private static List<BleToothCallBack> mCallBackList = new ArrayList<>();


    public BleToothHelper(Context context) {
        if (mLiteBluetooth == null) {//单例处理
            mLiteBluetooth = new LiteBluetooth(context);
            mLiteBluetooth.addGattCallback(mLiteBleuToothGattCallback);
        }
        mBleExceptionHandler = new DefaultBleExceptionHandler(context);
    }

    /**
     * 开启蓝牙
     */
    public void openBlueTooth() {
        mLiteBluetooth.enableBluetooth();
    }

    /**
     * 查找附近设备
     */
    public void scanDevicesPeriod() {
        openBlueTooth();
        mLiteBluetooth.startLeScan(new PeriodScanCallback(TIME_OUT_SCAN) {
            @Override
            public void onScanTimeout() {
                BleLog.i(TAG, "onScanTimeout: ");
                sendTimeOutCallBack(TIME_OUT_SCAN_TYPE);
            }

            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                BleLog.i(TAG, "device: " + device.getName() + "  mac: " + device.getAddress()
                        + "  rssi: " + rssi + "  scanRecord: " + Arrays.toString(scanRecord));
                sendScanDeviceCallBack(device, rssi, scanRecord);

            }
        });
    }

    /**
     * 连接设备和连接服务
     *
     * @param device
     */
    public void connectAndConnectServiceDevice(final Context context, final BluetoothDevice device) {
        mLiteBluetooth.stopScan(null);//停止查询
        mLiteBluetooth.connect(device, true, new LiteBleGattCallback() {
            @Override
            public void onConnectSuccess(BluetoothGatt bluetoothGatt, int i) {
                BleLog.i(TAG, "onConnectSuccess:连接成功，立刻启动服务发现 ");
                bluetoothGatt.discoverServices();//连接成功，立刻启动服务发现
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt bluetoothGatt, int i) {
                BluetoothUtil.printServices(bluetoothGatt);
                BluetoothGattCharacteristic Characteristic = BluetoothUtil.getCharacteristic(bluetoothGatt, UUID_SERVICE, UUID_Characteristic);//查找设备服务和服务特征
                bluetoothGatt.setCharacteristicNotification(Characteristic, true);
                byte[] dataToWrite = parseHexStringToBytes("8865");
                writeDataToCharacteristic(bluetoothGatt, Characteristic, dataToWrite);//写入
                sendConnectSuccessCallBack(device, bluetoothGatt, Characteristic);

            }

            @Override
            public void onConnectFailure(BleException e) {
                BleLog.i(TAG, "onConnectFailure: " + e.toString());
                sendFailureCallBack(e.toString());
            }
        });

    }


    /**
     * 蓝牙特征服务连接回调
     */
    private BluetoothGattCallback mLiteBleuToothGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            BleLog.i(TAG, "onConnectionStateChange: " + status + "  newState: " + newState);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] value = characteristic.getValue();
            String mReceiveData = "";
            try {
                mReceiveData = new String(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                mReceiveData = e.toString();
            }
            sendCharacteristicValueCallBack(mReceiveData);
            BleLog.i(TAG, "onCharacteristicChanged: " + mReceiveData);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            BleLog.i(TAG, "onCharacteristicWrite: " + characteristic.toString() + "   status:" + status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            BleLog.i(TAG, "onReadRemoteRssi: " + rssi + "   status:" + status);
        }
    };

    /**
     * 关闭蓝牙SCO通道
     */
    public void stopAudio() {
        if (null != mAudioManager) {
            mAudioManager.setBluetoothScoOn(false);
            mAudioManager.stopBluetoothSco();
        }
    }

    private AudioManager mAudioManager;

    //开启蓝牙通道
    public void startAudio(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setMode(AudioManager.MODE_RINGTONE);//不能改，千万不能改
        mAudioManager.setSpeakerphoneOn(true);
        if (!mAudioManager.isBluetoothScoAvailableOffCall()) {
            Log.d("bluetooh_myapp", "系统不支持蓝牙录音");
            return;
        }
        //蓝牙录音的关键，启动SCO连接，耳机话筒才起作用
        mAudioManager.startBluetoothSco();
        //蓝牙SCO连接建立需要时间，连接建立后会发出ACTION_SCO_AUDIO_STATE_CHANGED消息，通过接收该消息而进入后续逻辑。o
        //也有可能此时SCO已经建立，则不会收到上述消息，可以startBluetoothSco()前先stopBluetoothSco()
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                if (AudioManager.SCO_AUDIO_STATE_CONNECTED == state) {
                    mAudioManager.setBluetoothScoOn(true);  //打开SCO
                    context.unregisterReceiver(this);  //别遗漏
                } else {//等待一秒后再尝试启动SCO
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mAudioManager.startBluetoothSco();
                }
            }
        }, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED));
    }

    /**
     * 字节转换
     *
     * @param hex
     * @return
     */
    public byte[] parseHexStringToBytes(final String hex) {
        String tmp = hex.substring(2).replaceAll("[^[0-9][a-f]]", "");
        byte[] bytes = new byte[tmp.length() / 2]; // every two letters in the string are one byte finally
        String part = "";
        for (int i = 0; i < bytes.length; ++i) {
            part = "0x" + tmp.substring(i * 2, i * 2 + 2);
            bytes[i] = Long.decode(part).byteValue();
        }
        return bytes;
    }

    /**
     * 数据写入
     *
     * @param ch
     * @param dataToWrite
     */
    public void writeDataToCharacteristic(BluetoothGatt bluetoothGatt, final BluetoothGattCharacteristic ch, final byte[] dataToWrite) {
        if (bluetoothGatt == null || ch == null) {
            return;
        }
        // first set it locally....
        ch.setValue(dataToWrite);
        // ... and then "commit" changes to the peripheral
        bluetoothGatt.writeCharacteristic(ch);
    }

    /**
     * 添加回调
     *
     * @param bleToothCallBack
     */
    public static void addCallBack(BleToothCallBack bleToothCallBack) {
        if (null != bleToothCallBack) {
            mCallBackList.add(bleToothCallBack);
        }
    }

    /**
     * 查找设备发送回调
     */
    private void sendScanDeviceCallBack(BluetoothDevice device, int rssi, byte[] scanRecord) {
        for (BleToothCallBack bleToothCallBack : mCallBackList) {
            bleToothCallBack.onScanBleTooth(device, rssi, scanRecord);
        }
    }

    /**
     * 超时发送回调
     */
    private void sendTimeOutCallBack(int type) {
        for (BleToothCallBack bleToothCallBack : mCallBackList) {
            bleToothCallBack.onTimeout(type);
        }
    }

    /**
     * 超时发送回调
     */
    private void sendFailureCallBack(String error) {
        for (BleToothCallBack bleToothCallBack : mCallBackList) {
            bleToothCallBack.onFailure(error);
        }
    }

    /**
     * 超时发送回调
     */
    private void sendConnectSuccessCallBack(BluetoothDevice device, BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic Characteristic) {
        for (BleToothCallBack bleToothCallBack : mCallBackList) {
            bleToothCallBack.onConnectSuccess(device, bluetoothGatt, Characteristic);
        }
    }

    /**
     * 超时发送回调
     */
    private void sendCharacteristicValueCallBack(String value) {
        for (BleToothCallBack bleToothCallBack : mCallBackList) {
            bleToothCallBack.onCharacteristicChangedValue(value);
        }
    }
}
