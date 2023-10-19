package es.uma.smartmeter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import es.uma.smartmeter.utils.GoogleLoginManager;
import es.uma.smartmeter.utils.NetworkManager;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "SmartMeter-Login";
    // La forma de obtener el token es con account.getToken(), ya es cuestión de mandarlo donde sea.
    private SignInButton signInButton;
    private Button signoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Crea un ActivityResultLauncher que lanza una actividad determinada, se usa posteriormente para el sing in
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // There are no request codes
                System.out.println("Llega al result");
                Intent data = result.getData();

                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        this.signInButton = findViewById(R.id.sign_in_button);
        this.signoutButton = findViewById(R.id.bSignOut);

        GoogleSignInAccount account = GoogleLoginManager.getInstance(this).getAccount();
        if (account != null) {
            NetworkManager.getInstance(this).newLoginRequest(response -> {
                System.out.println("La response es " + response.toString());
                if (response.length() > 1) {
                    System.out.println("La acccount es " + account.getEmail());
                    System.out.println("EL token es " + account.getIdToken());
                    //Una vez tienes eso puedes hacer la post junto al nombre y al hogar.
                    updateUI(account);
                }
            }, error -> {
                updateUI(null);
            }, TAG);
        } else {
            System.out.println("Account nula");
            updateUI(null);
        }

        this.signInButton.setOnClickListener(view -> {
            Intent i = GoogleLoginManager.getInstance(this).getClient().getSignInIntent();
            someActivityResultLauncher.launch(i);
            updateUI(account);
        });

        this.signoutButton.setOnClickListener(view -> {
            GoogleLoginManager.getInstance(this).getClient().signOut();
            System.out.println("Sign out");
            finish();
            startActivity(getIntent());
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        NetworkManager.getInstance(this).cancelAllRequests(TAG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);

        // The Task returned from this call is always completed, no need to attach
        // a listener.

        System.out.println("Aquí llega result");
        //Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        finish();
        startActivity(getIntent());
        Toast.makeText(this, "Funciona el sign in", Toast.LENGTH_LONG).show();
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account == null) {
            // Oculta lo que haga falta
            System.out.println("Entra");
            this.signoutButton.setVisibility(View.GONE);
            this.signInButton.setVisibility(View.VISIBLE);

        } else {
            System.out.println("Entra else");
            Toast.makeText(this, account.getEmail(), Toast.LENGTH_LONG).show();
            System.out.println("Email");
            System.out.println(account.getEmail());
            System.out.println("Token");
            System.out.println(account.getIdToken());

            this.signInButton.setVisibility(View.GONE);
            this.signoutButton.setVisibility(View.VISIBLE);

            Intent intent = new Intent(getApplicationContext(), NavigationBar.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Error en el sign in", Toast.LENGTH_SHORT).show();
    }
}