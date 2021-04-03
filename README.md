# react-native-ankur-forground-service-location

## Getting started

`$ npm install react-native-foreground-location-service-ankur --save`

### Mostly automatic installation

`$ react-native link react-native-foreground-location-service-ankur`

## Usage
```javascript
import {
  DeviceEventEmitter,
} from 'react-native';
import AnkurForgroundServiceLocation from 'react-native-foreground-location-service-ankur';

// TODO: What to do with the module?
const EventEmitter = Platform.select({
  android: () => DeviceEventEmitter,
})();


EventEmitter.addListener('locationFetchingBackground', (location) => {
    // this will be executed once after 5 seconds
    console.log('location recevied',location);
  });


const notificationConfig ={
    title:"Fetching your location",
    icon:"ic_notification",
    color:"#0000ff"
  }
   // start Service
  AnkurForgroundServiceLocation.open(notificationConfig);

  //stop service
  AnkurForgroundServiceLocation.close();
```
