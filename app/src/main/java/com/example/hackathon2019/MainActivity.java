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
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    LinearLayout linearLayoutTmap;
    TMapView tmapView;
    double latitude;
    double longitude;
    private TMapGpsManager tmapgps = null;

    @Override
    public void onLocationChange(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        tmapView.setLocationPoint(longitude, latitude);
        tmapView.setCenterPoint(longitude, latitude);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);
        tmapView = new TMapView(this);

        tmapView.setSKTMapApiKey("42856b74-3604-474f-ad72-df6d1b5aaca0");
        linearLayoutTmap.addView(tmapView);

        tmapView.setIconVisibility(true);

        tmapgps = new TMapGpsManager(MainActivity.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);

        //tmapgps.setProvider(tmapgps.GPS_PROVIDER); //gps로 현 위치 잡기
        tmapgps.OpenGps();

        TMapPoint point = new TMapPoint(37.5443003, 126.9722328); //아무위치나..

        try {
            TMapData tmapdata = new TMapData();

            tmapdata.findAroundNamePOI(point, "편의점", 1, 99, new TMapData.FindAroundNamePOIListenerCallback() {
                @Override
                public void onFindAroundNamePOI(ArrayList<TMapPOIItem> poiItem) {
                    for(int i = 0; i < poiItem.size(); i++) {
                        TMapMarkerItem tItem = new TMapMarkerItem(); //마커아이템생성
                        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.icon2); //마커비트맵생성
                        tItem.setIcon(bitmap); // 마커 아이콘 지정
                        tItem.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
                        TMapPOIItem item = poiItem.get(i);
                        Log.d("편의시설", item.getPOIName());
                        Log.d("lat", String.valueOf(latitude));
                        TMapPoint point = item.getPOIPoint();
                        //Log.i("point", point.toString());
                        tItem.setTMapPoint(point);
                        tmapView.addMarkerItem("tItem"+i, tItem);
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
