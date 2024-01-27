# DUCO-Android-Miner

 A Duino Coin Mining App for Android

## Attention: This is NOT the official Duino Coin Miner for Android. Please note that I am not affiliated with Duino Coin maintainers.

This repository contains the source code for my Duino Coin mining app for android. The aim in developing this application was to create a more practical way of mining cryptocurrency on a mobile phone, without having to use the web miner, which usually had its Javascript interrupted by putting the browser in the background or turning off the screen.

Most of the source code is written in Java, but the SHA1 function is executed in native code, written in C++ from Volker Diels-Grabsch's implementation, with the aim of increasing the number of hashes per second.
I haven't made a comparison with the SHA1 functions written directly in Java to find out the real increase in the miner's performance.

The miner has the option of running several mining threads based on the number of cores available on his device, solving several jobs at the same time.

On my phone, a Xiaomi Redmi Note 11, each thread performs at around 100,000 hashes per second.

[This app was published on the Google Play Store.](https://play.google.com/store/apps/details?id=com.fatorius.duinocoinminer)

### Screenshots

![Screenshot_2024-01-04-22-22-22-134_com.fatorius.duinocoinminer.jpg](https://github.com/fatorius/DUCO-Android-Miner/blob/main/Screenshots/Screenshot_2024-01-04-22-22-22-134_com.fatorius.duinocoinminer.jpg?raw=true)



![Screenshot_2024-01-04-22-23-49-756_com.fatorius.duinocoinminer.jpg](https://github.com/fatorius/DUCO-Android-Miner/blob/main/Screenshots/Screenshot_2024-01-04-22-23-49-756_com.fatorius.duinocoinminer.jpg?raw=true)
