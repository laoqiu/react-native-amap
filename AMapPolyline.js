import PropTypes from 'prop-types';
import React, { Component } from 'react'
import {
  requireNativeComponent,
  View,
  UIManager,
  findNodeHandle,
  ViewPropTypes,
} from 'react-native';
import resolveAssetSource from 'react-native/Libraries/Image/resolveAssetSource';

class MapPolyline extends Component {
  constructor(props) {
    super(props);
  }
  render() {
    return (
      <AMapPolyline
        {...this.props}
        />
    )
  }
}

MapPolyline.propTypes = {
  ...ViewPropTypes,
  coordinates: PropTypes.arrayOf(PropTypes.shape({
      latitude: PropTypes.number.isRequired,
      longitude: PropTypes.number.isRequired,
    })),
    onPress: PropTypes.func,
    tappable: PropTypes.bool,
    fillColor: PropTypes.string,
    strokeWidth: PropTypes.number,
    strokeColor: PropTypes.string,
    zIndex: PropTypes.number,
    lineCap: PropTypes.oneOf([
      'butt',
      'round',
      'square',
    ]),
    lineJoin: PropTypes.oneOf([
      'miter',
      'round',
      'bevel',
    ]),
    miterLimit: PropTypes.number,
    geodesic: PropTypes.bool,
    lineDashPhase: PropTypes.number,
    lineDashPattern: PropTypes.arrayOf(PropTypes.number),
};

var AMapPolyline = requireNativeComponent(`AMapPolyline`, MapPolyline, {
  nativeOnly: {onChange: true}
});

module.exports = MapPolyline;
