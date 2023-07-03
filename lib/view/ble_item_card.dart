import 'package:ble_test1/data/ble_device_data.dart';
import 'package:flutter/material.dart';

class BleItemCard extends StatelessWidget {
  final BleDeviceData bleDeviceData;
  const BleItemCard({Key? key, required this.bleDeviceData}) : super(key: key);
  @override
  Widget build(BuildContext context) {
    return Card(
      child: SizedBox(
        height: 50,
        width: double.infinity,
        child: Padding(
          padding: const EdgeInsets.all(8.0),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(bleDeviceData.name),
              Text(bleDeviceData.macAddr),
            ],
          ),
        ),
      ),
    );
  }
}
