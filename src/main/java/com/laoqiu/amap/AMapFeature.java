package com.laoqiu.amap;

import android.content.Context;

import com.facebook.react.views.view.ReactViewGroup;
import com.amap.api.maps.AMap;

public abstract class AMapFeature extends ReactViewGroup {
    public AMapFeature(Context context) {
        super(context);
    }

    public abstract void addToMap(AMap map);

    public abstract void removeFromMap(AMap map);

    public abstract Object getFeature();
}
