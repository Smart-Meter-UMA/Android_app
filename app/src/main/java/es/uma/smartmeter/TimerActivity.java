package es.uma.smartmeter;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class TimerActivity extends AppCompatActivity {

    private final Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent it = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(it);
                finish();
            }
        }, 1500);
    }
}