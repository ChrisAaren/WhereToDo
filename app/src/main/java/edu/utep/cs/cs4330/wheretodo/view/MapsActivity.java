package edu.utep.cs.cs4330.wheretodo.view;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import edu.utep.cs.cs4330.wheretodo.R;
import edu.utep.cs.cs4330.wheretodo.model.ToDoDatabaseHelper;
import edu.utep.cs.cs4330.wheretodo.model.ToDoItem;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    ToDoDatabaseHelper databaseHelper;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        databaseHelper = new ToDoDatabaseHelper(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng marker = null;
        double[] coordinates;

        List<ToDoItem> tasks = databaseHelper.allItems();
        for (ToDoItem items : tasks) {
            coordinates = MainActivity.convertAddress(items.location(), this);
            marker = new LatLng(coordinates[0], coordinates[1]);
            mMap.addMarker(new MarkerOptions().position(marker).title("Marker for Task: " + items.description()));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
    }
}