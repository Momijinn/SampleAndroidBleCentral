SampleAndroidBleCentral
====
AndroidでBLE通信におけるCentralの役割をするサンプルアプリ

## Description
Android端末をBLE通信におけてCentralとして動作をするサンプルアプリ

## Demo
![screenshot](https://github.com/Momijinn/SampleAndroidBleCentral/img/screen.png)

## Requirement
* Android5.0以上の端末

## Usage
アプリケーションを端末に入れて、CONNECTを押すとデバイスと接続し、通知を受け取ることができます

DISCONNECTを押すと切断します

## Install
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

## Licence
This software is released under the MIT License, see LICENSE.

## Author
[Twitter](https://twitter.com/momijinn_aka)

[Blog](http://www.autumn-color.com/)