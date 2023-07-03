import 'package:json_annotation/json_annotation.dart';

part 'ble_device_data.g.dart';

@JsonSerializable()
class BleDeviceData {
  @JsonKey(name: 'name')
  String name;
  @JsonKey(name: 'macAddr')
  String macAddr;
  @JsonKey(name: 'type')
  int type;
  @JsonKey(name: 'bondState')
  int bondState;
  @JsonKey(name: 'majorClass')
  int majorCalss;
  @JsonKey(name: 'deviceClass')
  int deviceClass;

  BleDeviceData({
    this.name = 'Unknown',
    this.macAddr = 'Empty',
    this.type = -1,
    this.bondState = -1,
    this.majorCalss = -1,
    this.deviceClass = -1,
  });

  factory BleDeviceData.fromJson(Map<String, dynamic> json) =>
      _$BleDeviceDataFromJson(json);

  Map<String, dynamic> toJson() => _$BleDeviceDataToJson(this);
}
