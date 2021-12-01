package edu.utep.cs.cs4330.wheretodo.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;

import edu.utep.cs.cs4330.wheretodo.R;

public class EditItemActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner editDropDownList;
    private EditText todoEdit;
    private EditText notesEdit;
    private AutoCompleteTextView todoEditLocation;
    private Button editButton;

    private Intent intent;
    private int priority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        intent = getIntent();
        String itemDescription = intent.getStringExtra("DESCRIPTION");
        String itemLocation = intent.getStringExtra("LOCATION");
        String itemNotes = intent.getStringExtra("NOTES");
        priority = intent.getExtras().getInt("PRIORITY");
        String priorityAsText = convertPriority(priority);

        editButton = findViewById(R.id.edit_Button);
        todoEdit = findViewById(R.id.editItemEdit);
        notesEdit = findViewById(R.id.editNotes);
        editDropDownList = findViewById(R.id.editDropDownList);


        ArrayList<String> items = new ArrayList<String>(Arrays.asList("None", "Low", "Medium", "High"));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        editDropDownList.setAdapter(adapter);
        editDropDownList.setSelection(items.indexOf(priorityAsText));
        editDropDownList.setOnItemSelectedListener(this);

        editButton.setOnClickListener(this::editOnClick);
        todoEdit.setText(itemDescription);

        todoEditLocation = findViewById(R.id.editLocationEdit);
        todoEditLocation.setAdapter(new AutoSuggestAdapter(this, android.R.layout.simple_list_item_1));
        todoEditLocation.setText(itemLocation);

        notesEdit.setText(itemNotes);
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

    public void editOnClick(View view) {
        String description = todoEdit.getText().toString();
        String notes = notesEdit.getText().toString();
        String location = todoEditLocation.getText().toString();
        int postition = intent.getExtras().getInt("ITEM INDEX");
        intent.putExtra("NEW DESCRIPTION", description);
        intent.putExtra("ITEM POSITION", postition);
        intent.putExtra("ITEM LOCATION", location);
        intent.putExtra("ITEM NOTES", notes);
        intent.putExtra("ITEM PRIORITY", priority);
        setResult(RESULT_OK, intent);
        finish();
    }
}