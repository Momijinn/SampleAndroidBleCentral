package com.autumn_color.kanametakano.sampleblecentral;

import android.Manifest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.UUID;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //LOG
    private String TAG = "DEVICE_INFO";

    //Permission
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CODE_LOCATE_PERMISSION = 5;

    //PERIPHERAL_NAME
    private static final String PERIPHERAL_NAME = "MyBlePeripheral";
    //UUID
    private static final String CUSTOM_SURVICE_UUID = "713d0000-503e-4c75-ba94-3148f18d941e";
    private static final String CUSTOM_CHARACTERSTIC_UUID = "713d0001-503e-4c75-ba94-3148f18d941e";

    //Androidの固定値
    private static final String ANDROID_CENTRAL_UUID = "00002902-0000-1000-8000-00805f9b34fb";

    //BLE
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBleGatt = null;
    private BluetoothGattCharacteristic mBluetoothGattCharacteristic;

    private Button ConnectBtn, DisConnectBtn;
    private EditText RecvEditText;

    //
    final Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize
        ConnectBtn = (Button)findViewById(R.id.ConectButton);
        ConnectBtn.setOnClickListener(this);
        DisConnectBtn = (Button)findViewById(R.id.DisConectButton);
        DisConnectBtn.setOnClickListener(this);
        RecvEditText = (EditText)findViewById(R.id.RecvEditText);


        InitializeBleSetting();

    }

    /**
     * BLEの初期設定をおこなうところ
     */
    private void InitializeBleSetting() {
        //BLEがサポートしているかの確認
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            Toast.makeText(this, "お使いの端末はBLEが対応していません", Toast.LENGTH_SHORT).show();
            finish();
        }

        //bluetoothがONになっているか確認
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        //permisstion
        if(PermissionChecker.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestLocatePermission();
            return;
        }
    }


    /**
     * Permisstionの許可をする関数
     */
    private void requestLocatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("パーミッションの追加説明")
                    .setMessage("このアプリを使うには位置情報の許可が必要です")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_CODE_LOCATE_PERMISSION);
                        }
                    })
                    .create()
                    .show();
            return;
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATE_PERMISSION);
        return;
    }

    /**
     * Buttonイベント
     * @param
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ConectButton:
                if(mBleGatt == null) ConnectBleDevice();

                break;

            case R.id.DisConectButton:
                if(mBleGatt != null) DisConnectDevice();
                break;
        }
    }

    /**
     * ConnctBleDevice
     */
    private void ConnectBleDevice() {
        mBluetoothAdapter.getBluetoothLeScanner().startScan(mScanCallback);
    }
    /**
     * DisConncetDevice
     */
    private void DisConnectDevice() {
        mBleGatt.close();
        mBleGatt = null;
        Toast.makeText(this, "切断",Toast.LENGTH_SHORT).show();
    }


    /**
     * callback
     * ScanCallback
     * BLEの探索
     */
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            Log.i(TAG, "onScanResult()");
            Log.i(TAG, "DeviceName:" + result.getDevice().getName());
            Log.i(TAG, "DeviceAddr:" + result.getDevice().getAddress());
            Log.i(TAG, "RSSI:" + result.getRssi());
            Log.i(TAG, "UUID:" + result.getScanRecord().getServiceUuids());

            //接続するPeripheralNameが見つかったら接続
            if (PERIPHERAL_NAME.equalsIgnoreCase(result.getDevice().getName())) {
                mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback); //探索を停止

                result.getDevice().connectGatt(MainActivity.this, false, mGattCallback);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };


    /**
     * CallBack
     * GATTの処理関係
     * Peripheralへの接続,切断,データのやりとり
     */
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback(){
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // 接続できたらサービスの検索
                gatt.discoverServices();
            }else if (newState == BluetoothProfile.STATE_DISCONNECTED){ //マイコンの応答がなくなった時の処理
                mBleGatt.close();
                mBleGatt = null;

                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "切断されました",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service = gatt.getService(UUID.fromString(CUSTOM_SURVICE_UUID));

                if(service != null){
                    mBleGatt = gatt;

                    //Notifyの接続を試みてる
                    mBluetoothGattCharacteristic = service.getCharacteristic(UUID.fromString(CUSTOM_CHARACTERSTIC_UUID));
                    if(mBluetoothGattCharacteristic != null) {
                        boolean registered = gatt.setCharacteristicNotification(mBluetoothGattCharacteristic, true);
                        BluetoothGattDescriptor descriptor = mBluetoothGattCharacteristic.getDescriptor( UUID.fromString(ANDROID_CENTRAL_UUID) );
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        mBleGatt.writeDescriptor(descriptor);

                        if (registered){
                            Log.e("INFO", "notify ok");
                            handler.post(new Runnable() {
                                public void run() {
                                    Toast.makeText(MainActivity.this, "接続",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else Log.e("INFO", "notify ng");
                    }
                }
            }
        }


        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

            //回避:android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
            handler.post(new Runnable() {
                public void run() {
                    //順文字列を10進数にしている
                    byte[] RecvByteValue = characteristic.getValue();
                    String RecvStrValue = "";
                    for(int i=0; i < RecvByteValue.length ; i++){
                        RecvStrValue += (RecvByteValue[i] & 0xff) + ",";
                    }

                    RecvEditText.setText(RecvStrValue);
                }
            });
        }
    };

}


