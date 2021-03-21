# react-native-ankur-forground-service-location

## Getting started

`$ npm install react-native-foreground-location-service-ankur --save`

### Mostly automatic installation

`$ react-native link react-native-foreground-location-service-ankur`

## Usage
```javascript
import {
  DeviceEventEmitter,
  NativeAppEventEmitter,
} from 'react-native';
import AnkurForgroundServiceLocation from 'react-native-foreground-location-service-ankur';

// TODO: What to do with the module?
const EventEmitter = Platform.select({
  ios: () => NativeAppEventEmitter,
  android: () => DeviceEventEmitter,
})();


EventEmitter.addListener('locationFetchingBackground', (location) => {
    // this will be executed once after 5 seconds
    console.log('location recevied',location);
  });


   // start Service
  AnkurForgroundServiceLocation.open();

  //stop service
  AnkurForgroundServiceLocation.close();
```
