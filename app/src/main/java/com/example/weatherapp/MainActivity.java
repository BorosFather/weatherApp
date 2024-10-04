package com.example.weatherapp;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText cityName;
    private Button searchButton, exitButton;
    private TextView weatherResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.cityName);
        searchButton = findViewById(R.id.searchButton);
        weatherResult = findViewById(R.id.weatherResult);
        exitButton = findViewById(R.id.exitButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityName.getText().toString().trim();
                if (!city.isEmpty()) {
                    getWeatherData(city);
                }
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation fadeOut = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out);
                findViewById(android.R.id.content).startAnimation(fadeOut);

                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finishAfterTransition();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

        });

//        exitButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                View rootView = findViewById(android.R.id.content);
//
//                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), Color.WHITE,
//                        Color.BLACK);
//                colorAnimation.setDuration(2000);
//
//                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
//                        rootView.setBackgroundColor((int) valueAnimator.getAnimatedValue());
//                    }
//                });
//
//                colorAnimation.start();
//                colorAnimation.addListener(new android.animation.AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(android.animation.Animator animation) {
//
//                        finishAfterTransition();
//                    }
//
//                });
//            }
//        });
    }

    private void getWeatherData(String city) {
        String apiKey = "3f5dcea20ea99bdd27c364b7dc0903a1";
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey + "&units=metric";


        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject main = jsonObject.getJSONObject("main");
                            String temperature = main.getString("temp");
                            weatherResult.setText("Current Temperature: " + temperature + "°C");
                        } catch (Exception e) {
                            e.printStackTrace();
                            weatherResult.setText("Error parsing weather data");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        weatherResult.setText("Error fetching data: " + error.getMessage());
                    }
                });

        queue.add(stringRequest);
    }
}
