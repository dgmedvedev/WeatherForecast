package com.demo.weatherforecast;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
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
    // Для работы приложения, необходимо добавить API key в urlJson

    String json;
    String urlJson;
    String urlImage;
    Bitmap image;
    Button buttonClear;
    Button buttonDownload;
    ImageView imageView;
    TextView textViewWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlJson = getString(R.string.url_json);
        urlImage = getString(R.string.url_image);
        buttonClear = findViewById(R.id.buttonClear);
        buttonDownload = findViewById(R.id.buttonDownload);
        imageView = findViewById(R.id.imageView);
        textViewWeather = findViewById(R.id.textViewWeather);

        getContent();

        buttonClear.setOnClickListener(view -> {
            imageView.setImageBitmap(null);
            textViewWeather.setText("");
        });

        buttonDownload.setOnClickListener(view -> {
            imageView.setImageBitmap(image);
            textViewWeather.setText(json);
        });
    }

    private void getContent() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            downloadImage();
            downloadJson();

            handler.post(() -> {
                imageView.setImageBitmap(image);
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

    private void downloadImage() {
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(urlImage);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            image = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private void downloadJson() {
        URL url;
        HttpURLConnection urlConnection = null;
        StringBuilder result = new StringBuilder();
        try {
            url = new URL(urlJson);
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
    }
}