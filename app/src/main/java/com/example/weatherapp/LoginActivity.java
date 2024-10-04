package com.example.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Random;


public class LoginActivity extends AppCompatActivity {

    private EditText emailText, passwordText;
    private TextView errorText;
    private Button loginButton;
    private Spinner langSpinner;
    private SharedPreferences prefs;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailText = findViewById(R.id.email);
        passwordText = findViewById(R.id.password);
        errorText = findViewById(R.id.error);
        loginButton = findViewById(R.id.login_button);
        langSpinner = findViewById(R.id.lang);
        progress = findViewById(R.id.progress);

       // progress.setVisibility(View.GONE);

        //MODE_PRIVATE prefelenciak csak az alkalmazas szamara erhetok el
        prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String savedLanguage = prefs.getString("Language", "en");
        setLocale(savedLanguage);

       ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
       //android.R.layout.simple_spinner_dropdown_item alapertelmezett elrendezes
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //alapertelmezett spinner hint nem mukodik
        langSpinner.setPrompt("Language");
        langSpinner.setAdapter(adapter);

        langSpinner.setAdapter(adapter);

        langSpinner.setSelection(0, false);



        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {

                } else {
                    String selectedLang;
                    if (position == 1) {
                        selectedLang = "en";

                    } else {
                        selectedLang = "hu";

                    }

                    if (!selectedLang.equals(savedLanguage)) {
                        setLocale(selectedLang);
                        recreate();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

        loginButton.setOnClickListener(view -> {

            errorText.setVisibility(View.GONE);
            loginButton.setEnabled(false);
            progress.setVisibility(View.VISIBLE);

            //trim() eltavolitja a felesleges szokozoket
            String email = emailText.getText().toString().trim();
            String password = passwordText.getText().toString().trim();

            Log.d("LoginActivity", "Email: " + email + "'");
            Log.d("LoginActivity", "Password: " + password + "'");


            if (email.isEmpty() || password.isEmpty()) {
                showError("Please fill in all fields");
                loginButton.setEnabled(true);
                progress.setVisibility(View.GONE);
            } else {
                api(email, password);

            }

//            api(email, password);
        });


    }

    private void api(String email, String password) {

        String url = "http://10.0.2.2/feladatok/api.php";
      //  String url = "http://newdigital.hu/bzoltan/api.php";

        JSONObject loginData = new JSONObject();

        try {

            loginData.put("email", email);
            loginData.put("password", password);

        } catch (JSONException e) {

            throw new RuntimeException(e);
        }
        //specialis Volley keres, json ba kuld es kap valaszt
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                loginData,
                response -> {
                    Log.d("LoginActivity", "API Response: " + response.toString());

            try {
                //valasz json-bol kiolvassa hogy sikeres volt a keres
                boolean success = response.getBoolean("success");
                if (success) {
                    //intent MainActivityre iranyit
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);


                } else {

                    showError(getString(R.string.error));
                }

            } catch (JSONException e) {
                Log.e("LoginActivity", "JSON Parsing error: " + e.getMessage());
               // e.printStackTrace();
            }

            loginButton.setEnabled(true);
        },
            error -> {
                if (error instanceof TimeoutError) {

                    showError(getString(R.string.timeout_error));

                } else {
                    Log.e("LoginActivity", "API Request error: " + error.getMessage());
                    showError(getString(R.string.error));
                }

                loginButton.setEnabled(true);

        });

        request.setRetryPolicy(new DefaultRetryPolicy(

                4000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(this).add(request);
    }

    private void showError(String message) {

        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        //Configuration android rendszer beallitasai
        Configuration config = new Configuration();
        config.setLocale(locale);
        //fissiti a rendszer eroforrasait
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = prefs.edit();
        //kesobbi inditaskor megmarad a nyelv
        editor.putString("Language", lang);
        editor.apply();
        //fissiti a felhasznaloi feluletet
        updateUI();
    }

    private void updateUI() {
        emailText.setHint(getString(R.string.email));
        passwordText.setHint(getString(R.string.password));
        loginButton.setText(getString(R.string.login));
        errorText.setText("");
    }

}
