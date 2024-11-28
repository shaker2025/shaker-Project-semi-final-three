package edu.oakland.payroll;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    ImageButton payrollLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        payrollLogo = findViewById(R.id.payrollLogo);
        payrollLogo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EmployeeList.class);
            startActivity(intent);
        });

    }
}