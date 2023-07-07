package es.uma.smartmeter.utils;

import android.content.Context;

import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.util.Strings;

import es.uma.smartmeter.R;

public class GoogleLoginManager {
    private static GoogleLoginManager instance;
    private final Context ctx;
    private GoogleSignInClient client;
    private GoogleSignInAccount account;

    private GoogleLoginManager(Context context) {
        // getApplicationContext() es clave, evita que se filtre la
        // Activity o BroadcastReceiver si alguien pasa uno de ellos.
        ctx = context.getApplicationContext();
        getClient();
        getAccount();
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

    public GoogleSignInClient getClient() {
        if (client == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(ctx.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            client = GoogleSignIn.getClient(ctx, gso);
        }

        return client;
    }

    public String getEmail() {
        return getAccount() != null ? getAccount().getEmail() : "N/A";
    }

    public String getToken() {
        return getAccount() != null ? getAccount().getIdToken() : "N/A";
    }
}
