package com.example.hackathon2019;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;

public class MapPoi extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

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
                setContentView(R.layout.map);

                final double avglat;
                final double avglng;

                Bundle bundle = getIntent().getExtras();
                avglat = bundle.getDouble("latitude");
                avglng = bundle.getDouble("longitude");

                final String[] list = {"숙박", "쇼핑", "주요시설물", "은행", "ATM", "편의점",
                        "미용실", "이발소", "대형마트", "화장실", "공원", "커피", "음식", "레저", "호텔",  "마트", "식음료", "TV맛집", "카페", "한식", "중식", "일식", "양식"
                        , "패밀리레스토랑", "전문음식점", "피자", "치킨", "디저트", "제과점", "패스트푸드", "교통", "버스",  "버스정류장", "지하철",
                        "놀거리",  "영화관", "노래방", "PC방",  "공연장", "문화시설"};


                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);
                tmapView = new TMapView(this);

                tmapView.setSKTMapApiKey("42856b74-3604-474f-ad72-df6d1b5aaca0");
                linearLayoutTmap.addView(tmapView);

                tmapView.setTMapPoint(avglat, avglng);
                tmapView.setIconVisibility(true);



                //tmapgps = new TMapGpsManager(MapPoi.this);
                //tmapgps.setMinTime(1000);
                //tmapgps.setMinDistance(5);
                //tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);

                //tmapgps.setProvider(tmapgps.GPS_PROVIDER); //gps로 현 위치 잡기
                //tmapgps.OpenGps();

                Spinner spinner = (Spinner) findViewById(R.id.spinner);
                ArrayAdapter<String> adapter;
                adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
                spinner.setAdapter(adapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                TMapPoint point = new TMapPoint(avglat, avglng); //아무위치나..
                                TMapPoint point1 = new TMapPoint(MainActivity.curLat,MainActivity.curLng);
                                String categoryname = list[position];

                                try {
                                        TMapData tmapdata = new TMapData();

                                        tmapdata.findAroundNamePOI(point, categoryname, 1, 99, new TMapData.FindAroundNamePOIListenerCallback() {
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
                                        tmapdata.findPathData(point1,point, new TMapData.FindPathDataListenerCallback() {
                                                @Override
                                                public void onFindPathData(TMapPolyLine polyLine) {
                                                        polyLine.setLineColor(Color.BLUE);
                                                        polyLine.setLineWidth(2);
                                                        tmapView.addTMapPath(polyLine);
                                                        tmapView.addTMapPolyLine("Line1", polyLine);
                                                }
                                        });


                                }catch (Exception e){
                                        e.printStackTrace();
                                }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                });



        }
}

