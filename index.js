import PropTypes from 'prop-types';
import React, { Component } from 'react'
import {
  requireNativeComponent,
  View,
  UIManager,
  findNodeHandle,
  ViewPropTypes,
} from 'react-native';

import MapMarker from './AMapMarker'
import MapPolyline from './AMapPolyline'

class MapView extends Component {
  constructor(props) {
    super(props);
    this._onChange = this._onChange.bind(this)
    this._onMapClick = this._onMapClick.bind(this)
    this._onMarkerClick = this._onMarkerClick.bind(this)
    this._onMyLocationChange = this._onMyLocationChange.bind(this)
    this._onMapScreenShot = this._onMapScreenShot.bind(this)
  }
  animateToRegion(region) {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this),
      UIManager.AMapView.Commands.animateToRegion,
      [region,]
    );
  }
  getMapScreenShot() {
      UIManager.dispatchViewManagerCommand(
        findNodeHandle(this),
        UIManager.AMapView.Commands.getMapScreenShot,
        [1,]
      );
   }
  _onChange(event: Event) {
    if (event.nativeEvent.continuous) {
      if (this.props.onRegionChange) this.props.onRegionChange(event.nativeEvent.region);
      return
    }
    if (this.props.onRegionChangeComplete) this.props.onRegionChangeComplete(event.nativeEvent.region);
  }
  _onMapClick(event: Event) {
    if (this.props.onPress) this.props.onPress(event.nativeEvent)
  }
  _onMarkerClick(event: Event) {
    if (this.props.onMarkerClick) this.props.onMarkerClick(event.nativeEvent)
  }
  _onMyLocationChange(event: Event) {
    if (this.props.onMyLocationChange) this.props.onMyLocationChange(event.nativeEvent)
  }
  _onMapScreenShot(event: Event) {
    if (this.props.onMapScreenShot) this.props.onMapScreenShot(event.nativeEvent)
  }
  render() {
    return <AMapView {...this.props}
      onChange={this._onChange}
      onMapClick={this._onMapClick}
      onMarkerClick={this._onMarkerClick}
      onMyLocationChange={this._onMyLocationChange}
      onMapScreenShot={this._onMapScreenShot}
      />;
  }
}


MapView.Marker = MapMarker
MapView.Polyline = MapPolyline

MapView.propTypes = {
  ...ViewPropTypes,
  // custom
  mapType: PropTypes.oneOf([
    "standard",
    "satellite",
    "night",
    "navi",
    "none"
  ]),
  //myIcon: PropTypes.string,
  //showsUserLocation: PropTypes.bool,
  region: PropTypes.object,
  onPress: PropTypes.func,
  onRegionChange: PropTypes.func,
  onRegionChangeComplete: PropTypes.func,
  //markers: PropTypes.array,
  //polyline: PropTypes.array,
};

var AMapView = requireNativeComponent(`AMapView`, MapView, {
  nativeOnly: {onChange: true}
});

module.exports = MapView;
