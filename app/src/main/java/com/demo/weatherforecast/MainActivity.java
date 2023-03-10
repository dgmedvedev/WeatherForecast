package com.demo.weatherforecast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    String json;
    String urlAddress;
    Button buttonClear;
    Button buttonDownload;
    TextView textViewWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlAddress = getString(R.string.url_address);
        buttonClear = findViewById(R.id.buttonClear);
        buttonDownload = findViewById(R.id.buttonDownload);
        textViewWeather = findViewById(R.id.textViewWeather);

        getContent();

        buttonClear.setOnClickListener(view -> {
            textViewWeather.setText("");
        });

        buttonDownload.setOnClickListener(view -> {
            textViewWeather.setText(json);
        });
    }

    private void getContent() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            URL url;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                url = new URL(urlAddress);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    result.append(line);
                    line = reader.readLine();
                }
                json = result.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            handler.post(() -> {
                textViewWeather.setText(json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String name = jsonObject.getString("name");

                    JSONObject main = jsonObject.getJSONObject("main");
                    String temp = main.getString("temp");

                    JSONArray jsonArray = jsonObject.getJSONArray("weather");
                    JSONObject weather = jsonArray.getJSONObject(0);
                    String id = weather.getString("id");
                    Log.i("CITY_ID", name + ", T = " + temp + ", id = " + id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        });
    }
}