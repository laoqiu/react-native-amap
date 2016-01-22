package com.laoqiu.amap;

import android.content.Context;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;

import javax.annotation.Nullable;

public class AMapReactModule extends ReactContextBaseJavaModule {

    private Context context;
    private AMapReactPackage aPackage;

    public AMapReactModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
    }

    @Override
    public String getName() {
        return "AMapManager";
    }

    @Override
    public @Nullable Map<String, Object> getConstants() {
        HashMap<String, Object> constants = new HashMap<String, Object>();
        return constants;
    }

    public void setPackage(AMapReactPackage aPackage) {
        this.aPackage = aPackage;
    }
}
