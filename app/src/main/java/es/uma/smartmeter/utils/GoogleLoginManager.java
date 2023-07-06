package es.uma.smartmeter.utils;

import android.content.Context;

import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class GoogleLoginManager {
    private static GoogleLoginManager instance;
    private final Context ctx;
    private GoogleSignInAccount account;

    private GoogleLoginManager(Context context) {
        // getApplicationContext() es clave, evita que se filtre la
        // Activity o BroadcastReceiver si alguien pasa uno de ellos.
        ctx = context.getApplicationContext();
        account = getAccount();
    }

    public static synchronized GoogleLoginManager getInstance(Context context) {
        if (instance == null) {
            instance = new GoogleLoginManager(context);
        }
        return instance;
    }

    @Nullable
    public GoogleSignInAccount getAccount() {
        if (account == null) {
            account = GoogleSignIn.getLastSignedInAccount(ctx);
        }
        return account;
    }

    public void loginUser() {

    }

    public void logoutUser() {

    }

    public String getToken() {
        return getAccount() != null ? getAccount().getIdToken() : "N/A";
    }
}
