package es.uma.smartmeter.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import es.uma.smartmeter.R;

public class NetworkManager {
    private static NetworkManager instance;
    private final Context ctx;
    private RequestQueue requestQueue;

    private NetworkManager(Context context) {
        // getApplicationContext() es clave, evita que se filtre la
        // Activity o BroadcastReceiver si alguien pasa uno de ellos.
        ctx = context.getApplicationContext();
        requestQueue = getRequestQueue();
    }

    public static synchronized NetworkManager getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkManager(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx);
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void cancelAllRequests(Object tag) {
        getRequestQueue().cancelAll(tag);
    }

    public void newLoginRequest(Response.Listener<JSONObject> listener, Response.ErrorListener error, Object tag) {
        String url = getURL("login/");

        // Solicita una respuesta json desde la URL proporcionada.
        JSONObjectRequest request = new JSONObjectRequest(url, tag, null, listener, error);
        // Añade la petición al RequestQueue.
        addToRequestQueue(request);
    }

    public void newMeasurementsRequest(Response.Listener<JSONArray> listener, Object tag) {
        String url = getURL("medidas/");

        // Solicita una respuesta json desde la URL proporcionada.
        JSONArrayRequest request = new JSONArrayRequest(url, tag, listener, this::showError);
        // Añade la petición al RequestQueue.
        addToRequestQueue(request);
    }

    public void newHomesRequest(Response.Listener<JSONArray> listener, Object tag) {
        String url = getURL("hogars/");

        // Solicita una respuesta json desde la URL proporcionada.
        JSONArrayRequest request = new JSONArrayRequest(url, tag, listener, this::showError);
        // Añade la petición al RequestQueue.
        addToRequestQueue(request);
    }

    public void newRegisterDeviceRequest(String name, JSONObject home, Response.Listener<JSONObject> listener, Object tag) throws JSONException {
        String url = getURL("dispositivos/");

        JSONObject body = new JSONObject();
        body.put("nombre", name);
        body.put("hogar", home);

        // Solicita una respuesta json desde la URL proporcionada.
        JSONObjectRequest request = new JSONObjectRequest(url, tag, body, listener, this::showError);
        // Añade la petición al RequestQueue.
        addToRequestQueue(request);
    }

    private String getURL(String suffix) {
        return ctx.getString(R.string.backend_url).concat(suffix);
    }

    private String getToken() {
        return GoogleLoginManager.getInstance(ctx).getToken();
    }

    private void showError(VolleyError error) {
        Toast.makeText(ctx, ctx.getString(R.string.networkResponseErrorText, error.networkResponse != null ? error.networkResponse.statusCode : -1, error.getMessage()), Toast.LENGTH_LONG).show();
    }

    class JSONArrayRequest extends JsonArrayRequest {

        JSONArrayRequest(String url, @Nullable Object tag, Response.Listener<JSONArray> listener, @Nullable Response.ErrorListener errorListener) {
            super(Method.GET, url, null, listener, errorListener);

            if (tag != null) setTag(tag);
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = super.getHeaders();
            if (headers == null || headers.equals(Collections.emptyMap())) {
                headers = new HashMap<>();
            }
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", getToken());
            return headers;
        }
    }

    class JSONObjectRequest extends JsonObjectRequest {

        JSONObjectRequest(String url, @Nullable Object tag, @Nullable JSONObject jsonRequest, Response.Listener<JSONObject> listener, @Nullable Response.ErrorListener errorListener) {
            super(Method.POST, url, jsonRequest, listener, errorListener);

            if (tag != null) setTag(tag);
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = super.getHeaders();
            if (headers == null || headers.equals(Collections.emptyMap())) {
                headers = new HashMap<>();
            }
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", getToken());
            return headers;
        }
    }
}
