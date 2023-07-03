package io.lokks307.doseease.channel

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.ble_test1.MainActivity
import com.example.ble_test1.model.BleDeviceData
import com.google.gson.Gson
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val TAG = "flutter_native"

class BluetoothChannel(private val activity: MainActivity, flutterEngine: FlutterEngine) :
    MethodChannel.MethodCallHandler, EventChannel.StreamHandler {


    private val methodChannel = MethodChannel(
        flutterEngine.dartExecutor.binaryMessenger,
        BLUETOOTH_CHANNEL
    )

    private val eventChannel = EventChannel(
        flutterEngine.dartExecutor.binaryMessenger,
        BLUETOOTH_EVENT_CHANNEL
    )

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private lateinit var scanCallback: ScanCallback
    private val discoveredDevices = ArrayList<BluetoothDevice>()


    init {
        methodChannel.setMethodCallHandler(this)
        eventChannel.setStreamHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        CoroutineScope(Dispatchers.Main).launch {
            when (call.method) {
                METHOD_INIT -> {
                    Log.d(TAG, "METHOD_INIT")
                    initBluetooth()
                    result.success(true)
                }
                METHOD_START_SCAN -> {
                    Log.d(TAG, "METHOD_START_SCAN")
                    result.success(startScan())
                }
                METHOD_STOP_SCAN -> {
                    Log.d(TAG, "METHOD_STOP_SCAN")
                    result.success(stopScan())
                }
                else -> result.notImplemented()
            }
        }
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        TODO("Not yet implemented")
    }

    override fun onCancel(arguments: Any?) {
        TODO("Not yet implemented")
    }

    private fun initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    }

    private fun startScan(): Boolean {
        var isSuccess = true
        try {
            discoveredDevices.clear()

            scanCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult) {
                    val device = result.device
                    updateScanResult(device.name ?: "Unknown", device.address)
                }
            }

            bluetoothLeScanner.startScan(scanCallback)
        } catch (e: Exception) {
            Log.w(TAG, "스캔 시작 실패")
            isSuccess = false
        }
        return isSuccess
    }

    private fun stopScan(): Boolean {
        var isSuccess = true
        try {
            bluetoothLeScanner.stopScan(scanCallback)
            discoveredDevices.clear()
        } catch (e: Exception) {
            Log.w(TAG, "스캔 종료 실패")
            isSuccess = false
        }
        return isSuccess
    }

    private fun updateScanResult(deviceName: String, deviceAddr: String) {
        val gson = Gson()
        val res = gson.toJson(BleDeviceData(deviceName, deviceAddr))
        methodChannel.invokeMethod(METHOD_UPDATE_SCAN_DEVICE, res)
    }


    companion object {
        private const val BLUETOOTH_CHANNEL = "com.example.ble_test1/bluetooth"
        private const val BLUETOOTH_EVENT_CHANNEL = "com.example.ble_test1/bluetooth_event"

        private const val METHOD_INIT = "init"
        private const val METHOD_START_SCAN = "startScan"
        private const val METHOD_STOP_SCAN = "stopScan"
        private const val METHOD_UPDATE_SCAN_DEVICE = "updateScanDevice"

        private const val EVENT_SCAN_RESULT = "scanResult"
    }
}