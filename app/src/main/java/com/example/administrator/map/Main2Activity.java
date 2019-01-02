package com.example.administrator.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {
public LocationClient locationClient;

private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new MyLocationListener());
        setContentView(R.layout.activity_main2);

        textView=findViewById(R.id.text);
        List<String>permissionList = new ArrayList<>();

        if(ContextCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.READ_PHONE_STATE)!= PackageManager
                .PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager
                .PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
       if(!permissionList.isEmpty()){
            String[]permissions = permissionList.toArray(new String[permissionList.size()]);
           ActivityCompat.requestPermissions(Main2Activity.this,permissions,1);
       }else {
           requestLocation();
       }
    }

    private void requestLocation() {
        initLocation();
        locationClient.start();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        locationClient.setLocOption(option);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length>0){
                    for(int result : grantResults){
                        if(result!=PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"必须同意所以权限才能使用本程序",Toast.LENGTH_SHORT).show();

                            return;
                        }
                    }
                    requestLocation();
                }else {
                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
                default:
        }
    }

    private class MyLocationListener extends BDAbstractLocationListener  {
        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    StringBuilder currentPosition = new StringBuilder();
                    currentPosition.append("经度：").append(bdLocation.getLongitude());
                    currentPosition.append("维度：").append(bdLocation.getLatitude());
                    currentPosition.append("定位方式：");
                    if(bdLocation.getLocType()==BDLocation.TypeGpsLocation){
                        currentPosition.append("GPS");
                    }else if(bdLocation.getLocType()==BDLocation.TypeNetWorkLocation){
                        currentPosition.append("网络");
                    }
                    textView.setText(currentPosition);
                }
            });
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
            super.onConnectHotSpotMessage(s, i);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationClient.stop();
    }
}
