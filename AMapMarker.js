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

class MapMarker extends Component {
  constructor(props) {
    super(props);
    this._onPress = this._onPress.bind(this)
  }
  _onPress(event: Event) {
    if (this.props.onPress) this.props.onPress(event.nativeEvent)
  }
  render() {
    let image
    if (this.props.image) {
      image = resolveAssetSource(this.props.image) || {}
      image = image.uri;
    }
    return (
      <AMapMarker
        {...this.props}
        onPress={this._onPress}
        image={image}
        />
    )
  }
}

MapMarker.propTypes = {
  ...ViewPropTypes,
  identifier: PropTypes.string,
  reuseIdentifier: PropTypes.string,
  title: PropTypes.string,
  description: PropTypes.string,
  image: PropTypes.any,
  opacity: PropTypes.number,
  pinColor: PropTypes.string,
  coordinate: PropTypes.shape({
    latitude: PropTypes.number.isRequired,
    longitude: PropTypes.number.isRequired,
  }).isRequired,
  centerOffset: PropTypes.shape({
    x: PropTypes.number.isRequired,
    y: PropTypes.number.isRequired,
  }),
  anchor: PropTypes.shape({
    x: PropTypes.number.isRequired,
    y: PropTypes.number.isRequired,
  }),
  flat: PropTypes.bool,
  draggable: PropTypes.bool,
  onPress: PropTypes.func,
  onSelect: PropTypes.func,
  onDeselect: PropTypes.func,
  onDragStart: PropTypes.func,
  onDrag: PropTypes.func,
  onDragEnd: PropTypes.func,
};

var AMapMarker = requireNativeComponent(`AMapMarker`, MapMarker, {
  nativeOnly: {onChange: true}
});

module.exports = MapMarker;
