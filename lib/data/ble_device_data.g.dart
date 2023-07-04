// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'ble_device_data.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

BleDeviceData _$BleDeviceDataFromJson(Map<String, dynamic> json) =>
    BleDeviceData(
      name: json['name'] as String? ?? 'Unknown',
      macAddr: json['macAddr'] as String? ?? 'Empty',
      type: json['type'] as int? ?? -1,
      bondState: json['bondState'] as int? ?? -1,
      majorCalss: json['majorClass'] as int? ?? -1,
      minorClass: json['deviceClass'] as int? ?? -1,
      uuid: json['uuid'] as String? ?? 'Empty',
      rssi: json['rssi'] as int? ?? -1,
    );

Map<String, dynamic> _$BleDeviceDataToJson(BleDeviceData instance) =>
    <String, dynamic>{
      'name': instance.name,
      'macAddr': instance.macAddr,
      'type': instance.type,
      'bondState': instance.bondState,
      'majorClass': instance.majorCalss,
      'deviceClass': instance.minorClass,
      'uuid': instance.uuid,
      'rssi': instance.rssi,
    };
