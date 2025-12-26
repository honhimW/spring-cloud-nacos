# Spring Cloud Nacos

| Module    | Status                                                                                                                                                                                                              |
|-----------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Config    | [![Maven Central](https://img.shields.io/maven-central/v/io.github.honhimw/spring-cloud-starter-nacos-config.svg)](https://central.sonatype.com/artifact/io.github.honhimw/spring-cloud-starter-nacos-config)       |
| Discovery | [![Maven Central](https://img.shields.io/maven-central/v/io.github.honhimw/spring-cloud-starter-nacos-discovery.svg)](https://central.sonatype.com/artifact/io.github.honhimw/spring-cloud-starter-nacos-discovery) |

### Summary

This repository provides a streamlined `spring-cloud-nacos` integration,
designed as a lightweight alternative to `spring-cloud-alibaba-nacos`.

The official library is often significantly delayed in keeping up with Spring Cloud releases and includes many unrelated
components (Sentinel, Seata, RocketMQ, etc.), making it slow and bloated.

To address this, the repository focuses solely on Nacos and also delivers an optimized nacos-client.
The official client is oversized (13 MB+ core plus 9 MB+ `grpc-netty-shaded`).

Even with `classifier: pure`, `grpc-netty-shaded` remains required, resulting in unnecessary bulk.
This version removes all shaded dependencies and aligns library versions with spring-boot-dependencies,
which typically avoids conflicts and eliminates redundant code, significantly reducing package size.

## Usage

```groovy
dependencies {
    runtimeOnly 'io.github.honhimw:spring-cloud-starter-nacos-config'
    runtimeOnly 'io.github.honhimw:spring-cloud-starter-nacos-discovery'
}
```
