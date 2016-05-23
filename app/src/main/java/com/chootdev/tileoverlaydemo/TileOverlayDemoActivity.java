package com.chootdev.tileoverlaydemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class TileOverlayDemoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private float maxZoomLevel;

    private static final String MOON_MAP_URL_FORMAT =
            "http://fmdev.ivivacloud.com/LayoutUtil/tile/31/b7fa30b1-79f1-4173-bc30-084cd64c3fdc/%d/%d/%d";

    private TileOverlay mCustomTileLayout;
    MarkerOptions markerOptions;
    Marker marker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tile_overlay_demo);

        maxZoomLevel = 5;

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        GoogleMap map = mapFragment.getMap();
        addMarkers(map);

        reverseZoomOnMaxLevelReached(map);

        addMarkerClickListner(map);

        setMapClickListner(map);
        setMapMarkerDragListner(map);
        onMapReady(mapFragment.getMap());
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        changeMapUISettings(map);

        TileProvider tileProvider = getTileProvider();
        mCustomTileLayout = map.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
    }

    private void setMapMarkerDragListner(final GoogleMap map) {
        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
                Log.d("System out", "onMarkerDragStart..." + arg0.getPosition().latitude + "..." + arg0.getPosition().longitude);
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                Log.d("System out", "onMarkerDragEnd..." + arg0.getPosition().latitude + "..." + arg0.getPosition().longitude);
                map.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
            }

            @Override
            public void onMarkerDragEnd(Marker arg0) {
                Log.i("System out", "onMarkerDrag...");
            }
        });
    }

    private void setMapClickListner(final GoogleMap map) {
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (marker != null)
                    marker.remove();

                markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                // set marker icons and dragable effect
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("mark", 100, 100)));
                markerOptions.draggable(true);

                // Animating to the touched position
                map.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                marker = map.addMarker(markerOptions);
            }
        });
    }

    @NonNull
    private TileProvider getTileProvider() {
        return new UrlTileProvider(256, 256) {
            @Override
            public synchronized URL getTileUrl(int x, int y, int zoom) {

                String s = String.format(Locale.US, MOON_MAP_URL_FORMAT, zoom, x, y);

                URL url = null;
                try {
                    url = new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
                System.out.println(url);
                return url;
            }
        };
    }

    private void changeMapUISettings(GoogleMap map) {
        map.setMapType(GoogleMap.MAP_TYPE_NONE);

        map.getUiSettings().setCompassEnabled(false);   // remove compass view
        map.getUiSettings().setMapToolbarEnabled(false);   // remove toolbar
        map.getUiSettings().setRotateGesturesEnabled(false);   // remove rotate gesture from this
        map.getUiSettings().setMyLocationButtonEnabled(false);   // remove gps button
        map.getUiSettings().setTiltGesturesEnabled(false);   // remove two finger tlit feature
    }

    private void addMarkers(GoogleMap map) {
        // custom marker
        map.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Hello world")
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("markbig", 65, 100))) // set size according to image u get
                .draggable(true)); // make marker drag enable

        // default marker
        map.addMarker(new MarkerOptions()
                .position(new LatLng(20, 0))
                .title("Hello world"));
    }

    private void reverseZoomOnMaxLevelReached(final GoogleMap map) {
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (map.getCameraPosition().zoom > maxZoomLevel) {
                    map.animateCamera(CameraUpdateFactory.zoomTo(maxZoomLevel - 1));
                }
            }
        });
    }

    private void addMarkerClickListner(GoogleMap map) {
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getApplicationContext(), "Marker Clicked. MId : " + marker.getId() + "\nlat :" +
                        marker.getPosition().latitude + "\nlon" + marker.getPosition().longitude, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }


    public Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    public void setFadeIn(View v) {
        if (mCustomTileLayout == null) {
            return;
        }
        mCustomTileLayout.setFadeIn(((CheckBox) v).isChecked());
    }

}