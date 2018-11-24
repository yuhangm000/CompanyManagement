package com.company.management;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class FormCreate extends AppCompatActivity {
    private ArrayList<Spinner> spinner_material_name = new ArrayList<>();
    private ArrayList<Spinner> spinner_material_size = new ArrayList<>();
    private ArrayList<String> material_number = new ArrayList<>();
    private TextView creator;
    private TextView status;
    private ListView material_list;
    private Button cancel, complete;
    private Spinner spinner;
    private FormCreateListContentAdapter formCreateListContentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_create);
        init();
        spinner_material_name.add(spinner);
        spinner_material_size.add(spinner);
        material_number.add("0");
        spinner_material_name.add(spinner);
        spinner_material_size.add(spinner);
        material_number.add("0");
        formCreateListContentAdapter.setMaterial_item(spinner_material_name, spinner_material_size, material_number);
        material_list.setAdapter(formCreateListContentAdapter);
    }
    void init() {
        creator = (TextView) findViewById(R.id.form_create_creator);
        status = (TextView) findViewById(R.id.form_create_status);
        material_list = (ListView) findViewById(R.id.form_create_material_list);
        cancel = (Button) findViewById(R.id.form_create_cancel);
        complete = (Button) findViewById(R.id.form_create_complete);
        formCreateListContentAdapter = new FormCreateListContentAdapter();

    }
}
