package com.laoqiu.amap;

//import android.app.Activity;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class AMapReactPackage implements ReactPackage {

    private AMapViewManager glManager;

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        AMapReactModule module = new AMapReactModule(reactContext);
        module.setPackage(this);
        modules.add(module);
        return modules;
    }

    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        glManager = new AMapViewManager();
        return Arrays.<ViewManager>asList(
                glManager
        );
    }

    public AMapViewManager getManager() {
        return glManager;
    }
}
