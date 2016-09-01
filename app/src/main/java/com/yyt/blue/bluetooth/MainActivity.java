package com.yyt.blue.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nanfeng.linphonelibrary.activity.WellcomeActivity;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mScanBtn, mCallPhone;
    private TextView mBleTooth, mClassicTooth;
    private ListView mLoglistview;
    private List<String> mLogList = new ArrayList<>();
    private ListViewAdapter mListViewAdapter;
    private ImageView mFM;
    private BleToothHelper mBleToothHelper;
    private Boolean isChangle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {

        mScanBtn = (Button) findViewById(R.id.scan_blue);
        mCallPhone = (Button) findViewById(R.id.callphone);
        mBleTooth = (TextView) findViewById(R.id.ble_blue);
        mClassicTooth = (TextView) findViewById(R.id.classic_blue);
        mLoglistview = (ListView) findViewById(R.id.listlog_blue);
        mFM = (ImageView) findViewById(R.id.fm);
        mListViewAdapter = new ListViewAdapter(this, mLogList);
        mLoglistview.setAdapter(mListViewAdapter);
        mScanBtn.setOnClickListener(this);
        mCallPhone.setOnClickListener(this);
        mBleToothHelper = new BleToothHelper(this);
        BleToothHelper.addCallBack(new BleToothCallBack() {
            @Override
            void onTimeout(int type) {
                addLog("查询时间到。");
            }

            @Override
            void onFailure(String error) {
                addLog("onFailure:" + error);
            }

            @Override
            void onConnectSuccess(final BluetoothDevice device, BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic Characteristic) {
                addLog("onConnectSuccess:" + device.getName() + "  MAC:" + device.getAddress());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBleTooth.append("\n" + "设备名：" + device.getName() + "\n" + "MACaddress:" + device.getAddress());
                    }
                });
            }

            @Override
            void onCharacteristicChangedValue(final String value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addLog("value:" + value);
                        switch (value) {
                            case "FM":
                                mFM.setVisibility(View.VISIBLE);
                                break;
                            case "PTT0":
                                mFM.setVisibility(View.GONE);
                                break;
                            case "PTT1":
                                if (isChangle) {
                                    isChangle = false;
                                }
                                break;
                            case "PTT2":
                                isChangle = true;
                                break;
                        }
                    }
                });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 添加数据，逆序
     *
     * @param str
     */
    private void addLog(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLogList.add(0, str);
                mListViewAdapter.setListData(mLogList);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scan_blue:
                Intent intent = new Intent(MainActivity.this, ScanBlueToothActivity.class);
                startActivity(intent);
                break;
            case R.id.callphone:
                Intent intent1 = new Intent(MainActivity.this, WellcomeActivity.class);
                startActivity(intent1);
                break;
        }
    }
}
