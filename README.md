# mpvKt-fork

A media player for Android based on [mpv-android](https://github.com/mpv-android/mpv-android) aiming to provide a *nicer* user interface over the original.

This is a fork of [abdallahmehiz/mpvKt](https://github.com/abdallahmehiz/mpvKt) with key changes for personal use.

## What's different

- Fixed subtitle picker UI bugs
- A more modern design language
- All config and `watch_later` files written to a publicly accessible SD location for cross-platform sync via [syncthing-fork](https://github.com/researchxxl/syncthing-android)
- Simple file picker for subtitles (no Google file picker, no file type filtering)

## Building

Java 21 is required for building. [Adoptium Temurin JDK 21](https://adoptium.net/) is recommended.

```bash
./gradlew assembleDebug
```

## License

This project is licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for the full text.

## Contributing

Pull requests are not welcome. This is a personal project. However, forking is encouraged.

I may make changes to this. I may not. This is effectively for me, but I'm giving it back to the community.

This is my little experiment with frontier Chinese AI models from Tencent.

## Acknowledgments

- [abdallahmehiz/mpvKt](https://github.com/abdallahmehiz/mpvKt) for the original work
- [mpv-android](https://github.com/mpv-android) for the base mpv library

## Donations

[![](https://github.com/aha999/DonateButtons/raw/master/paypal-donate-icon-7.png)](https://paypal.me/dkendall529?country.x=GB&locale.x=en_GB)