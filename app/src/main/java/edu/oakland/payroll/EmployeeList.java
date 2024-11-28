package edu.oakland.payroll;
//package com.example.payrollapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;

public class EmployeeList extends AppCompatActivity {

    private ListView employeeList;
    private Button addEmployeeButton;
    private ArrayList<String> employees = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    public static SQLiteDatabase db;
    EIHelper lvDbHelper;
    String AllQuery = "";
    private static final String TAG = "EmployeeList";

    Button homebtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);
        //button
        addEmployeeButton = findViewById(R.id.addEmployeeButton);

        //create ListView
        employeeList = findViewById(R.id.employeeList);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, employees);
        employeeList.setAdapter(adapter);


        //create Database
        createDB();
        AllQuery = "SELECT * FROM employeeInfo";
        String[] hourlyRate = getResult(AllQuery);

        //Goes to Add Employee Activity
        addEmployeeButton.setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeList.this, AddEmployeeActivity.class);
            startActivity(intent);
        });

        // Initialize the homebtn and set its click listener
        homebtn = findViewById(R.id.backhome1);
        homebtn.setOnClickListener(view -> {
            // Navigate back to LanguageSelectionActivity
            Intent intent = new Intent(EmployeeList.this, LanguageSelectionActivity.class);
            startActivity(intent);
            finish(); // Optional: close the current activity
        });

        //Click on the ListView to go to Salary Calculator
        employeeList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(EmployeeList.this, SalaryCalculator.class);
            intent.putExtra("employeeName", employees.get(position));
            intent.putExtra("hourlyRate", hourlyRate[position]);
            int empID = position + 1;
            intent.putExtra("employeeId", String.valueOf(empID));
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
   }
    public void createDB() {
        lvDbHelper = new EIHelper(this);
        try {
            lvDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            lvDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }
        db = lvDbHelper.getWritableDatabase();
    }

    //creates listview with database & returns a hourly rate array
    public String[] getResult(String q) {
        Cursor result = db.rawQuery(q, null);
        result.moveToFirst();
        int count = result.getCount();
        Log.i("count=", String.valueOf(count));
        String[] hourlyRate = new String[count];
        employees = new ArrayList<String>();
        int empIdNumber = 1;
        if (count >= 1) {
            do {
                Log.d("id=", result.getString(0));
                //Listview structure is: 0. Name
                employees.add(result.getString(0) + ". " + result.getString(1));
                //String array
                hourlyRate[empIdNumber-1] = result.getString(2);
                empIdNumber++;
            }while (result.moveToNext());
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, employees);
        employeeList.setAdapter(adapter);
        return hourlyRate;
    }
}
