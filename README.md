<div align="center">
  <img src="./app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" alt="RevoMirror Android icon" width="120"/>
  <h1 align="center">RevoMirror-Android</h1>
  <h4 align="center">Android screen mirroring client for RevoMirror over LAN.</h4>
</div>

<div align="center">
  <a href="https://github.com/Revopoint-Software/RevoMirror-Android/stargazers"><img src="https://img.shields.io/github/stars/Revopoint-Software/RevoMirror-Android.svg?logo=github&style=for-the-badge" alt="GitHub stars"></a>
  <a href="https://github.com/Revopoint-Software/RevoMirror-Android/releases/latest"><img src="https://img.shields.io/github/downloads/Revopoint-Software/RevoMirror-Android/total.svg?style=for-the-badge&logo=github" alt="GitHub Releases"></a>
  <a href="https://github.com/Revopoint-Software/RevoMirror-Android/blob/main/LICENSE.txt"><img src="https://img.shields.io/github/license/Revopoint-Software/RevoMirror-Android.svg?style=for-the-badge" alt="License"></a>
</div>

<div align="center">
  <b>English</b> | <a href="README.zh-CN.md">简体中文</a>
</div>

## About

**RevoMirror-Android** is an Android screen mirroring and remote control client
developed by **Revopoint Software**. It is based on the open-source project
[Moonlight Android](https://github.com/moonlight-stream/moonlight-android),
which is licensed under GPL-3.0.

RevoMirror-Android enables:

- **PC screen mirroring**: View a paired PC desktop on an Android device in real time.
- **Touch remote control**: Control the remote desktop with touch input.
- **LAN operation**: Connect to a host on the same local network for low-latency streaming.
- **RevoMirror workflow**: Discover, add, pair, and connect to RevoMirror-compatible hosts.

> **Note:** RevoMirror-Android is a derivative work based on Moonlight Android.
> The core streaming client, input pipeline, decoder integration, and NVIDIA
> GameStream/Sunshine protocol support are inherited from Moonlight Android.
> RevoMirror-Android adds the RevoMirror UI, pairing flow, touch interaction,
> localization, legal document views, and Android packaging changes.

## Key Features

- **Android client**: Runs on Android phones and tablets.
- **PC to Android mirroring**: Stream a Windows or macOS host desktop to Android.
- **Touch control**: Send touch input back to the host for remote operation.
- **Pinch zoom and pan**: Use two-finger gestures to zoom and drag the streamed view.
- **Device discovery and pairing**: Find hosts on the LAN, add hosts manually, and pair with a PIN.
- **Custom RevoMirror UI**: Includes splash screen, device list, pairing dialogs, settings, and loading states.
- **Language settings**: Supports in-app language switching for the RevoMirror interface.
- **License and policy views**: Includes GPLv3, user agreement, and privacy policy documents in the app.

## How It Works

RevoMirror-Android is built from the Moonlight Android codebase and keeps its
streaming foundation:

- **Moonlight Android client stack** for host discovery, pairing, streaming, decoding, and input.
- **moonlight-common-c / MoonBridge** for the native streaming protocol layer.
- **Android MediaCodec** for hardware video decoding where supported.
- **Touch and input handling** adapted for RevoMirror remote desktop control.

On top of this, RevoMirror-Android provides a RevoMirror-oriented Android app
experience for connecting to a RevoMirror-PC or Sunshine-compatible host.

## Platform Support

| Role | Android | Windows | macOS |
|------|:-------:|:-------:|:-----:|
| Client (viewer / controller) | Supported | Not applicable | Not applicable |
| Host (PC to be mirrored / controlled) | Not applicable | Supported via RevoMirror-PC / Sunshine | Supported via RevoMirror-PC / Sunshine |

## System Requirements

### Android Client

| Component | Requirement |
|-----------|-------------|
| OS | Android 7.0 or later |
| CPU / GPU | Device with hardware video decoding support recommended |
| Network | 5 GHz Wi-Fi or wired Ethernet adapter recommended |
| Connection | Same LAN/subnet as the host PC |

### Host PC

RevoMirror-Android is intended to connect to a compatible RevoMirror-PC or
Sunshine-based host. The host requirements follow the underlying streaming host
capabilities, including hardware-accelerated screen capture and video encoding
where available.

## Getting Started

1. Install and run RevoMirror-PC on the host computer.
2. Install RevoMirror-Android on the Android device.
3. Make sure the Android device and host PC are on the same LAN or subnet.
4. Open RevoMirror-Android and select a discovered host, or add one manually.
5. Pair with the host using the displayed PIN.
6. Start mirroring and use touch gestures to view or control the desktop.

## Building

1. Install Android Studio, Android SDK 35, and Android NDK `27.0.12077973`.
2. Clone the repository and initialize submodules if required.
3. Open `RevoMirror-Android` in Android Studio.
4. Build the `app` module using the `nonRoot` or `root` product flavor.


## Documentation

- Modification notice: [CHANGES.md](CHANGES.md)
- Upstream client: [Moonlight Android](https://github.com/moonlight-stream/moonlight-android)
- Compatible host engine: [Sunshine](https://github.com/LizardByte/Sunshine)

## License

RevoMirror-Android is licensed under the **GNU General Public License v3.0
(GPL-3.0)**.

This project is a derivative work based on
[Moonlight Android](https://github.com/moonlight-stream/moonlight-android),
which is also licensed under GPL-3.0. In compliance with the terms of the GPL,
RevoMirror-Android is distributed under the same license.

See [LICENSE](./LICENSE) for
the full license text.

## Acknowledgements & Credits

RevoMirror-Android is built upon the excellent work of these open-source
projects:

- **[Moonlight Android](https://github.com/moonlight-stream/moonlight-android)**:
  Open-source Android client for NVIDIA GameStream and Sunshine.
- **[Moonlight Common C](https://github.com/moonlight-stream/moonlight-common-c)**:
  Native streaming protocol library used by Moonlight clients.
- **[Sunshine](https://github.com/LizardByte/Sunshine)**:
  Self-hosted game streaming host compatible with Moonlight.

We extend our sincere gratitude to the Moonlight and Sunshine contributors. The
original license and copyright notices are retained throughout this repository.

## About Revopoint Software

RevoMirror-Android is developed and maintained by **Revopoint Software**
(Xi'an Chishine Optoelectronics Technology Co., Ltd.).
