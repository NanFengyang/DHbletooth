package com.yyt.blue.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ScanBlueToothActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private String TAG = "ScanBlueToothActivity";
    private TextView mScaning;
    private ListView mListView;
    private List<BluetoothDevice> mlist = new ArrayList<>();
    private DeviceListViewAdapter mListViewAdapter;
    private String scanText = "搜索中";
    private Activity activity;
    private BleToothHelper mBleToothHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_blue_tooth);
        activity = this;
        initView();
        mBleToothHelper = new BleToothHelper(this);
        BleToothHelper.addCallBack(new BleToothCallBack() {
            @Override
            void onTimeout(int type) {
                showToast("连接超时");
                mScaning.setText("搜索完成");
            }

            @Override
            void onFailure(String error) {
                showToast("onFailure：" + error);
            }

            @Override
            void onScanBleTooth(BluetoothDevice device, int rssi, byte[] scanRecord) {
                Log.i("onScanBleTooth", "onScanBleTooth");
                if (!mlist.contains(device)) {
                    Log.i("onScanBleTooth", "device:" + device.getName());
                    mlist.add(0, device);
                    mListViewAdapter.setListData(mlist);
                }
            }

            @Override
            void onConnectSuccess(BluetoothDevice device, BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic Characteristic) {
                Log.i("onScanBleTooth", "onConnectSuccess:");
                finish();
            }
        });
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * scan devices for a while
     */
    private void scanDevicesPeriod() {
        mScaning.setText(scanText);
        mlist.clear();
        mListViewAdapter.setListData(mlist);
        mBleToothHelper.scanDevicesPeriod();
    }

    private void initView() {
        mScaning = (TextView) findViewById(R.id.scaning);
        mListView = (ListView) findViewById(R.id.scanlistview);
        mListViewAdapter = new DeviceListViewAdapter(this, mlist);
        mListView.setAdapter(mListViewAdapter);
        mListView.setOnItemClickListener(this);
        mScaning.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanDevicesPeriod();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        BluetoothDevice device = mListViewAdapter.getItem(i);
        showToast("连接设备：" + device.getName() + " mac:" + device.getAddress());
        //绑定服务  开启服务
        mBleToothHelper.connectAndConnectServiceDevice(this, device);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scaning:
                scanDevicesPeriod();
                break;
        }
    }
}
