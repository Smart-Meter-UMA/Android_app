package es.uma.smartmeter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;

import es.uma.smartmeter.utils.GoogleLoginManager;
import es.uma.smartmeter.utils.NetworkManager;

public class MainLoginActivity extends AppCompatActivity {

    public static final String TAG = "SmartMeter-Login";
    private boolean hasFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        if (GoogleLoginManager.getInstance(this).getAccount() != null) {
            View content = findViewById(android.R.id.content);
            content.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            if (hasFinished) {
                                content.getViewTreeObserver().removeOnPreDrawListener(this);
                                return true;
                            } else {
                                return false;
                            }
                        }
                    });
            tryBackendLogin();
            return;
        }

        ActivityResultLauncher<Intent> loginActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                tryBackendLogin();
            }
        });

        findViewById(R.id.sign_in_button).setOnClickListener(view -> {
            Intent i = GoogleLoginManager.getInstance(this).getClient().getSignInIntent();
            loginActivityResultLauncher.launch(i);
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        NetworkManager.getInstance(this).cancelAllRequests(TAG);
    }

    private void tryBackendLogin() {
        NetworkManager.getInstance(this).newLoginRequest(response -> {
            hasFinished = true;
            System.out.println("La response es " + response.toString());
            if (response.length() > 1) {
                startActivity(new Intent(getApplicationContext(), NavigationBar.class));
                finish();
            }
        }, error -> {
            hasFinished = true;
            GoogleLoginManager.getInstance(this).getClient().signOut();
            startActivity(new Intent(getApplicationContext(), MainLoginActivity.class));
            finish();
        }, TAG);
    }
}