# Run Configs
Install the [Kotest Plugin](https://kotest.io/docs/intellij/intellij-plugin.html) if you want to be able to run tests from IDEA.

## Sample and Tests
To run the `:sample` module or end 2 end tests you will need to add your credentials to the `local.properties`

```properties
vrtnu.username=
vrtnu.password=

vtmgo.username=
vtmgo.password=

vier.username=
vier.password=
```

Afterwards you can run tests like normal.
Use the Kotest plugin from within IDEA or from CLI
```sh
./gradlew test
```

Prevent `git` from trying to update these run configurations.

```sh
git update-index --assume-unchanged .idea/runConfigurations/*
```
