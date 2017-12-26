package com.example.nirjon.bledemo5_advertiseconnect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    Switch sw1, sw2;
    TextView tv1, tv2;

    String myUUIDstring = "EC505EFD-75B9-44EB-8F2A-6FE0B41E7264";
    ParcelUuid myParcelUUID = new ParcelUuid(UUID.fromString(myUUIDstring));

    /*Advertisement Broadcast*/
    BluetoothManager myManager;
    BluetoothAdapter myAdapter;
    BluetoothLeAdvertiser myAdvertiser;
    AdvertiseSettings myAdvertiseSettings;
    AdvertiseData myAdvertiseData;
    AdvertiseCallback myAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Log.v("Tag","Success to start advertise: " + settingsInEffect.toString());
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            tv1.setText("Failed to start advertisement: errorcode = " + errorCode);
            tv1.invalidate();
        }
    };

    /*Scan for Advertisement*/
    BluetoothLeScanner myScanner;
    ScanSettings myScanSettings;
    ScanFilter myScanFilter;
    List<ScanFilter> myScanFilterList;
    ScanCallback myScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if(result == null) return;
            if(result.getDevice() == null) return;
            tv2.setText("Found: " + result.getDevice().toString());
            Log.v("Tag", "Found " + result.getDevice().toString());
            myGatt = result.getDevice().connectGatt(getApplicationContext(), false, myGattCallback);
            myScanner.stopScan(this);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    /*GATT Server*/
    BluetoothDevice myRemoteClientDevice;
    BluetoothGattServer myGattServer;
    BluetoothGattService myGattService;
    BluetoothGattServerCallback myGattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);

            myRemoteClientDevice = device;

            final int ns = newState;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv1.setText("GATT Server connection state changed: " + ns);
                    tv1.invalidate();
                }
            });
            Log.v("Tag", "GATT Server connection state changed: " + newState);
        }

        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            super.onServiceAdded(status, service);
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
        }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            super.onDescriptorReadRequest(device, requestId, offset, descriptor);
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
        }

        @Override
        public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
            super.onExecuteWrite(device, requestId, execute);
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
        }

        @Override
        public void onMtuChanged(BluetoothDevice device, int mtu) {
            super.onMtuChanged(device, mtu);
        }

        @Override
        public void onPhyUpdate(BluetoothDevice device, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(device, txPhy, rxPhy, status);
        }

        @Override
        public void onPhyRead(BluetoothDevice device, int txPhy, int rxPhy, int status) {
            super.onPhyRead(device, txPhy, rxPhy, status);
        }
    };

    /*GATT Client*/
    BluetoothGatt myGatt;
    BluetoothGattCallback myGattCallback = new BluetoothGattCallback() {
        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            final int ns = newState;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv2.setText("GATT Client connection state changed: " + ns);
                    tv2.invalidate();
                }
            });

            Log.v("Tag", "GATT Client connection state changed: " + newState);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init_advertise();
        init_scan();
    }

    /*Server Side Code: 1) starts advertisement and 2) opens GATT Server.*/
    void init_advertise() {
        myManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        myAdapter = myManager.getAdapter();

        tv1 = (TextView) findViewById(R.id.tv1);
        sw1 = (Switch) findViewById(R.id.switchADV);

        if (myAdapter.isMultipleAdvertisementSupported() == false) {
            tv1.setText("Device does not support BLE advertisment.");
            sw1.setEnabled(false);
            return;
        }

        myAdvertiser = myAdapter.getBluetoothLeAdvertiser();
        myAdvertiseSettings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .build();
        myAdvertiseData = new AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .setIncludeTxPowerLevel(false)
                .addServiceUuid(myParcelUUID)
                .build();

        sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    tv1.setText("Advertise contains Service UUID: " + myUUIDstring);

                    myGattServer = myManager.openGattServer(getApplicationContext(), myGattServerCallback);
                    myGattService = new BluetoothGattService(myParcelUUID.getUuid(), BluetoothGattService.SERVICE_TYPE_PRIMARY);
                    myGattServer.addService(myGattService);

                    myAdvertiser.startAdvertising(myAdvertiseSettings, myAdvertiseData, myAdvertiseCallback);

                } else {
                    tv1.setText("Advertisement stopped.");
                    try {
                        myAdvertiser.stopAdvertising(myAdvertiseCallback);
                        myGattServer.cancelConnection(myRemoteClientDevice);
                        myGattServer.close();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    /*Client side code: starts scanning (specific UUID). Connection happens at the scan callback */
    void init_scan()
    {
        tv2 = (TextView) findViewById(R.id.tv2);
        sw2 = (Switch) findViewById(R.id.switchSC);

        myScanner = myAdapter.getBluetoothLeScanner();
        myScanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        myScanFilter = new ScanFilter.Builder()
                .setServiceUuid(myParcelUUID)
                .build();

        myScanFilterList = new ArrayList<ScanFilter>();
        myScanFilterList.add(myScanFilter);

        sw2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    tv2.setText("Scanning for " + myUUIDstring);
                    myScanner.startScan(myScanFilterList, myScanSettings, myScanCallback);
                }
                else {
                    tv2.setText("Scan stopped.");
                    try {
                        myScanner.stopScan(myScanCallback);
                        myGatt.disconnect();
                        myGatt.close();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        });
    }



}
