import 'package:ble_test1/data/ble_device_data.dart';
import 'package:flutter/material.dart';

class BleItemCard extends StatelessWidget {
  final BleDeviceData bleDeviceData;
  const BleItemCard({Key? key, required this.bleDeviceData}) : super(key: key);
  @override
  Widget build(BuildContext context) {
    return Card(
      child: ConstrainedBox(
        // constraints: BoxConstraints(minHeight: 40, maxHeight: 100),
        constraints: BoxConstraints.loose(const Size.fromHeight(100)),

        child: Padding(
          padding: const EdgeInsets.all(8.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(bleDeviceData.name),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  RichText(
                    text: TextSpan(
                      children: [
                        const TextSpan(
                            text: 'Type: ',
                            style: TextStyle(color: Colors.black)),
                        TextSpan(
                            text: bleDeviceData.type.toString(),
                            style: const TextStyle(color: Colors.red)),
                        const TextSpan(
                            text: ', Bont State: ',
                            style: TextStyle(color: Colors.black)),
                        TextSpan(
                            text: bleDeviceData.bondState.toString(),
                            style: const TextStyle(color: Colors.blue)),
                        const TextSpan(
                            text: '\n', style: TextStyle(color: Colors.black)),
                        const TextSpan(
                            text: 'Major Class: ',
                            style: TextStyle(color: Colors.black)),
                        TextSpan(
                            text: bleDeviceData.majorCalss.toString(),
                            style: const TextStyle(color: Colors.green)),
                        const TextSpan(
                            text: ', Device Class:',
                            style: TextStyle(color: Colors.black)),
                        TextSpan(
                            text: bleDeviceData.deviceClass.toString(),
                            style: const TextStyle(color: Colors.orange)),
                      ],
                    ),
                  ),
                ],
              ),
              Text(bleDeviceData.macAddr),
            ],
          ),
        ),
      ),
    );
  }
}
