// LoginActivity.java
package com.example.changelanguage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private Spinner spinner;
    private EditText username, password;
    private Button loginButton;

    private String chooseLangCode = "en"; // Default language code
    private String selectedLanguage = "English"; // Default display language

    public final static String[] languages = {"Select language", "English", "Hindi"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load saved language preference
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String savedLangCode = prefs.getString("My_Lang", "en");

        chooseLangCode = savedLangCode; // Set default
        selectedLanguage = savedLangCode.equals("hi") ? "Hindi" : "English";

        LanguageManager.setLocale(this, savedLangCode);

        setContentView(R.layout.activity_login);

        spinner = findViewById(R.id.language_spinner);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Set the correct spinner position
        spinner.setSelection(savedLangCode.equals("hi") ? 2 : 1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) return;

                chooseLangCode = position == 1 ? "en" : "hi";
                selectedLanguage = position == 1 ? "English" : "Hindi";

                if (!chooseLangCode.equals(savedLangCode)) {
                    saveLanguage(chooseLangCode);
                    restartActivity();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        loginButton.setOnClickListener(v -> {
            String user = username.getText().toString();
            String pass = password.getText().toString();

            if (user.equals("admin") && pass.equals("1234")) {
                Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                // Store the language selection
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("selectedLanguage", selectedLanguage);
                editor.apply();

                LanguageManager.setLocale(LoginActivity.this, chooseLangCode);

                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveLanguage(String langCode) {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("My_Lang", langCode);
        editor.apply();
    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
