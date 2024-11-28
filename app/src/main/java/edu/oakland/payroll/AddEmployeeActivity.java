package edu.oakland.payroll;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class AddEmployeeActivity extends AppCompatActivity {

    private EditText employeeName, hourlyRate;
    private TextView employeeId;
    private Button salaryButton;
    public static SQLiteDatabase db;
    EIHelper EIHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        //EditText
        employeeName = findViewById(R.id.employeeName);
        hourlyRate = findViewById(R.id.hourlyRate);

        //TextView
        employeeId = findViewById(R.id.employeeId);

        //Button
        salaryButton = findViewById(R.id.salaryButton);

        //Create Databases
        createDB();


        // Initialize the homebtn and set its click listener
        Button homebtn;
        homebtn = findViewById(R.id.backhome2);
        homebtn.setOnClickListener(view -> {
            // Navigate back to LanguageSelectionActivity
            Intent intent = new Intent(AddEmployeeActivity.this, LanguageSelectionActivity.class);
            startActivity(intent);
            finish(); // Optional: close the current activity
        });

        //Button to go to Salary Calculator
        salaryButton.setOnClickListener(v -> {

            //Getting values from EditTexts & TextView
            String name = employeeName.getText().toString();
            String id = employeeId.getText().toString();
            String rate = hourlyRate.getText().toString();

            //Checking for empty fields
            if (employeeName.length() != 0 && employeeId.length() != 0 && hourlyRate.length() != 0) {

                //Inserting data into database
                AddData(id, name, rate);

                //resets the EditTexts & TextView
                employeeName.setText("");
                employeeId.setText("");
                hourlyRate.setText("");
            } else {
                toastMessage("You must complete all fields");
            }

            //Sends ID, Name, & Hourly Rate to Salary Calculator
            Intent intent = new Intent(AddEmployeeActivity.this, SalaryCalculator.class);
            intent.putExtra("employeeName", name);
            intent.putExtra("hourlyRate", rate);
            intent.putExtra("employeeId", id);
            Log.i("name=", String.valueOf(name));

            startActivity(intent);
        });
    }
        public void createDB() {
            EIHelper = new EIHelper(this);
            try {
                EIHelper.createDataBase();
            } catch (IOException ioe) {
                throw new Error("Unable to create database");
            }
            try {
                EIHelper.openDataBase();
            } catch (SQLException sqle) {
                //    throw sqle;
            }
            db = EIHelper.getWritableDatabase();
        }

        //Adds data to both tables: Employee Info & Salary Info
        public void AddData(String id, String name, String rate) {
            boolean insertData = EIHelper.addEmployee(id, name, rate);
            boolean insertData2 = EIHelper.addSalary(id);
            if (insertData && insertData2){
                toastMessage("Data successfully inserted!");
            }else {
                toastMessage("Something went wrong");
            }
        }

        //Used to alert for database insert/update concerns
        private void toastMessage(String message) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
}
