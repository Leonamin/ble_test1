import 'package:json_annotation/json_annotation.dart';

part 'ble_device_data.g.dart';

@JsonSerializable()
class BleDeviceData {
  @JsonKey(name: 'name')
  String name;
  @JsonKey(name: 'macAddr')
  String macAddr;

  BleDeviceData({
    required this.name,
    required this.macAddr,
  });

  factory BleDeviceData.fromJson(Map<String, dynamic> json) =>
      _$BleDeviceDataFromJson(json);

  Map<String, dynamic> toJson() => _$BleDeviceDataToJson(this);
}
