# Permission Component

## Prepare

```
cp src/main/java/eu/organicity/Config.java.example src/main/java/eu/organicity/Config.java
```

## Build war

```
mvn package
```

## Deploy on Wildfly

```
mvn clean wildfly:undeploy wildfly:deploy
```

