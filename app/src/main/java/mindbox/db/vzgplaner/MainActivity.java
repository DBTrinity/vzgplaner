package mindbox.db.vzgplaner;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.Point;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {
    MapView map = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

        //inflate and create the map
        setContentView(R.layout.activity_main);

        map = findViewById(R.id.map);

        // https://wiki.openstreetmap.org/wiki/Tile_servers

        final ITileSource tileSource = new XYTileSource(
                "HOT",
                1,
                20,
                256,
                ".png",
                new String[]{
                        // OSM B&W mapnik map grayscale
                        "https://tiles.wmflabs.org/bw-mapnik/",
                        "https://a.tiles.wmflabs.org/bw-mapnik/",
                        "https://b.tiles.wmflabs.org/bw-mapnik/",
                        "https://c.tiles.wmflabs.org/bw-mapnik/"
                        // "http://a.tile.openstreetmap.fr/",
                        // "http://b.tile.openstreetmap.fr/",
                        // "http://c.tile.openstreetmap.fr/"
                }, "© OpenStreetMap contributors");
        map.setTileSource(tileSource);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);


        KmlDocument kmlDocument = new KmlDocument();


        InputStream is = getResources().openRawResource(R.raw.railwaystationnodes);

        FeatureCollection featureCollection = null;
        try {
            featureCollection =
                    new ObjectMapper().readValue(is, FeatureCollection.class);
        } catch (IOException e) {
            e.printStackTrace();
        }


//        String jsonString = new BufferedReader(new InputStreamReader(is)).lines()
//                .parallel().collect(Collectors.joining("\n"));
//        kmlDocument.parseGeoJSON(jsonString);


//        Set<String> strings = kmlDocument.mKmlRoot.mItems.get(0).mExtendedData.keySet();

//        featureCollection.getFeatures().stream().filter(i -> i.getProperties().get(""))

        for (Feature f : featureCollection.getFeatures()) {
            Point point = (Point) f.getGeometry();
            Marker marker = new Marker(map);
            marker.setPosition(new GeoPoint(point.getCoordinates().getLatitude(), point.getCoordinates().getLongitude(), point.getCoordinates().getAltitude()));
            marker.setTextIcon((String) f.getProperty("code"));
//            marker.setTextIcon(String.valueOf(f.getProperty("geographicalName")));
            marker.setTextLabelFontSize(30);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker item, MapView arg1) {
                    item.showInfoWindow();
                    Log.i("bla", "onMarkerClick: ");
                    return true;
                }
            });
            map.getOverlays().add(marker);
            map.invalidate();

        }


//
//        Drawable defaultMarker = getResources().getDrawable(R.drawable.marker_default);
//        Bitmap defaultBitmap = ((BitmapDrawable) defaultMarker).getBitmap();
//        Style defaultStyle = new Style(defaultBitmap, 0x901010AA, 5f, 0x20AA1010);
//        FolderOverlay geoJsonOverlay = (FolderOverlay) kmlDocument.mKmlRoot.buildOverlay(map, defaultStyle, null, kmlDocument);
//
//        map.getOverlays().add(geoJsonOverlay);

        IMapController mapController = map.getController();
        mapController.setZoom(9.5);
        GeoPoint startPoint = new GeoPoint(52.520008, 13.404954);
        mapController.setCenter(startPoint);

    }

    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }
}