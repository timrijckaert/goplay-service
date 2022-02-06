# Run Configs
Install the [Kotest Plugin](https://kotest.io/docs/intellij/intellij-plugin.html) if you want to be able to run tests from IDEA.

## Sample and Tests
To run the end 2 end tests you will need to add your credentials to the `resources` > kotest.properties`

```properties
username=
password=
```

# Charles Root Certificates

## Charles

In order to use Charles as a proxy to investigate API calls a few steps are required.
In `Charles` navigate to **Help** > **SSL Proxying** > **Save Charles Root Certificate**

## Mac OS X
Make sure to add the Charles Root certificate to the Mac OS X Keychain.

Right click in the settings to also trust the certificate.

## JVM
Add the Charles Root certificate to the trusted Java certificates.

Convert the `.pem` to a `.der`

```
openssl x509 -in charles-ssl-proxying-certificate.pem -out charles-ssl-proxying-certificate.der -outform DER
```

Import the `.der` as a trusted JDK certifact.

```
keytool -import -alias charles -keystore cacerts -file ~/Desktop/charles-ssl-proxying-certificate.der
```

See full explanation [here](https://magicmonster.com/kb/prg/java/ssl/pkix_path_building_failed/)
