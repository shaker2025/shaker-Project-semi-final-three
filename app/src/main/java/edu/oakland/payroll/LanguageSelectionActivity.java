package edu.oakland.payroll;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class LanguageSelectionActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_LANGUAGE = "Language";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

        // Find buttons by their IDs
        Button englishButton = findViewById(R.id.buttonEnglish);
        Button arabicButton = findViewById(R.id.buttonArabic);
        Button frenchButton = findViewById(R.id.buttonFrench);

        // Assign correct actions to each button
        englishButton.setOnClickListener(v -> setLanguageAndProceed("en"));
        arabicButton.setOnClickListener(v -> setLanguageAndProceed("ar"));
        frenchButton.setOnClickListener(v -> setLanguageAndProceed("fr"));
    }

    private void setLanguageAndProceed(String languageCode) {
        // Save selected language to shared preferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply();

        // Update app language
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        android.content.res.Configuration config = getResources().getConfiguration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Proceed to the next activity
        Intent intent = new Intent(LanguageSelectionActivity.this, EmployeeList.class);
        startActivity(intent);
        finish();
    }
}
