import 'dart:convert';

import 'package:ble_test1/ble_device_data.dart';
import 'package:ble_test1/value_listener.dart';
import 'package:flutter/services.dart';

class BluetoothChannel {
  BluetoothChannel() {
    _methodChannel.setMethodCallHandler(_handleMethod);
  }

  final MethodChannel _methodChannel =
      const MethodChannel('com.example.ble_test1/bluetooth');
  final EventChannel _eventChannel =
      const EventChannel('com.example.ble_test1/bluetooth_event');

  static const _methodInit = 'init';
  static const _methodStartScan = 'startScan';
  static const _methodStopScan = 'stopScan';
  static const _methodConnect = 'connect';
  static const _methodUpdateScanDevice = 'updateScanDevice';

  static const _eventScanResult = 'scanResult';

  final ValueListener<BleDeviceData> _scanResultListener =
      ValueListener<BleDeviceData>();

  ValueListener<BleDeviceData> get scanResultListener => _scanResultListener;

  int addScanResultListener(Function(BleDeviceData) listener) {
    return _scanResultListener.add(listener);
  }

  void removeScanResultListener(int? listenId) {
    _scanResultListener.remove(listenId);
  }

  Future<void> init() async {
    await _methodChannel.invokeMethod(_methodInit);
  }

  Future<void> startScan() async {
    await _methodChannel.invokeMethod(_methodStartScan);
  }

  Future<void> stopScan() async {
    await _methodChannel.invokeMethod(_methodStopScan);
  }

  Future<void> connect(String address) async {
    await _methodChannel.invokeMethod(_methodConnect, address);
  }

  Stream<dynamic> get scanResultStream {
    return _eventChannel.receiveBroadcastStream(_eventScanResult);
  }

  Future<void> _handleMethod(MethodCall call) async {
    switch (call.method) {
      case _methodUpdateScanDevice:
        try {
          final res = call.arguments;
          final data = BleDeviceData.fromJson(jsonDecode(res));
          print('Device Name : ' + data.name);

          _scanResultListener.notify(data);
        } catch (e) {
          print(e);
        }
        // if (data.name != 'Unknown') print(data.name);

        break;
      default:
        return Future<void>.error('Method not defined');
    }
  }
}
