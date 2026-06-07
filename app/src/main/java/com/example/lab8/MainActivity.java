package com.example.lab8;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.Random;

/**
 * Lab 8: Advanced Threading & Async Patterns
 * A creative implementation demonstrating non-blocking UI operations.
 */
public class MainActivity extends AppCompatActivity {

    private TextView txtStatus;
    private LinearProgressIndicator progressBar;
    private ImageView img;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeInterface();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    private void initializeInterface() {
        txtStatus = findViewById(R.id.txtStatus);
        progressBar = findViewById(R.id.progressBar);
        img = findViewById(R.id.img);

        MaterialButton btnLoadThread = findViewById(R.id.btnLoadThread);
        MaterialButton btnCalcAsync = findViewById(R.id.btnCalcAsync);
        MaterialButton btnToast = findViewById(R.id.btnToast);

        // 1. Instant UI Feedback (Responsiveness Test)
        btnToast.setOnClickListener(v -> {
            showPulseEffect(v);
            Toast.makeText(this, "⚡ Interface Engine: 100% Responsive", Toast.LENGTH_SHORT).show();
        });

        // 2. Thread + Handler Implementation
        btnLoadThread.setOnClickListener(v -> executeImageStream());

        // 3. AsyncTask Implementation
        btnCalcAsync.setOnClickListener(v -> new DataAnalyticsEngine().execute());
    }

    private void updateStatus(String msg, String hexColor) {
        txtStatus.setText(msg);
        txtStatus.setTextColor(Color.parseColor(hexColor));
    }

    private void showPulseEffect(View view) {
        view.animate().scaleX(1.05f).scaleY(1.05f).setDuration(100)
                .withEndAction(() -> view.animate().scaleX(1f).scaleY(1f).setDuration(100));
    }

    /**
     * WORKER THREAD DEMO
     * Loads a resource in the background and updates UI via Handler.
     */
    private void executeImageStream() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        updateStatus("Streaming asset data...", "#0984E3");

        new Thread(() -> {
            try {
                // Simulating heavy I/O or Network latency
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Background processing: Decoding resource
            final Bitmap decodedAsset = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);

            // Re-routing execution to Main Looper
            mainHandler.post(() -> {
                img.setAlpha(0f);
                img.setImageBitmap(decodedAsset);
                img.animate().alpha(1f).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator());
                
                progressBar.setVisibility(View.INVISIBLE);
                updateStatus("Asset Synced Successfully", "#00B894");
            });
        }).start();
    }

    /**
     * ASYNCTASK DEMO
     * Simulates complex data analytics with progress updates.
     */
    private class DataAnalyticsEngine extends AsyncTask<Void, Integer, Long> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(false);
            progressBar.setProgress(0);
            updateStatus("Initializing Analytics...", "#E17055");
        }

        @Override
        protected Long doInBackground(Void... voids) {
            long entropy = 0;
            Random rand = new Random();

            for (int i = 1; i <= 100; i++) {
                try {
                    // Simulating computational load
                    Thread.sleep(60); 
                    entropy += rand.nextInt(500);
                    publishProgress(i);
                } catch (InterruptedException e) {
                    break;
                }
            }
            return entropy;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progress = values[0];
            progressBar.setProgress(progress);
            if (progress % 15 == 0) {
                updateStatus("Processing Neural Nodes: " + progress + "%", "#6C5CE7");
            }
        }

        @Override
        protected void onPostExecute(Long result) {
            progressBar.setVisibility(View.INVISIBLE);
            updateStatus("Engine result: " + result + " ops/sec", "#2D3436");
            Toast.makeText(MainActivity.this, "Analytics Cycle Complete", Toast.LENGTH_SHORT).show();
        }
    }
}
