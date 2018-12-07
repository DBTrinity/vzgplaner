package mindbox.db.vzgplaner;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleOpacity;


public class MainActivity extends Activity {
    MapView mapView = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getApplicationContext();

        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);

        InputStream inputStream = context.getResources().openRawResource(R.raw.test);
        final String result = new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));


        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);


        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                // One way to add a marker view
                FeatureCollection featureCollection = FeatureCollection.fromJson(result);
                Source source = new GeoJsonSource("my.data.source", featureCollection);
                mapboxMap.addSource(source);

                CircleLayer circleLayer = new CircleLayer("trees-style", "trees-source");
                circleLayer.withProperties(
                        circleOpacity(1.0f),
                        circleColor(Color.parseColor("#ff0000"))
                );

                // replace street-trees-DC-9gvg5l with the name of your source layer
                circleLayer.setSourceLayer("street-trees-DC-9gvg5l");

                mapboxMap.addLayer(circleLayer);


            }
        });


    }

    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
}