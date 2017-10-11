package com.barberfish.barberfish;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private final String URL = "https://speech.googleapis.com/v1/speech:recognize";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
            else if (type.startsWith("audio/")) {
                handleSendAudio(intent);
            }
            else {
                ((TextView) findViewById(R.id.tvStatus)).setText("Intent wird nicht unterstützt");
            }
        } else {
            ((TextView) findViewById(R.id.tvStatus)).setText("kein intent");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void handleSendAudio_(Intent intent) {
        ((TextView) findViewById(R.id.tvStatus)).setText("Audio??");

        Uri audioUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (audioUri == null)
            return;

        ((TextView) findViewById(R.id.tvStatus)).setText(audioUri.toString());
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(getApplicationContext(), audioUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();


    }
    private void sendHTTPRequest(Uri uri) {
        InputStream stream = null;
        try {
            stream = getContentResolver()
                    .openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("config", Util.createConfig());
            jsonRequest.put("audio", Util.createAudio(stream));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String apiKey = PreferenceManager.getDefaultSharedPreferences(this).getString("api_key","");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, URL + "?key=" + apiKey, jsonRequest,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        ((TextView) findViewById(R.id.tvStatus)).setText(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ((TextView) findViewById(R.id.tvStatus)).setText("error: " + error.toString());
                    }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }
    private void handleSendAudio(Intent intent) {
        final Uri audioUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        if (audioUri == null) {
            return;
        }

        ((TextView) findViewById(R.id.tvStatus)).setText(intent.toString());
        int res = 1;
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, res);

        sendHTTPRequest(audioUri);

    }

    private void handleSendImage(Intent intent) {
        ((TextView) findViewById(R.id.tvStatus)).setText("Image???");
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


}
