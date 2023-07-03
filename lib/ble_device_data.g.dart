// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'ble_device_data.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

BleDeviceData _$BleDeviceDataFromJson(Map<String, dynamic> json) =>
    BleDeviceData(
      name: json['name'] as String,
      macAddr: json['macAddr'] as String,
    );

Map<String, dynamic> _$BleDeviceDataToJson(BleDeviceData instance) =>
    <String, dynamic>{
      'name': instance.name,
      'macAddr': instance.macAddr,
    };
