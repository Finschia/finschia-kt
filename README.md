# finschia-kt

gRPC client library in kotlin for finschia (https://github.com/Finschia/finschia).

Requires [finschia-sdk v0.49.0](https://github.com/Finschia/finschia-sdk/tree/v0.49.0)

Current finschia-sdk version applied to finschia-kt/protobuf is [finschia-sdk v0.49.0](https://github.com/Finschia/finschia-sdk/tree/v0.49.0)

## Using SDK

* finschia-kt SDK(crypto)
```kotlin
val sdkCryptoVersion = "0.2.2"
dependencies {
    implementation("network.finschia:finschia-kt-crypto:$sdkCryptoVersion")
}
```
* finschia-kt SDK(proto)
```kotlin
val sdkProtoVersion = "4.0.0"

dependencies {
   implementation("network.finschia:finschia-proto:$sdkProtoVersion")
}
```

## Build

You can build the project by following command, which also runs all the unit tests:

```shell
$ ./gradlew build
```

## How to use

### 1. How to get a Bech32 address from the KeyWallet

You can get the address by:

```kotlin
val alicePubKey: PubKey = ...
val aliceAddress = Address(alicePubKey).toBech32(accountPrefix)
```

### 2. How to validate a Bech32 address

You can check the address by:

```kotlin
val testingAddress: String = "link..."
val tested = Address(testingAddress) // this will throw if the address is invalid
```

### 3. How to create raw transaction for LN transfer

```kotlin
val unsignedBytes = ConcreteScenarios.createBankMsgSendSingleDirect(
    aliceAddress, // sender address
    bobAddress,   // recipient address
    10000,        // amount in base denom
    alicePubKey,  // sender public key
    1,            // sender sequence
    100000,       // timeout height
    "",           // memo
)
```

### 4. How to create a signature

```kotlin
val signedBytes = ConcreteScenarios.signSingleDirect(
    unsignedBytes, // unsigned tx bytes
    aliceWallet,   // signer wallet
    0,             // signer account number
)
```

## Examples

We provide simple client programs. 

`with-tx-wrapper-example` and `without-tx-wrapper-example` show how to create tx and send it. 
The former is using the tx wrapper, while the latter does not use the wrapper.

`multisig-example` shows how to create multisig tx and send it.

`query-example` shows how to query account information.

## How to run `without-tx-wrapper-example`

### 1. Run finschia-sdk simapp

Build with reference to [Quick Start build](https://github.com/Finschia/finschia-sdk/tree/v0.49.0#quick-start).

Initialize and configure simapp.

```shell
$ export TEST_MNEMONIC="mind flame tobacco sense move hammer drift crime ring globe art gaze cinnamon helmet cruise special produce notable negative wait path scrap recall have"
$ simd init validator --home ~/simapp --chain-id sim
$ simd keys add alice --keyring-backend=test --home ~/simapp --recover --account=0 <<< ${TEST_MNEMONIC}
$ simd keys add bob --keyring-backend=test --home ~/simapp --recover --account=1 <<< ${TEST_MNEMONIC}
$ simd add-genesis-account $(simd --home ~/simapp keys show alice -a --keyring-backend=test) 100000000000stake,100000000000tcony --home ~/simapp
$ simd add-genesis-account $(simd --home ~/simapp keys show bob -a --keyring-backend=test) 100000000000stake,100000000000tcony --home ~/simapp
$ simd gentx alice 10000000000stake --keyring-backend=test --home ~/simapp --chain-id=sim
$ simd collect-gentxs --home ~/simapp
```

Run simapp.

```shell
$ simd start --home ~/simapp
```

### 2. Replace hard-coded arguments

To confirm it's possible to send transactions to the chain created above using finschia-kt, replace the following
three parts of the [without-tx-wrapper-example/src/main/kotlin/network/link/sdk/example/Client.kt](https://github.com/Finschia/finschia-kt/blob/main/examples/without-tx-wrapper-example/src/main/kotlin/network/link/sdk/example/Client.kt).

1. **Chain ID**: constant of the name `CHAIN_ID` in `ConcreteScenarios`
   ```go
   private const val CHAIN_ID = "sim"
   private const val BASE_DENOM = "tcony"
   ```
   with the following output:
   ```shell
   $ cat ~/simapp/config/genesis.json | jq '.chain_id'
   ```
2. **Account sequences**: 5th argument of `ConcreteScenarios.createBankMsgSendSingleDirect()`
   ```go
   val unsignedBytes = ConcreteScenarios.createBankMsgSendSingleDirect(
       aliceAddress, // sender address
       bobAddress,   // recipient address
       10000,        // amount in base denom
       alicePubKey,  // sender public key
       1,            // sender sequence
       100000,       // timeout height
       "",           // memo
   )
   ```
   with the following outputs, respectively:
   ```shell
   $ simd q account link146asaycmtydq45kxc8evntqfgepagygelel00h | grep sequence
   $ simd q account link1twsfmuj28ndph54k4nw8crwu8h9c8mh3rtx705 | grep sequence
   ```
3. **Account number**: 3rd argument of `ConcreteScenarios.signSingleDirect`
   ```go
   val signedBytes = ConcreteScenarios.signSingleDirect(
       unsignedBytes, // unsigned tx bytes
       aliceWallet,   // signer wallet
       0,             // signer account number
   )
   ```
   with the following outputs, respectively:
   ```shell
   $ simd q account link146asaycmtydq45kxc8evntqfgepagygelel00h | grep account_number
   $ simd q account link1twsfmuj28ndph54k4nw8crwu8h9c8mh3rtx705 | grep account_number
   ```

### 3. Run java client

Broadcast transaction and prints the hash of transaction.

```shell
$ ./gradlew build
$ ./gradlew :examples:without-tx-wrapper-example:run
```

Query transaction.
```shell
$ simd q tx ${TX_HASH}
```
