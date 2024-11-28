package edu.oakland.payroll;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;

public class SalaryCalculator extends AppCompatActivity {

    private TextView employeeNameDetail, employeeHourlyRate, payrollOutput;
    private EditText hoursWorkedInput;
    private Button calculatePayrollButton, EmployeeListButton;
    public static SQLiteDatabase db;
    EIHelper salaryDbHelper;
    String salAllQuery = "";
    private ListView salaryList;
    private ArrayList<String> salary = new ArrayList<>();
    private ArrayAdapter<String> adapter2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salary_calculator);
        //Text Views
        employeeNameDetail = findViewById(R.id.employeeNameDetail);
        employeeHourlyRate = findViewById(R.id.employeeHourlyRate);
        payrollOutput = findViewById(R.id.payrollOutput);

        //Edit Text
        hoursWorkedInput = findViewById(R.id.hoursWorkedInput);

        //Buttons
        calculatePayrollButton = findViewById(R.id.calculatePayrollButton);
        EmployeeListButton = findViewById(R.id.EmployeeListButton);

        // Intents from Add Employee Activity
        String employeeName = getIntent().getStringExtra("employeeName");
        String hourlyRate = getIntent().getStringExtra("hourlyRate");
        String employeeId = getIntent().getStringExtra("employeeId");

        //Setting text from Intents
        employeeNameDetail.setText(employeeName);
        employeeHourlyRate.setText("Hourly Rate: $" + hourlyRate);

        //setting ListView up
        salaryList = findViewById(R.id.salaryList);


        //setting up SalaryInfo database
        createDB();
        salAllQuery = "SELECT salaryInfor.payrollID, employeeInfo.Role, salaryInfor.hoursWorked, salaryInfor.workedOvertime \n" +
                "FROM salaryInfor " +
                "LEFT JOIN employeeInfo " +
                "ON salaryInfor.employeeID = employeeInfo.employeeID " +
                "WHERE salaryInfor.employeeID = "+ employeeId;
        getResult(salAllQuery);

        //Sets Listviews in Salary Calculator & Employee List & updates both Employee & Salary Info tables
        calculatePayrollButton.setOnClickListener(v -> {
            String hoursInput = hoursWorkedInput.getText().toString();
            String id = employeeId;

            //runs the calculation
            if (!hoursInput.isEmpty()) {
                Double hoursWorked = Double.parseDouble(hoursInput);
                Double grossPay = calculatePay(Double.parseDouble(hourlyRate), hoursWorked);
                payrollOutput.setText("Gross Pay: $" + grossPay);

                //performs the update on the database
                UpdateData(id, hoursInput);

                //resets the listview & EditText
                adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, salary);
                salaryList.setAdapter(adapter2);
                hoursWorkedInput.setText("");
            } else {
                toastMessage("Fill in hours!");
            }
        });

        //Goes back to Employee List
        EmployeeListButton.setOnClickListener(v -> {
            Intent intent = new Intent(SalaryCalculator.this, EmployeeList.class);
            startActivity(intent);
        });
    }

    //calculates the pay
    private Double calculatePay(Double hourlyRate, Double hoursWorked) {
        Double regularHours = Math.min(hoursWorked, 40);
        Double overtimeHours = Math.max(0, hoursWorked - 40);
        return (Double) ((regularHours * hourlyRate) + (overtimeHours * hourlyRate * 1.5));
    }

    public void createDB() {
        salaryDbHelper = new EIHelper(this);
        try {
            salaryDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            salaryDbHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }
        db = salaryDbHelper.getWritableDatabase();
    }

    //updates the database
    public void UpdateData(String id, String hoursInput) {
        boolean updateData = salaryDbHelper.updateEmployee(id, hoursInput);
        if (updateData){
            toastMessage("Data successfully inserted!");
        }else {
            toastMessage("Something went wrong");
        }
    }

    //creates listview for Salary Calculator
    public void getResult(String q) {
        Cursor result = db.rawQuery(q, null);
        result.moveToFirst();
        int count = result.getCount();
        Log.i("count=", String.valueOf(count));
        salary = new ArrayList<String>();
        int empIdNumber = 1;
        if (count >= 1) {
            do {
                Log.d("payrollId=", result.getString(0));
                //Structure is : Payroll ID: 0
                //               Role: RoleName
                //               Expected Hours Worked: 0
                //               Worked Overtime: Yes/No
                salary.add("Payroll ID: " + result.getString(0) + "\nRole: " + result.getString(1) + "\nExpected Hours Worked: "
                        + result.getString(2) + "\nWorked Overtime: " + result.getString(3));
                empIdNumber++;
            }while (result.moveToNext());
        }
    }

    //Used to alert for database insert/update concerns
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}

