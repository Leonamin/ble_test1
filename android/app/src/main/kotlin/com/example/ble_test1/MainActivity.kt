package com.example.ble_test1

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.lokks307.doseease.channel.BluetoothChannel

class MainActivity: FlutterActivity() {
    private lateinit var bluetoothChannel: BluetoothChannel

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        bluetoothChannel = BluetoothChannel(this, flutterEngine)
    }
}
