import 'package:json_annotation/json_annotation.dart';

part 'ble_device_data.g.dart';

@JsonSerializable()
class BleDeviceData {
  @JsonKey(name: 'name', defaultValue: 'Unknown')
  String name;
  @JsonKey(name: 'macAddr', defaultValue: 'Empty')
  String macAddr;
  @JsonKey(name: 'type', defaultValue: -1)
  int type;
  @JsonKey(name: 'bondState', defaultValue: -1)
  int bondState;
  @JsonKey(name: 'majorClass', defaultValue: -1)
  int majorCalss;
  @JsonKey(name: 'deviceClass', defaultValue: -1)
  int minorClass;
  @JsonKey(name: 'uuid', defaultValue: 'Empty')
  String uuid;
  @JsonKey(name: 'rssi', defaultValue: -1)
  int rssi;

  BleDeviceData({
    this.name = 'Unknown',
    this.macAddr = 'Empty',
    this.type = -1,
    this.bondState = -1,
    this.majorCalss = -1,
    this.minorClass = -1,
    this.uuid = 'Empty',
    this.rssi = -1,
  });

  factory BleDeviceData.fromJson(Map<String, dynamic> json) =>
      _$BleDeviceDataFromJson(json);

  Map<String, dynamic> toJson() => _$BleDeviceDataToJson(this);
}
