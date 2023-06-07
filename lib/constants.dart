/// Constants contains the keys used for streaming events and bytes
class Constants {
  /// Event for when network state changes to Wifi
  static const wifi = 0xFF;

  /// Event for when network state changes to cellular/mobile data
  static const cellular = 0xEE;

  /// Event for when network is disconnected
  static const disconnected = 0xDD;

  /// Event for when network state is a state you do not
  /// support (e.g VPN or Ethernet on Android)
  static const unknown = 0xCC;

  /// Event that denotes an end of the stream
  static const eof = -0xFFFFFF;
}
