# eea-listener

### Overview

The EEA Listener is a light Java program that listens for events on an Ethereum
chain complient with the [EEA Off-Chain Trusted Compute Specification
v1](https://entethalliance.org/wp-content/uploads/2019/05/EEA_Off_Chain_Trusted_Compute_Specification_V1_0.pdf)
and forward Work Order events to to an off-chain worker service via JSON-RPC.

### Run with Gradle

*Please first update your config located in `./src/main/resources/application.yml`*

```
gradle bootRun --refresh-dependencies
```
