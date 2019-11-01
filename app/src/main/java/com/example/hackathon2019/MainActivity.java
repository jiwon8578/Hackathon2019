package com.example.hackathon2019;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    LinearLayout linearLayoutTmap;
    TMapView tmapView;
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);
        tmapView = new TMapView(this);

        tmapView.setSKTMapApiKey("42856b74-3604-474f-ad72-df6d1b5aaca0");
        linearLayoutTmap.addView(tmapView);

        tmapView.setIconVisibility(true);

        setGps();
        try {
            TMapMarkerItem tItem = new TMapMarkerItem();
            tItem.setVisible(TMapMarkerItem.VISIBLE);
            TMapData tmapdata = new TMapData();
            TMapPoint tpoint = new TMapPoint(latitude, longitude);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.icon2);


            ArrayList arTMapPOIItem_3 = tmapdata.findAroundNamePOI(tpoint, "맛집");
            for (int i = 0; i < arTMapPOIItem_3.size(); i++) {
                TMapPOIItem item = (TMapPOIItem) arTMapPOIItem_3.get(i);
                TMapPoint point = item.getPOIPoint();
                tItem.setTMapPoint(point);
                tItem.setIcon(bitmap); // 마커 아이콘 지정
                //tItem.setPosition(0.5,1.0);
                tmapView.addMarkerItem("tItem", tItem);

            }
        }catch (Exception e){

        }

    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {


            if (location != null) {
                try {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    tmapView.setLocationPoint(longitude, latitude);
                    tmapView.setCenterPoint(longitude, latitude);

                }catch (Exception e){
                }

            }


        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    public void setGps() {
        final LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자(실내에선 NETWORK_PROVIDER 권장)
                1000, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);
    }
}
