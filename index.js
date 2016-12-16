import { PropTypes } from 'react';
import { requireNativeComponent } from 'react-native';

let iface = {
  name: 'RCTAMap',
  propTypes: {
    testID: PropTypes.string,
    accessibilityComponentType: PropTypes.string,
    accessibilityLabel: PropTypes.string,
    accessibilityLiveRegion: PropTypes.string,
    renderToHardwareTextureAndroid: PropTypes.bool,
    importantForAccessibility: PropTypes.string,
    onLayout: PropTypes.bool,
    // custom
    mapType: PropTypes.number,
    showsUserLocation: PropTypes.bool,
    onMapClick: PropTypes.bool,
    onMarkerClick: PropTypes.bool,
    onRegionChange: PropTypes.bool,
    annotations: PropTypes.array,
    region: PropTypes.object,
  }
}

module.exports = requireNativeComponent('RCTAMap', iface);

