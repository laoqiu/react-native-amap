package com.laoqiu.amap;

import android.os.StrictMode;
import android.location.Location;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Environment;
import android.net.Uri;
import java.text.SimpleDateFormat;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.PolygonOptions;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.Callback;
//import com.facebook.react.uimanager.ReactProp;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.common.MapBuilder;
import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Date;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;


public class AMapViewManager extends ViewGroupManager<MapView> {
    public static final String RCT_CLASS = "AMapView";

    private MapView mapView;
    private AMap map;
    private ThemedReactContext context;
    private EventDispatcher eventDispatcher;
    private LatLngBounds boundsToMove;

    //private final AMap map;
    private final ReactApplicationContext appContext;
    private final Map<Marker, AMapMarker> markerMap = new HashMap<>();
    private final Map<Polyline, AMapPolyline> polylineMap = new HashMap<>();
    private final List<AMapFeature> features = new ArrayList<>();

    public static final int ANIMATE_TO_REGION = 1;
    public static final int GET_MAP_SCREENSHOT = 2;
    private final Map<String, Integer> MAP_TYPES = MapBuilder.of(
            "standard", AMap.MAP_TYPE_NORMAL,
            "satellite", AMap.MAP_TYPE_SATELLITE,
            "night", AMap.MAP_TYPE_NIGHT,
            "navi", AMap.MAP_TYPE_NAVI,
            "none", AMap.MAP_TYPE_BUS
    );

    public AMapViewManager(ReactApplicationContext context) {
        this.appContext = context;
    }

    @Override
    public String getName() {
        return RCT_CLASS;
    }

    @Override
    protected void addEventEmitters(ThemedReactContext content, final MapView mapView) {
        final AMap map = mapView.getMap();
        map.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                WritableMap event = Arguments.createMap();
                WritableMap region = Arguments.createMap();
                LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
                region.putDouble("latitude", position.target.latitude);
                region.putDouble("longitude", position.target.longitude);
                region.putDouble("latitudeDelta", bounds.northeast.latitude - bounds.southwest.latitude);
                region.putDouble("longitudeDelta", bounds.northeast.longitude - bounds.southwest.longitude);
                event.putInt("continuous", 1);
                event.putMap("region", region);
                ReactContext reactContext = (ReactContext) mapView.getContext();
                pushEvent(reactContext, mapView, "topChange", event);
            }
            @Override
            public void onCameraChangeFinish(CameraPosition position) {
                WritableMap event = Arguments.createMap();
                WritableMap region = Arguments.createMap();
                LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
                region.putDouble("latitude", position.target.latitude);
                region.putDouble("longitude", position.target.longitude);
                region.putDouble("latitudeDelta", bounds.northeast.latitude - bounds.southwest.latitude);
                region.putDouble("longitudeDelta", bounds.northeast.longitude - bounds.southwest.longitude);
                event.putInt("continuous", 0);
                event.putMap("region", region);
                ReactContext reactContext = (ReactContext) mapView.getContext();
                pushEvent(reactContext, mapView, "topChange", event);
            }
        });
        map.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                WritableMap event = Arguments.createMap();
                event.putDouble("latitude", point.latitude);
                event.putDouble("longitude", point.longitude);
                ReactContext reactContext = (ReactContext) mapView.getContext();
                pushEvent(reactContext, mapView, "onMapClick", event);
            }
        });
        map.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                WritableMap event;
                AMapMarker markerView = markerMap.get(marker);
                ReactContext reactContext = (ReactContext) mapView.getContext();

                event = makeClickEventData(marker.getPosition());
                event.putString("action", "marker-press");
                event.putString("id", markerView.getIdentifier());
                pushEvent(reactContext, mapView, "onMarkerPress", event);

                event = makeClickEventData(marker.getPosition());
                event.putString("action", "marker-press");
                event.putString("id", markerView.getIdentifier());
                pushEvent(reactContext, markerMap.get(marker), "onPress", event);
                return true;
            }
        });
        //map.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
        //    @Override
        //    public void onMyLocationChange(Location location) {
        //        if (location != null) {
        //            WritableMap event = Arguments.createMap();
        //            event.putDouble("latitude", location.getLatitude());
        //            event.putDouble("longitude", location.getLongitude());
        //            event.putDouble("accuracy", (double) location.getAccuracy());
        //            ReactContext reactContext = (ReactContext) mapView.getContext();
        //            pushEvent(reactContext, mapView, "onMyLocationChange", event);
        //        }
        //    }
        //});
    }

    @Nullable
    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.<String, Object>builder()
                .put("onMapClick", MapBuilder.of("registrationName", "onMapClick"))
                .put("onMarkerPress", MapBuilder.of("registrationName", "onMarkerPress"))
                //.put("onMyLocationChange", MapBuilder.of("registrationName", "onMyLocationChange"))
                .put("onMapScreenShot", MapBuilder.of("registrationName", "onMapScreenShot"))
                .build();
    }

    @Override
    public @Nullable Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
            "animateToRegion", ANIMATE_TO_REGION,
            "getMapScreenShot", GET_MAP_SCREENSHOT
        );
    }

    @Override
    public void receiveCommand(final MapView mapView, int commandId, @Nullable ReadableArray args) {
        switch (commandId){
            case ANIMATE_TO_REGION:
                ReadableMap region = args.getMap(0);
                Double lat = region.getDouble("latitude");
                Double lng = region.getDouble("longitude");
                Double lngDelta = region.getDouble("longitudeDelta");
                Double latDelta = region.getDouble("latitudeDelta");
                LatLngBounds bounds = new LatLngBounds(
                  new LatLng(lat - latDelta / 2, lng - lngDelta / 2), // southwest
                  new LatLng(lat + latDelta / 2, lng + lngDelta / 2)  // northeast
                );
                mapView.getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
                return;
            case GET_MAP_SCREENSHOT:
                mapView.getMap().getMapScreenShot(new AMap.OnMapScreenShotListener() {
                    @Override
                    public void onMapScreenShot(Bitmap snapshot) {
                    }
                    @Override
                    public void onMapScreenShot(Bitmap snapshot, int status) {
                        if(snapshot == null){
                            return;
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                        File tempFile;
                        FileOutputStream fos;
                        try {
                            tempFile = new File(appContext.getCacheDir(), "higo_"+sdf.format(new Date()) + ".png");
                            fos = new FileOutputStream(tempFile);
                            boolean b = snapshot.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            try {
                                fos.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (b) {
                                // 返回数据给前端js
                                String uri = Uri.fromFile(tempFile).toString();
                                WritableMap event = Arguments.createMap();
                                event.putString("uri", uri);
                                ReactContext reactContext = (ReactContext) mapView.getContext();
                                pushEvent(reactContext, mapView, "onMapScreenShot", event);
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return;
        }
    }

    @Override
    protected MapView createViewInstance(ThemedReactContext reactContent) {
        AMapOptions options = new AMapOptions();
        options.zoomControlsEnabled(false);
        //options.zoomGesturesEnabled(false);
        //options.rotateGesturesEnabled(false);
        mapView = new MapView(reactContent, options);
        mapView.onCreate(null);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        map = mapView.getMap();
        eventDispatcher = reactContent.getNativeModule(UIManagerModule.class).getEventDispatcher();
        return mapView;
    }

    @ReactProp(name = "mapType")
    public void setMapType(MapView mapView, @Nullable String mapType) {
        int typeId = MAP_TYPES.get(mapType);
        mapView.getMap().setMapType(typeId);
    }

    @ReactProp(name = "region")
    public void setRegion(MapView mapView, @Nullable ReadableMap region) {
        if (region == null) return;
        AMap map = mapView.getMap();
        Double lat = region.getDouble("latitude");
        Double lng = region.getDouble("longitude");
        Double lngDelta = region.getDouble("longitudeDelta");
        Double latDelta = region.getDouble("latitudeDelta");
        LatLngBounds bounds = new LatLngBounds(
            new LatLng(lat - latDelta / 2, lng - lngDelta / 2), // southwest
            new LatLng(lat + latDelta / 2, lng + lngDelta / 2)  // northeast
        );
        if (mapView.getHeight() <= 0 || mapView.getWidth() <= 0) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 10));
            boundsToMove = bounds;
        } else {
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
        }
    }

    @Override
    public void addView(MapView parent, View child, int index) {
        this.addFeature(parent, child, index);
    }

    public void addFeature(MapView parent, View child, int index) {
        AMap map = parent.getMap();
        if (child instanceof AMapMarker) {
            AMapMarker annotation = (AMapMarker) child;
            annotation.addToMap(map);
            features.add(index, annotation);
            Marker marker = (Marker) annotation.getFeature();
            markerMap.put(marker, annotation);
        } else if (child instanceof AMapPolyline) {
            AMapPolyline polylineView = (AMapPolyline) child;
            polylineView.addToMap(map);
            features.add(index, polylineView);
            Polyline polyline = (Polyline) polylineView.getFeature();
            polylineMap.put(polyline, polylineView);
        } else {
            ViewGroup children = (ViewGroup) child;
            for (int i = 0; i < children.getChildCount(); i++) {
              addFeature(parent, children.getChildAt(i), index);
            }
        }
    }

    @Override
    public int getChildCount(MapView view) {
        return features.size();
    }

    @Override
    public View getChildAt(MapView view, int index) {
        return features.get(index);
    }

    @Override
    public void removeViewAt(MapView parent, int index) {
        AMap map = parent.getMap();
        AMapFeature feature = features.remove(index);
        if (feature instanceof AMapMarker) {
            markerMap.remove(feature.getFeature());
        } else if (feature instanceof AMapPolyline) {
            polylineMap.remove(feature.getFeature());
        }
        feature.removeFromMap(map);
    }

    @Override
    public void updateExtraData(MapView view, Object extraData) {
        // if boundsToMove is not null, we now have the MapView's width/height, so we can apply
        // a proper camera move
        if (boundsToMove != null) {
          HashMap<String, Float> data = (HashMap<String, Float>) extraData;
          float width = data.get("width");
          float height = data.get("height");
          view.getMap().moveCamera(
              CameraUpdateFactory.newLatLngBounds(
                  boundsToMove,
                  (int) width,
                  (int) height,
                  0
              )
          );
          boundsToMove = null;
        }
    }

    public void pushEvent(ReactContext context, View view, String name, WritableMap data) {
        context.getJSModule(RCTEventEmitter.class)
            .receiveEvent(view.getId(), name, data);
    }

    @Override
    public void onDropViewInstance(MapView mapView) {
        mapView.onDestroy();
    }

    public WritableMap makeClickEventData(LatLng point) {
        WritableMap event = new WritableNativeMap();

        WritableMap coordinate = new WritableNativeMap();
        coordinate.putDouble("latitude", point.latitude);
        coordinate.putDouble("longitude", point.longitude);
        event.putMap("coordinate", coordinate);

        Projection projection = map.getProjection();
        Point screenPoint = projection.toScreenLocation(point);

        WritableMap position = new WritableNativeMap();
        position.putDouble("x", screenPoint.x);
        position.putDouble("y", screenPoint.y);
        event.putMap("position", position);

        return event;
    }


}
