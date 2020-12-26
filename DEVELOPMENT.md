# Run Configs
Prevent `git` from trying to update.

```sh
git update-index --assume-unchanged .idea/runConfigurations/*
```

## Sample
To run the `:sample` module you will need to add your credentials.  

You can do so by adding to the provided run configurations:  
> `<option name="PROGRAM_PARAMETERS" value="john.doe@domain.com password" />`

## Tests
Install the [Kotest IntelliJ Plugin](https://kotest.io/docs/intellij/intellij-plugin.html) to be able to run tests.

Add username and password to run configs to be able to run/test locally.  
You can find it under the section `<envs>`

## Tests command line
If you only want to run tests in command line add following to `local.properties`

```properties
vrtnu.username=
vrtnu.password=

vtmgo.username=
vtmgo.password=

vier.username=
vier.password=
```

Run tests like normal
```sh
./gradlew test
```

The credentials will be injected for you.
