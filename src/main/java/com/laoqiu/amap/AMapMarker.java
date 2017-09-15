package com.laoqiu.amap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.ImageView;

import android.widget.LinearLayout;

import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.react.bridge.ReadableMap;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import javax.annotation.Nullable;

public class AMapMarker extends AMapFeature {

    private MarkerOptions markerOptions;
    private Marker marker;
    private int width;
    private int height;
    private String identifier;

    private LatLng position;
    private String title;
    private String snippet;

    private boolean anchorIsSet;
    private float anchorX;
    private float anchorY;

    private final Context context;
    private final View view;

    private float markerHue = 0.0f; // should be between 0 and 360
    private BitmapDescriptor iconBitmapDescriptor;
    private Bitmap iconBitmap;

    private boolean hasCustomMarkerView = false;
    private float rotation = 0.0f;
    private boolean flat = false;
    private boolean draggable = false;
    private int zIndex = 0;
    private float opacity = 1.0f;

    private final DraweeHolder<?> logoHolder;
    private DataSource<CloseableReference<CloseableImage>> dataSource;
    private final ControllerListener<ImageInfo> mLogoControllerListener =
            new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(
                        String id,
                        @Nullable final ImageInfo imageInfo,
                        @Nullable Animatable animatable) {
                    CloseableReference<CloseableImage> imageReference = null;
                    try {
                        imageReference = dataSource.getResult();
                        if (imageReference != null) {
                            CloseableImage image = imageReference.get();
                            if (image != null && image instanceof CloseableStaticBitmap) {
                                CloseableStaticBitmap closeableStaticBitmap = (CloseableStaticBitmap) image;
                                Bitmap bitmap = closeableStaticBitmap.getUnderlyingBitmap();
                                if (bitmap != null) {
                                    bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                    iconBitmap = bitmap;
                                    iconBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
                                }
                            }
                        }
                    } finally {
                        dataSource.close();
                        if (imageReference != null) {
                            CloseableReference.closeSafely(imageReference);
                        }
                    }
                    update();
                }
            };

    public AMapMarker(Context context) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        this.view = inflater.inflate(R.layout.layout_bubble, null);
        logoHolder = DraweeHolder.create(createDraweeHierarchy(), context);
        logoHolder.onAttach();
    }

    private GenericDraweeHierarchy createDraweeHierarchy() {
        return new GenericDraweeHierarchyBuilder(getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setFadeDuration(0)
                .build();
    }

    public void setCoordinate(ReadableMap coordinate) {
        position = new LatLng(coordinate.getDouble("latitude"), coordinate.getDouble("longitude"));
        if (marker != null) {
            marker.setPosition(position);
        }
        update();
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
        update();
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setTitle(String title) {
        this.title = title;
        if (marker != null) {
            marker.setTitle(title);
        }
        update();
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
        if (marker != null) {
            marker.setSnippet(snippet);
        }
        update();
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
        if (marker != null) {
            marker.setRotateAngle(rotation);
        }
        update();
    }

    public void setFlat(boolean flat) {
        this.flat = flat;
        if (marker != null) {
            marker.setFlat(flat);
        }
        update();
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
        if (marker != null) {
            marker.setDraggable(draggable);
        }
        update();
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
        if (marker != null) {
            marker.setZIndex(zIndex);
        }
        update();
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
        if (marker != null) {
            marker.setAlpha(opacity);
        }
        update();
    }

    public void setMarkerHue(float markerHue) {
        this.markerHue = markerHue;
        update();
    }

    public void setAnchor(double x, double y) {
        anchorIsSet = true;
        anchorX = (float) x;
        anchorY = (float) y;
        if (marker != null) {
            marker.setAnchor(anchorX, anchorY);
        }
        update();
    }

    //public void setCalloutAnchor(double x, double y) {
    //    calloutAnchorIsSet = true;
    //    calloutAnchorX = (float) x;
    //    calloutAnchorY = (float) y;
    //    if (marker != null) {
    //        marker.setInfoWindowAnchor(calloutAnchorX, calloutAnchorY);
    //    }
    //    update();
    //}

    public void setImage(String uri) {
        if (uri == null) {
            iconBitmapDescriptor = null;
            update();
        } else if (uri.startsWith("http://") || uri.startsWith("https://") ||
                uri.startsWith("file://")) {
            ImageRequest imageRequest = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(uri))
                    .build();

            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            dataSource = imagePipeline.fetchDecodedImage(imageRequest, this);
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(imageRequest)
                    .setControllerListener(mLogoControllerListener)
                    .setOldController(logoHolder.getController())
                    .build();
            logoHolder.setController(controller);
        } else {
            iconBitmapDescriptor = getBitmapDescriptorByName(uri);
            update();
        }
    }

    public MarkerOptions getMarkerOptions() {
        if (markerOptions == null) {
            markerOptions = createMarkerOptions();
        }
        return markerOptions;
    }

    @Override
    public void addView(View child, int index) {
        super.addView(child, index);
        // if children are added, it means we are rendering a custom marker
        // if (!(child instanceof AMapCallout)) {
        // }
        hasCustomMarkerView = true;
        update();
    }

    @Override
    public Object getFeature() {
        return marker;
    }

    @Override
    public void addToMap(AMap map) {
        marker = map.addMarker(getMarkerOptions());
    }

    @Override
    public void removeFromMap(AMap map) {
        marker.remove();
        marker = null;
    }

    private BitmapDescriptor getIcon() {
        if (hasCustomMarkerView) {
            // creating a bitmap from an arbitrary view
            if (iconBitmapDescriptor != null) {
                //Bitmap viewBitmap = createDrawable();
                //int width = Math.max(iconBitmap.getWidth(), viewBitmap.getWidth());
                //int height = Math.max(iconBitmap.getHeight(), viewBitmap.getHeight());
                //Bitmap combinedBitmap = Bitmap.createBitmap(width, height, iconBitmap.getConfig());
                //Canvas canvas = new Canvas(combinedBitmap);
                //canvas.drawBitmap(iconBitmap, 0, 0, null);
                //canvas.drawBitmap(viewBitmap, 0, 0, null);
                //return BitmapDescriptorFactory.fromBitmap(combinedBitmap);
                ImageView image = (ImageView) this.view.findViewById(R.id.icon);
                Bitmap bitmap = MLRoundedImageView.getCroppedBitmap(Bitmap.createScaledBitmap(iconBitmap, 120, 120, true), 60);
                image.setImageBitmap(bitmap);
                return BitmapDescriptorFactory.fromView(this.view);
            } else {
                return BitmapDescriptorFactory.fromBitmap(createDrawable());
            }
        } else if (iconBitmapDescriptor != null) {
            // use local image as a marker
            return iconBitmapDescriptor;
        } else {
            // render the default marker pin
            return BitmapDescriptorFactory.defaultMarker(this.markerHue);
        }
    }

    private MarkerOptions createMarkerOptions() {
        MarkerOptions options = new MarkerOptions();
        if (anchorIsSet) options.anchor(anchorX, anchorY);
        //if (calloutAnchorIsSet) options.infoWindowAnchor(calloutAnchorX, calloutAnchorY);
        options.position(position);
        options.title(title);
        options.snippet(snippet);
        options.rotateAngle(rotation);
        options.setFlat(flat);
        options.draggable(draggable);
        options.zIndex(zIndex);
        options.alpha(opacity);
        options.icon(getIcon());
        return options;
    }

    public void update() {
        if (marker == null) {
            return;
        }

        marker.setIcon(getIcon());
        //marker.setPosition(position);

        if (anchorIsSet) {
            marker.setAnchor(anchorX, anchorY);
        } else {
            marker.setAnchor(0.5f, 1.0f);
        }

        //if (calloutAnchorIsSet) {
        //    marker.setInfoWindowAnchor(calloutAnchorX, calloutAnchorY);
        //} else {
        //    marker.setInfoWindowAnchor(0.5f, 0);
        //}
    }

    public void update(int width, int height) {
        this.width = width;
        this.height = height;
        update();
    }

    private Bitmap createDrawable() {
        int width = this.width <= 0 ? 100 : this.width;
        int height = this.height <= 0 ? 100 : this.height;

        this.buildDrawingCache();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);

        return bitmap;
    }

    private int getDrawableResourceByName(String name) {
        return getResources().getIdentifier(
                name,
                "drawable",
                getContext().getPackageName());
    }

    private BitmapDescriptor getBitmapDescriptorByName(String name) {
        return BitmapDescriptorFactory.fromResource(getDrawableResourceByName(name));
    }

}
