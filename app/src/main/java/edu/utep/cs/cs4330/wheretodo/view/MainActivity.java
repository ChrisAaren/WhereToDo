package edu.utep.cs.cs4330.wheretodo.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import edu.utep.cs.cs4330.wheretodo.R;
import edu.utep.cs.cs4330.wheretodo.model.ToDoDatabaseHelper;
import edu.utep.cs.cs4330.wheretodo.model.ToDoItem;

public class MainActivity extends AppCompatActivity {

    private List<ToDoItem> toDoItemArrayList;
    private ToDoDatabaseHelper toDoDatabaseHelper;
    private ToDoAdapter toDoAdapter;

    private ListView listView;

    private String fullAddress;
    final int addRequestCode = 1;
    final int editRequestCode = 2;
    final int locationPermissionCode = 42;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Geocoder geocoder;
    private List<Address> addressList;

    double longitude;
    double latitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fullAddress = getCoordinates();
        } else {
            requestLocationPermission();
        }

        fullAddress = getCoordinates();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this::onItemClick);

        toDoDatabaseHelper = new ToDoDatabaseHelper(this);
        toDoItemArrayList = toDoDatabaseHelper.allItems();

        Collections.sort(toDoItemArrayList, this::compare);

        if (toDoItemArrayList.size() == 0) {
            AlertDialog.Builder firstTime = new AlertDialog.Builder(this)
                    .setTitle("Welcome to WhereTodo!")
                    .setMessage("Click on the toolbar in upper right-hand corner to add a task.")
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            firstTime.show();
        }

        toDoAdapter = new ToDoAdapter(this, R.layout.todo_item, toDoItemArrayList);
        toDoAdapter.setItemClickListener(item -> toDoDatabaseHelper.update(item));

        listView.setAdapter(toDoAdapter);
        registerForContextMenu(listView);
    }

    public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
        viewDetails(position);
    }

    public int compare(ToDoItem item1, ToDoItem item2) {
        if (item1.priority() < item2.priority())
            return 1;
        if (item1.priority() > item2.priority())
            return -1;
        return 0;
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("This permission is needed to assign locations to a task.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, locationPermissionCode);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, locationPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == locationPermissionCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.searchView);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {
                if (TextUtils.isEmpty(query)) {
                    toDoAdapter.filter("");
                    listView.clearTextFilter();
                } else {
                    toDoAdapter.filter(query);
                }
                toDoAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                toDoAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Task Options");
        menu.add(0, 0, 0, "Edit Task");
        menu.add(0, 1, 3, "Clear Task");
        menu.add(0, 2, 2, "Get Directions to Task in Google Maps");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        switch (item.getItemId()) {
            case 0:
                editItem(index);
                return true;
            case 1:
                clearItem(index);
                return true;
            case 2:
                ToDoItem visitTask = toDoItemArrayList.get(index);
                double[] coordinates = convertAddress(visitTask.location(), this);
                openMaps(coordinates[0], coordinates[1]);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_item:
                addItem();
                return true;
            case R.id.mapList:
                Intent mapIntent = new Intent(this, MapsActivity.class);
                startActivity(mapIntent);
                return true;
            case R.id.clearChecked:
                clearChecked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String convertPriority(int priority) {
        switch (priority) {
            case 0:
                return "None";
            case 1:
                return "Low";
            case 2:
                return "Medium";
            case 3:
                return "High";
            default:
                return "None";
        }
    }

    public void viewDetails(int index) {
        ToDoItem task = toDoItemArrayList.get(index);
        String priority = convertPriority(task.priority());
        Intent intent = new Intent(this, ViewTaskDetails.class);
        intent.putExtra("VIEW ITEM", task);
        intent.putExtra("VIEW PRIORITY", priority);
        startActivity(intent);
    }

    public void addItem() {
        fullAddress = getCoordinates();
        Intent intent = new Intent(this, AddItemActivity.class);
        intent.putExtra("Location", fullAddress);
        startActivityForResult(intent, addRequestCode);
    }

    public void editItem(int position) {
        Intent intent = new Intent(this, EditItemActivity.class);
        ToDoItem item = toDoItemArrayList.get(position);
        intent.putExtra("DESCRIPTION", item.description());
        intent.putExtra("ITEM INDEX", position);
        intent.putExtra("LOCATION", item.location());
        intent.putExtra("NOTES", item.notes());
        intent.putExtra("PRIORITY", item.priority());
        startActivityForResult(intent, editRequestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                ToDoItem item = (ToDoItem) data.getSerializableExtra("ITEM");
                toDoItemArrayList.add(item);
                toDoDatabaseHelper.addItem(item);
                Collections.sort(toDoItemArrayList, this::compare);
                toDoAdapter.notifyDataSetChanged();
            }
        } else {
            if (resultCode == RESULT_OK) {
                int index = data.getExtras().getInt("ITEM POSITION");
                String description = data.getStringExtra("NEW DESCRIPTION");
                String location = data.getStringExtra("ITEM LOCATION");
                String notes = data.getStringExtra("ITEM NOTES");
                int priority = data.getExtras().getInt("ITEM PRIORITY");
                ToDoItem item = toDoItemArrayList.get(index);
                item.setDescription(description);
                item.setLocation(location);
                item.setNotes(notes);
                item.setPriority(priority);
                toDoDatabaseHelper.update(item);
                Collections.sort(toDoItemArrayList, this::compare);
                toDoAdapter.notifyDataSetChanged();
            }
        }
    }

    public void clearChecked() {
        for (ToDoItem item : new ArrayList<ToDoItem>(toDoItemArrayList)) {
            ToDoItem checked = item;
            if (checked.isDone()) {
                toDoAdapter.remove(item);
                toDoItemArrayList.remove(item);
                toDoDatabaseHelper.delete(item.id());
            }
        }
        toDoAdapter.notifyDataSetChanged();
    }

    public void clearItem(int position) {
        ToDoItem item = toDoItemArrayList.get(position);
        toDoAdapter.remove(item);
        toDoDatabaseHelper.delete(item.id());
        toDoItemArrayList.remove(item);
        toDoAdapter.notifyDataSetChanged();
    }

    public String getCoordinates() {
        geocoder = new Geocoder(this, Locale.getDefault());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    try {
                        addressList = geocoder.getFromLocation(latitude, longitude, 1);
                        String address = addressList.get(0).getAddressLine(0);
                        String area = addressList.get(0).getLocality();
                        String city = addressList.get(0).getAdminArea();

                        fullAddress = address + ", " + area + ", " + city;
                        Collections.sort(toDoItemArrayList, MainActivity.this::compare);
                        toDoAdapter.notifyDataSetChanged();
                    } catch (IOException | IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return fullAddress;
    }

    public static double[] convertAddress(String address, Context context) {
        Geocoder geoCoder = new Geocoder(context);
        if (address != null && !address.isEmpty()) {
            try {
                List<Address> addressList = geoCoder.getFromLocationName(address, 1);
                if (addressList != null && addressList.size() > 0) {
                    double lat = addressList.get(0).getLatitude();
                    double lng = addressList.get(0).getLongitude();
                    return new double[]{lat, lng};
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void openMaps(double lat, double lng) {
        Uri navigationIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lng);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}