package edu.utep.cs.cs4330.wheretodo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import edu.utep.cs.cs4330.wheretodo.R;
import edu.utep.cs.cs4330.wheretodo.model.ToDoItem;

public class AddItemActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ToDoItem item;
    private Intent intent;
    private int priority;

    private Spinner dropDown;
    private EditText todoAdd;
    private EditText todoAddNotes;
    private AutoCompleteTextView todoLocation;
    private Button addButtton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        addButtton = findViewById(R.id.add_Button);
        todoLocation = findViewById(R.id.addLocationEdit);
        todoAddNotes = findViewById(R.id.notesEdit);
        dropDown = findViewById(R.id.dropDownList);

        String[] items = new String[]{"None", "Low", "Medium", "High"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        dropDown.setAdapter(adapter);
        dropDown.setOnItemSelectedListener(this);

        addButtton.setOnClickListener(this::addOnClick);
        todoLocation.setAdapter(new AutoSuggestAdapter(this, android.R.layout.simple_list_item_1));

        intent = getIntent();

        String location = intent.getStringExtra("Location");

        todoLocation.setText(location);

        todoAdd = findViewById(R.id.addItemEdit);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected;
        switch (position) {
            case 0:
                selected = (String) parent.getItemAtPosition(position);
                if (selected.equals("None"))
                    priority = 0;
                break;
            case 1:
                selected = (String) parent.getItemAtPosition(position);
                if (selected.equals("Low"))
                    priority = 1;
                break;
            case 2:
                selected = (String) parent.getItemAtPosition(position);
                if (selected.equals("Medium"))
                    priority = 2;
                break;
            case 3:
                selected = (String) parent.getItemAtPosition(position);
                if (selected.equals("High"))
                    priority = 3;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        priority = 0;
    }

    public void addOnClick(View view) {
        String description = todoAdd.getText().toString();
        String notes = todoAddNotes.getText().toString();
        String location = todoLocation.getText().toString();
        item = new ToDoItem(description, location, notes, priority);
        Intent intent = getIntent();
        intent.putExtra("ITEM", item);
        setResult(RESULT_OK, intent);
        finish();
    }
}