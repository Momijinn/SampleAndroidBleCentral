# SampleAndroidBleCentral
AndroidでBLE通信におけるCentralの役割をするサンプルアプリ

# 環境
* Android5.0以上の端末

# 概要

基本的にMainActivity.javaの接続したいデバイス名とServiceUUIDとCharacteristicUUIDを書き換えれば他のデバイスに対応できると思います
```java
//PERIPHERAL_NAME
private static final String PERIPHERAL_NAME = "MyBlePeripheral";
//UUID
private static final String CUSTOM_SURVICE_UUID = "713d0000-503e-4c75-ba94-3148f18d941e";
private static final String CUSTOM_CHARACTERSTIC_UUID = "713d0001-503e-4c75-ba94-3148f18d941e";
```

デフォルトの状態で接続を確認したい場合は、BLEnano2を購入し以下のURLのGitHubに上がっているプログラムをBlenano2に書き込んでみてください

https://github.com/Momijinn/SampleBlenano2BlePeripheral.git

# blog
[AutumnColor.com](http://www.autumn-color.com/)