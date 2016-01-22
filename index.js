import { PropTypes, requireNativeComponent } from 'react-native';

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
    mode: PropTypes.number,
  }
}

module.exports = requireNativeComponent('RCTAMap', iface);

