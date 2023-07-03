import 'package:ble_test1/channel/bluetooth_channel.dart';
import 'package:ble_test1/data/ble_device_data.dart';
import 'package:ble_test1/view/ble_item_card.dart';
import 'package:flutter/material.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  final BluetoothChannel _bluetoothChannel = BluetoothChannel();
  final List<BleDeviceData> _bleDeviceDataList = [];

  int? scanResultListenerId;

  bool completedInit = false;
  bool isWaiting = false;
  bool isScanning = false;

  bool get canPressButton => completedInit && !isWaiting;
  bool get canStartScan => canPressButton && !isScanning;
  bool get canStopScan => canPressButton && isScanning;
  bool get isNotScanning => !isScanning;

  @override
  void initState() {
    super.initState();
    _bluetoothChannel.init().then((value) {
      WidgetsBinding.instance.addPostFrameCallback((_) {
        setState(() {
          completedInit = true;
        });
      });
    });
  }

  @override
  void dispose() {
    super.dispose();
    if (scanResultListenerId != null) {
      _bluetoothChannel.scanResultListener.remove(scanResultListenerId);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Demo BLE'),
      ),
      body: Column(
        children: [
          Expanded(
            child: ListView.builder(
              shrinkWrap: true,
              physics: const BouncingScrollPhysics(),
              itemCount: _bleDeviceDataList.length,
              itemBuilder: (context, index) => BleItemCard(
                bleDeviceData: _bleDeviceDataList[index],
              ),
            ),
          ),
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 20),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                ElevatedButton(
                  onPressed: () => onTapScan(),
                  child: Text(scanButtonStatusText()),
                ),
                ElevatedButton(
                    onPressed: onTapClear, child: const Text('Clear')),
              ],
            ),
          ),
        ],
      ),
    );
  }

  startScan() {
    _bluetoothChannel.startScan().then((value) {
      isWaiting = false;
      isScanning = true;
      setState(() {});
      scanResultListenerId ??=
          _bluetoothChannel.addScanResultListener(addDeviceToList);
    });
  }

  stopScan() {
    _bluetoothChannel.stopScan().then((value) {
      isWaiting = false;
      isScanning = false;
      setState(() {});
    });
  }

  onTapScan() {
    _debugPrintStatus();
    if (!canPressButton) return;
    setState(() {
      isWaiting = true;
    });
    if (isNotScanning) {
      startScan();
    } else {
      stopScan();
    }
  }

  onTapClear() {
    setState(() {
      _bleDeviceDataList.clear();
    });
  }

  String scanButtonStatusText() {
    if (isWaiting) {
      return 'Waiting...';
    } else if (isScanning) {
      return 'Stop Scan';
    } else {
      return 'Start Scan';
    }
  }

  _debugPrintStatus() {
    print('completedInit: $completedInit');
    print('isWaiting: $isWaiting');
    print('isScanning: $isScanning');
    print('canPressButton: $canPressButton');
    print('canStartScan: $canStartScan');
    print('canStopScan: $canStopScan');
  }

  void addDeviceToList(BleDeviceData data) {
    if (_bleDeviceDataList.any((d) => d.macAddr == data.macAddr)) return;
    setState(() {
      _bleDeviceDataList.add(data);
    });
  }
}
