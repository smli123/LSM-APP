package com.thingzdo.ui.control;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

import com.thingzdo.smartplug_udp.R;
import com.thingzdo.ui.smartplug.SmartPlugApplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class DetailBattery2Activity extends Activity {
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SDKInitializer.initialize(SmartPlugApplication.getContext());
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.map_view);
        
        // 显示指定位置
		mBaiduMap = mMapView.getMap();

//	   LatLng p = new LatLng(22.538143,113.931306);
//	   MapStatus mMapStatus = new MapStatus.Builder()
//		        .target(p)
//		        .zoom(18)
//		        .build();
//	   MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
//	   // 刷新地图状态
//	   mBaiduMap.setMapStatus(mMapStatusUpdate);
	   
       // 界面加载时添加绘制图层
       addCustomElementsDemo();
    }
    
    /**
     * 添加点、线、多边形、圆、文字
     */
    public void addCustomElementsDemo() {
        // 添加折线
        LatLng p1 = new LatLng(39.97923, 116.357428);
        LatLng p2 = new LatLng(39.94923, 116.397428);
        LatLng p3 = new LatLng(39.97923, 116.437428);
        LatLng p4 = new LatLng(39.96923, 116.367428);
        LatLng p5 = new LatLng(39.95923, 116.368428);
        LatLng p6 = new LatLng(39.95323, 116.362428);
        LatLng p7 = new LatLng(39.95423, 116.363428);
        LatLng p8 = new LatLng(39.95123, 116.364428);
        List<LatLng> points = new ArrayList<LatLng>();
        points.add(p1);
        points.add(p2);
        points.add(p3);
        points.add(p4);
        points.add(p5);
        points.add(p6);
        points.add(p7);
        points.add(p8);
        OverlayOptions ooPolyline = new PolylineOptions().width(10)
                .color(0xAAFF0000).points(points);
        mBaiduMap.addOverlay(ooPolyline);
        
        // 添加文字 
        LatLng llText = new LatLng(39.86923, 116.397428);
        OverlayOptions ooText = new TextOptions().bgColor(0xAAFFFF00)
        		.fontSize(24).fontColor(0xFFFF00FF)
        		.text("百度地图SDK").rotate(-30).position(llText); 
        mBaiduMap.addOverlay(ooText);
        
        /*
         * // 添加弧线 OverlayOptions ooArc = new
         * ArcOptions().color(0xAA00FF00).width(4) .points(p1, p2, p3);
         * mBaiduMap.addOverlay(ooArc); 
         * // 添加圆 LatLng llCircle = new
         * LatLng(39.90923, 116.447428); OverlayOptions ooCircle = new
         * CircleOptions().fillColor(0x000000FF) .center(llCircle).stroke(new
         * Stroke(5, 0xAA000000)) .radius(1400); mBaiduMap.addOverlay(ooCircle);
         *
         * LatLng llDot = new LatLng(39.98923, 116.397428); OverlayOptions ooDot
         * = new DotOptions().center(llDot).radius(6) .color(0xFF0000FF);
         * mBaiduMap.addOverlay(ooDot); 
         * // 添加多边形 LatLng pt1 = new
         * LatLng(39.93923, 116.357428); LatLng pt2 = new LatLng(39.91923,
         * 116.327428); LatLng pt3 = new LatLng(39.89923, 116.347428); LatLng
         * pt4 = new LatLng(39.89923, 116.367428); LatLng pt5 = new
         * LatLng(39.91923, 116.387428); List<LatLng> pts = new
         * ArrayList<LatLng>(); pts.add(pt1); pts.add(pt2); pts.add(pt3);
         * pts.add(pt4); pts.add(pt5); OverlayOptions ooPolygon = new
         * PolygonOptions().points(pts) .stroke(new Stroke(5,
         * 0xAA00FF00)).fillColor(0xAAFFFF00); mBaiduMap.addOverlay(ooPolygon);
         * // 添加文字 LatLng llText = new LatLng(39.86923, 116.397428);
         * OverlayOptions ooText = new TextOptions().bgColor(0xAAFFFF00)
         * .fontSize(24).fontColor(0xFFFF00FF).text("百度地图SDK").rotate(-30)
         * .position(llText); mBaiduMap.addOverlay(ooText);
         */
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        MapView.setMapCustomEnable(false);
        mMapView = null;
    }
}
