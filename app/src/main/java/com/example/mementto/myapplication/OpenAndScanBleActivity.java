package com.example.mementto.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class OpenAndScanBleActivity extends AppCompatActivity {

    /**
     * 判断回调结果是否为蓝牙请求
     */
    private static final int OPEN_BT = 2;

    /**
     *蓝牙管理器
     */
    private BluetoothManager manager;

    /**
     * 蓝牙适配器
     */
    private BluetoothAdapter adapter;

    private ProgressBar progressBar;

    private LinearLayout progressClick;

    private LinearLayout background;

    private RelativeLayout ret;

    private TextView textView;

    private boolean flag = true;

    /**
     * 弹出框
     */
    private Toast toast1;
    private Toast toast2;
    private Toast toast3;
    private Toast toast4;
    private Toast toast5;

    /**
     * 蓝牙设备名字存放数组
     */
    private List<BluetoothDevice> devices;

    /**
     * 测试按钮
     */
    private Button test;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_scan_ble);

        initView();

        openBT();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initView() {

        devices = new ArrayList<>();

        background = (LinearLayout) findViewById(R.id.ble_background);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressClick = (LinearLayout) findViewById(R.id.progress_click);
        textView = (TextView) findViewById(R.id.scan_stop);

        stop();

        ret = (RelativeLayout) findViewById(R.id.ret);
        ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        progressClick.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {
                if (flag) {
                    scan();
                } else {
                    stop();
                    adapter.stopLeScan(mLeScanCallback);
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void stop() {
        progressBar.setIndeterminateDrawable(getResources().getDrawable(R.mipmap.loading));
        progressBar.setProgressDrawable(getResources().getDrawable(R.mipmap.loading));
        textView.setText("停止搜索");
        flag = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void scan() {
        progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_circle));
        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_circle));
        textView.setText("搜索设备");
        flag = false;
        adapter.startLeScan(mLeScanCallback);
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            if (bluetoothDevice.getName() != null) {
                if (! devices.contains(bluetoothDevice)) {
                    devices.add(bluetoothDevice);
                    System.out.println(bluetoothDevice.getName() + ", " + bluetoothDevice.getAddress());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addBLEItem("   " + bluetoothDevice.getName());
                        }
                    });
                }
            }
        }
    };

    private void addBLEItem(String BLEName) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 145));
        textView.setText(BLEName);
        textView.setTextSize(15);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setBackgroundColor(Color.WHITE);
        background.addView(textView);

        View view = new View(this);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
        background.addView(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void openBT() {

        //获取蓝牙管理器
        manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (manager == null) {
            toast1.show();
        }

        //获取蓝牙适配器
        adapter = manager.getAdapter();

        //适配器为空或者不可用，都可以表示为蓝牙没打开
        if (adapter == null || !adapter.isEnabled()) {

            //前往打开蓝牙
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, OPEN_BT);
        }


    }

    /**
     * 隐式回调方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //回调结果为蓝牙
        if (requestCode == OPEN_BT && resultCode == RESULT_OK) {
            scan();
        } else {
            finish();
        }
    }
}
