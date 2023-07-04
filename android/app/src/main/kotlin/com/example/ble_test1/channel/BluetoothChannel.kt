package io.lokks307.doseease.channel

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
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
import kotlin.experimental.and


const val TAG = "flutter_native"
const val IBEACON_TYPE = 0x02
const val IBEACON_LENGTH = 0x15

const val UUID_SIZE = 16

const val F_MAJOR = "major"
const val F_MINOR = "minor"
const val F_UUID = "uuid"

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
                    initBluetooth(activity)
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

    private fun initBluetooth(context :Context) {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    }

    private fun startScan(): Boolean {
        var isSuccess = true
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        try {
            discoveredDevices.clear()

            scanCallback =
            object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult) {
                    processScanResult(result)
                }
            }

            bluetoothLeScanner.startScan(null, scanSettings, scanCallback)
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

    private fun processScanResult(result: ScanResult) {
        val scanRecord = result.scanRecord?.bytes ?: return

        val parsedScanRecord = parseScanRecord(scanRecord)

        val gson = Gson()

        val device = result.device

        val rssi = result.rssi

        var data = BleDeviceData(
            device.name,
            device.address,
            device.type,
            device.bondState,
            parsedScanRecord[F_MAJOR] as Int?,
            parsedScanRecord[F_MINOR] as Int?,
            parsedScanRecord[F_UUID] as String?,
            rssi,
        )
        val res = gson.toJson(data)
        methodChannel.invokeMethod(METHOD_UPDATE_SCAN_DEVICE, res)
    }

    private fun parseScanRecord(data: ByteArray?): Map<String, Any?> {
        val res = mutableMapOf<String, Any?>(
            F_UUID to null,
            F_MAJOR to null,
            F_MINOR to null,
        )

        // and 0xFF를 하는 이유는 쓰레기 값에 대해 마스킹을 하기 위해서이다.
        // kotlin의 Int형은 1바이트가 아니기 때문에 1바이트 범위를 넘어서서 쓰레기 값이 저장될 수 있다.
        data?.let{
            var headerPos = 0
            var patternFound = false
            // iBeacon 헤더 탐색
            // ChatGPT 추천 코드를 살짝 건드린건데 아마 scanRecord의 초반 부분이 누락될 수 있어서 고정 위치가 아닌 패턴 매칭으로 검사하는거 같다.
            // iBeacon의 prefix는 총 9바이트이며 0~8번 인덱스가 해당 공간이다.
            // iBeacon의 Type(0x02)과 Length(0x15)의 패턴을 찾는 코드다.
            // 정상 길이로 들어오면 headerPos가 7일 때 7, 8번 인덱스를 검사하고 끝날 것이다.
            while (headerPos <= 7) {
                val type = it[headerPos].toInt() and 0xff
                val length = it[headerPos + 1].toInt() and 0xff

                // iBeacon 헤더 타입, 길이 찾기
                if (type == IBEACON_TYPE && length == IBEACON_LENGTH) {
                    patternFound = true
                    break
                }
                headerPos++
            }
            // 헤더 찾는게 성공 하면 헤더의 마지막 위치로부터 +2를 하면 uuid 데이터 시작점이다
            headerPos += 2

            // UUID의 크기는 16Byte이며 UUID 시작점에서 +16~ 을 하면 major와 minor의 공간 시작점이 된다.
            // major, minor는 각각 2Byte 공간을 가지고 있다.
            // 고정 인덱스 시작점은 25, 27이다
            val majorH = headerPos + UUID_SIZE
            val majorL = majorH + 1
            val minorH = majorL + 1
            val minorL = minorH + 1
            if (patternFound) {
                // UUID 파싱
                res[F_UUID] = bytesToHex(it.copyOfRange(headerPos, headerPos + UUID_SIZE))
                res[F_MAJOR] =
                    ((it[majorH].toInt() and 0xff) shl 8 ) + (it[majorL].toInt() and 0xff)
                res[F_MINOR] =
                    ((it[minorH].toInt() and 0xff) shl 8 )  + (it[minorL].toInt() and 0xff)
            }
        }

        return res
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

private fun bytesToHex(bytes: ByteArray): String {
    val sb = StringBuilder()
    for (b in bytes) {
        sb.append(String.format("%02x", b))
    }
    return sb.toString()
}