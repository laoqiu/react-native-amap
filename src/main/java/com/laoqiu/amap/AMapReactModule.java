package com.laoqiu.amap;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

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
