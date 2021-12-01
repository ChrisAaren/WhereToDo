package edu.utep.cs.cs4330.wheretodo.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.utep.cs.cs4330.wheretodo.R;
import edu.utep.cs.cs4330.wheretodo.model.ToDoItem;

public class ViewTaskDetails extends AppCompatActivity implements OnMapReadyCallback {

    private FragmentManager fragmentManager;

    GoogleMap map;

    private TextView taskName;
    private TextView taskNotes;
    private TextView taskLocation;
    private TextView taskPriority;
    private Button goBack;

    ToDoItem task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task_details);

        Intent intent = getIntent();
        task = (ToDoItem) intent.getSerializableExtra("VIEW ITEM");
        String priority = intent.getStringExtra("VIEW PRIORITY");

        taskName = findViewById(R.id.taskNameView);
        taskNotes = findViewById(R.id.notesView);
        taskLocation = findViewById(R.id.locationView);
        taskPriority = findViewById(R.id.priority);

        taskName.setText(Html.fromHtml("Task Name: " + "<b>" + task.description() + "</b>"));
        taskNotes.setText(Html.fromHtml("Task Notes: " + "<b>" + task.notes() + "</b>"));
        taskLocation.setText(Html.fromHtml("Task Location: " + "<b>" + task.location() + "</b>"));
        taskPriority.setText(Html.fromHtml("<b>" + priority + "</b>"));

        goBack = findViewById(R.id.goBackButton);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.taskMap);
        supportMapFragment.getMapAsync(this);

        fragmentManager = getSupportFragmentManager();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        double[] coordinates = MainActivity.convertAddress(task.location(), this);
        LatLng destination = new LatLng(coordinates[0],coordinates[1]);
        map.addMarker(new MarkerOptions().position(destination).title(task.description()));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(destination,15));
    }
}