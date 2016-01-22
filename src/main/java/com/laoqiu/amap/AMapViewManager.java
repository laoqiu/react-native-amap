package com.laoqiu.amap;
//import android.app.Activity;
import android.util.Log;
import android.os.StrictMode;
//import android.view.MotionEvent;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
//import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.Callback;
import com.facebook.react.uimanager.ReactProp;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;


public class AMapViewManager extends SimpleViewManager<MapView> {
    public static final String RCT_CLASS = "RCTAMap";

    private MapView mapView;

    @Override
    public String getName() {
        return RCT_CLASS;
    }

    @Override
    protected MapView createViewInstance(ThemedReactContext content) {
        mapView = new MapView(content);
        mapView.onCreate(null);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        return mapView;
    }

    @ReactProp(name="mode")
    public void setMode(MapView mapView, int type) {
        mapView.getMap().setMapType(type);
    }

    public MapView getMapView() {
        return mapView;
    }

    // 事件
    //@Override
    //protected void addEventEmitters(final ThemedReactContext reactContext, final MapView view) {

    //}

}
