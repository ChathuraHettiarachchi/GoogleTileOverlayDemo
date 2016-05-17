package com.chootdev.tileoverlaydemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class TileOverlayDemoActivity extends AppCompatActivity implements OnMapReadyCallback {

    /**
     * This returns moon tiles.
     */

    private float maxZoomLevel;

    private static final String MOON_MAP_URL_FORMAT =
            "http://fmdev.ivivacloud.com/LayoutUtil/tile/30/77bd552f-e477-4ddc-919b-ba0285b70afb/%d/%d/%d";

    private TileOverlay mCustomTileLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tile_overlay_demo);

        maxZoomLevel = 5;

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        map.setMapType(GoogleMap.MAP_TYPE_NONE);

        map.getUiSettings().setCompassEnabled(false);   // remove compass view
        map.getUiSettings().setMapToolbarEnabled(false);   // remove toolbar
        map.getUiSettings().setRotateGesturesEnabled(false);   // remove rotate gesture from this
        map.getUiSettings().setMyLocationButtonEnabled(false);   // remove gps button
        map.getUiSettings().setTiltGesturesEnabled(false);   // remove two finger tlit feature

        TileProvider tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public synchronized URL getTileUrl(int x, int y, int zoom) {

                String s = String.format(Locale.US, MOON_MAP_URL_FORMAT, zoom, x, y);

                URL url = null;
                try {
                    url = new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
                return url;
            }
        };

        mCustomTileLayout = map.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(10, 10))
                .title("Hello world"));

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (map.getCameraPosition().zoom > maxZoomLevel) {
                    map.animateCamera(CameraUpdateFactory.zoomTo(maxZoomLevel-1));
                }
            }
        });
    }

    public void setFadeIn(View v) {
        if (mCustomTileLayout == null) {
            return;
        }
        mCustomTileLayout.setFadeIn(((CheckBox) v).isChecked());
    }
}

