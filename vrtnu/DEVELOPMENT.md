# Investigate VRT NU APK network calls

## Prerequisite

Follow the normal procedure of preparing your test device to allow Charles.  
In summary you should have accepted the Charles root certificate on your phone.  

Your network traffic should be proxied to your Charles instance.  
You should have accepted the connection from your test device.  

## Prepare VRT NU release APK to allow Charles SSL Proxy
### Download APK

You can use any tool to obtain the Play Store APK.
A simple method is to use `envozi` online tool to get a direct download link

```
https://apps.evozi.com/apk-downloader/?id=be.vrt.vrtnu
```

Save on a well known destination

### Override network configuration of the VRT NU APK

Extract contents with `apktool`

```
apktool d vrt-nu.apk
```

This will generate a folder `vrt-nu`.  
Locate the file `AndroidManifest.xml`  
Locate the folder `res`  
Create a new directory underneath `res` named `xml` if it does exist yet.  
Create a file `network_security_config.xml` under `res` > `raw`.  
Paste following contents  

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true">
        <trust-anchors>
            <certificates src="system"/>
            <certificates src="user"/>
        </trust-anchors>
    </base-config>
</network-security-config>
```

> This will force Android to accept any System and most importantly user installed certificates.  

Open the `AndroidManifest.xml`  
Locate the `<application>`  tag.  
Add the `network_security_config`.  

```xml
<application android:networkSecurityConfig="@xml/network_security_config" ...
```

Execute following command to generate a new APK from our changes

```
apktool b vrt-nu
```

Inside the `vrt_nu/dist` you will find your new APK  

### Resign

To make lives easier an existing key is provided in this repo.  (`resign-dummy-key.keystore`)  

#### ZIP Align

```
zipalign -c 4 vrt_nu/dist/vrt-nu.apk
```

> zipalign can be found in your android-sdk location. In one of the downloaded `build-tools/`

```
apksigner sign --ks resign-dummy-key.keystore vrt_nu.apk
Keystore password for signer #1: android 
```

### Install

```
adb install vrt_nu.apk
```
