# Moxa I/O
This interface is a Java interface for controlling a MOXA device. The Moxa.java file is used for the Moxa ioLogik E1200 Series Remote I/O devices. There are 11 different product configurations for the ioLogik E1200 series.
* E1210
* E1211
* E1212
* E1213
* E1214
* E1240
* E1241
* E1242
* E1260  
* E1261
* E1262

### Disclaimer:
All examples for this library are abstract. The final design pattern of how the methods in this library are used is completely up to the developer of the project (you).

## Moxa Documentation
Since this device is an electrical component, please read the documentation about the device before starting to work with it.

### Product page
https://www.moxa.com/en/products/industrial-edge-connectivity/controllers-and-ios/universal-controllers-and-i-os/iologik-e1200-series

### Manual
https://www.moxa.com/getmedia/d23ee0e8-e999-4d75-b50e-305181294943/moxa-iologik-e1200-series-manual-v15.7.pdf


Please refer to the documentation for help on how to call specific commands

## Interface Java Doc
https://swdevteam.com/moxa/javadoc/javadoc.html

## Installation
Place this file in your /src/project/package directory to get started. Once it is placed it is rather simple to use.


## Usage

##### Initialize the device:    

 ```
  Moxa mx;                            // use somewhere that the instance of the class can be accessed

  mx = new Moxa();                    // provides path to driver
  int initStatus = mx.initDevice();   // initializes socket for device communication
  ```                                                
##### Connecting to a device:
> connectToDevice( String ipaddress, String password )

The connection should be made with the existing Moxa class instance (mx).

```
  int handle = mx.connectToDevice("10.0.0.22", "myPassword");
```
Multiple handles can be made with the help of a loop. Here we use an ArrayList and iterate through them. Again, please come up with your own specific data structures that will suit your project.
```
  public List<String> ipAddresses = new ArrayList<>();                
  public List<Integer> handles = new ArrayList<>();

  for(int i=0;i<ipAddresses.size();i++){
    handles.add( mx.connectToDevice( ipAddresses.get(i) ,  "myPassword" ) );
  }

```

##### Reading Digital Inputs:
> readDI( int connectionHandle, byte channel, byte count )

This will read DI 0 of device 100001.
```
  int result = mx.readDI(100001, (byte) 0, (byte) 1 );

```
To read multiple channels simply change the count parameter to how many channels you want to monitor.


##### Reading Digital Outputs:
> readOutput(int connection, byte slot, byte channel)

This will read DO 2 on slot 0 of device 100001.
```
  int result = mx.readOutput( 100001, (byte) 0, (byte) 2 )
```

##### Writing Digital Outputs:
> writeOutput(int connection, byte channel, byte count, int value)

Turn the output on:
```
  int result = writeOutput( 100001, (byte) 0, (byte) 1, 1 );

```
Turn the output off:
```
  int result = writeOutput( 100001, (byte) 0, (byte) 1, 0 );

```

##### Read Analog Inputs:
> readAnalogChannel( int connection, byte channel, byte count )

Reads analog voltage on AI 1:
```
  double result = mx.readAnalogChannel( 100001 , (byte) 1, (byte) 1 );
```


##### Disconnect Device
> disconnect(int connection)

Disconnects device 100001

```
  mx.disconnect( 100001 );
```


##### Exit Device
> exitDevice()

```
  mx.exitDevice();
```
