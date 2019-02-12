package com.example.mementto.myapplication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * 判断回调结果是否为蓝牙请求
     */
    private static final int OPEN_BT = 2;

    /**
     * 判断回调结果是否为位置请求
     */
    private static final int REQUEST_COARSE_LOCATION = 0;

    /**
     * 控制扫描时间
     */
    private static final int SCAN_PERIOD = 30;

    /**
     *蓝牙管理器
     */
    private BluetoothManager manager;

    /**
     * 蓝牙适配器
     */
    private BluetoothAdapter adapter;

    /**
     * Android5.0以上的蓝牙扫描方式
     */
    private BluetoothLeScanner scanner;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        test = (Button) findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View view) {
                mayRequestLocation();
            }
        });
    }

    /**
     * 编辑弹出框
     */
    private void initView() {
        toast1 = Toast.makeText(this, StringValue.WITHOUT_BT, Toast.LENGTH_SHORT);
        toast2 = Toast.makeText(this, StringValue.VERSION_VALUE, Toast.LENGTH_SHORT);
        toast3 = Toast.makeText(this, StringValue.VERSION_AWARDED, Toast.LENGTH_SHORT);
        toast4 = Toast.makeText(this, StringValue.VERSION_REJECTED, Toast.LENGTH_SHORT);
        toast5 = Toast.makeText(this, StringValue.BT_SCAN_FAIL, Toast.LENGTH_SHORT);

        devices = new ArrayList<>();
    }

    /**
     * 打开蓝牙方法
     */
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
     * 版本判断方法
     */
    private void compareVersion() {
        //Android版本大于5.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int i = 1;
            int j = 3;
            scanDevice();

            //Android版本低于5.0，高于4.3
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            scanLeDevice();
        } else {
            toast2.show();
        }
    }

    /**
     * 模糊位置权限请求方法
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void mayRequestLocation() {
        //Android版本大于6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkCallPhonePermission = ContextCompat
                    .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            //判断权限是否已申请
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_COARSE_LOCATION);
                return;
            } else {
                openBT();
            }
        } else {
            openBT();
        }

    }



    /**
     * Android版本大于5.0的扫描方法
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scanDevice() {
        scanner = adapter.getBluetoothLeScanner();
        scanner.startScan(mScanCallBack);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scanner.stopScan(mScanCallBack);
            }
        }, SCAN_PERIOD);
    }

    /**
     * Android版本低于5.0，高于4.3的扫描方法
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void scanLeDevice() {
        adapter.startLeScan(mLeScanCallback);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.stopLeScan(mLeScanCallback);
            }
        }, SCAN_PERIOD);
    }

    /**
     * Android版本大于5.0的扫描回调方法
     */
    private ScanCallback mScanCallBack = new ScanCallback() {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            int i = 1;
            int j = i;
            BluetoothDevice device = result.getDevice();
            if (! devices.contains(device)) {
                devices.add(device);
                Log.e("name", "SCAN--" + device.getName());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            toast5.show();
        }
    };

    /**
     * Android版本低于5.0，高于4.3的扫描回调方法
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {

        }
    };

    /**
     * 位置请求回调结果方法
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //确保是我们的请求
        if (requestCode == REQUEST_COARSE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toast3.show();
                openBT();
            } else if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                toast4.show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 隐式回调方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //回调结果为蓝牙
        if (requestCode == OPEN_BT && resultCode == RESULT_OK) {
            compareVersion();
        }
    }
}
