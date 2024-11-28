package edu.oakland.payroll;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EmployeeDetailsActivity extends AppCompatActivity {

    private TextView employeeNameDetail, employeeHourlyRate, payrollOutput;
    private EditText hoursWorkedInput;
    private Button calculatePayrollButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_details);

        employeeNameDetail = findViewById(R.id.employeeNameDetail);
        employeeHourlyRate = findViewById(R.id.employeeHourlyRate);
        hoursWorkedInput = findViewById(R.id.hoursWorkedInput);
        payrollOutput = findViewById(R.id.payrollOutput);
        calculatePayrollButton = findViewById(R.id.calculatePayrollButton);

        // Assuming hourlyRate and employeeName are passed as intent extras
        String employeeName = getIntent().getStringExtra("employeeName");
        double hourlyRate = getIntent().getDoubleExtra("hourlyRate", 0);

        employeeNameDetail.setText(employeeName);
        employeeHourlyRate.setText("Hourly Rate: $" + hourlyRate);

        calculatePayrollButton.setOnClickListener(v -> {
            String hoursInput = hoursWorkedInput.getText().toString();
            if (!hoursInput.isEmpty()) {
                double hoursWorked = Double.parseDouble(hoursInput);
                double grossPay = calculatePay(hourlyRate, hoursWorked);
                payrollOutput.setText("Gross Pay: $" + grossPay);
            }
        });
    }

    private double calculatePay(double hourlyRate, double hoursWorked) {
        double regularHours = Math.min(hoursWorked, 40);
        double overtimeHours = Math.max(0, hoursWorked - 40);
        return (regularHours * hourlyRate) + (overtimeHours * hourlyRate * 1.5);
    }
}

