package es.uma.smartmeter.utils;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import es.uma.smartmeter.R;

public class NetworkManager {
    private static NetworkManager instance;
    private RequestQueue requestQueue;
    private final Context ctx;

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

    public void newLoginRequest(TextView output, Object tag) {
        String url = getURL("login/");

        // Solicita una respuesta json desde la URL proporcionada.
        JsonRequest request = new JsonRequest(Request.Method.POST, url, tag, null,
                response -> {
                    // Muestra la respuesta.
                    output.setText(ctx.getString(R.string.networkResponseSuccessText, response.toString()));
                },
                error -> Toast.makeText(ctx, ctx.getString(R.string.networkResponseErrorText, error.networkResponse != null ? error.networkResponse.statusCode : -1, error.getMessage()), Toast.LENGTH_LONG).show());
        // Añade la petición al RequestQueue.
        addToRequestQueue(request);
    }

    public void newMeasurementsRequest(TextView output, Object tag) {
        String url = getURL("medidas/");

        // Solicita una respuesta json desde la URL proporcionada.
        JsonRequest request = new JsonRequest(Request.Method.GET, url, tag, null,
                response -> {
                    // Muestra la respuesta.
                    output.setText(ctx.getString(R.string.networkResponseSuccessText, response.toString()));
                },
                error -> Toast.makeText(ctx, ctx.getString(R.string.networkResponseErrorText, error.networkResponse != null ? error.networkResponse.statusCode : -1, error.getMessage()), Toast.LENGTH_LONG).show());
        // Añade la petición al RequestQueue.
        addToRequestQueue(request);
    }

    public void newHomesRequest(TextView output, Object tag) {
        String url = getURL("hogars/");

        // Solicita una respuesta json desde la URL proporcionada.
        JsonRequest request = new JsonRequest(Request.Method.GET, url, tag, null,
                response -> {
                    // Muestra la respuesta.
                    output.setText(ctx.getString(R.string.networkResponseSuccessText, response.toString()));
                },
                error -> Toast.makeText(ctx, ctx.getString(R.string.networkResponseErrorText, error.networkResponse != null ? error.networkResponse.statusCode : -1, error.getMessage()), Toast.LENGTH_LONG).show());
        // Añade la petición al RequestQueue.
        addToRequestQueue(request);
    }

    public void newRegisterDeviceRequest(String name, JSONObject home, TextView output, Object tag) {
        String url = getURL("dispositivos/");

        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("nombre", name);
            jsonRequest.put("hogar", home);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // Solicita una respuesta json desde la URL proporcionada.
        JsonRequest request = new JsonRequest(Request.Method.POST, url, tag, jsonRequest,
                response -> {
                    // Muestra la respuesta.
                    try {
                        output.setText(ctx.getString(R.string.networkResponseSuccessText, response.getString("token")));
                    } catch (JSONException e) {
                        output.setText(ctx.getString(R.string.networkResponseSuccessText, response.toString()));
                    }
                },
                error -> Toast.makeText(ctx, ctx.getString(R.string.networkResponseErrorText, error.networkResponse != null ? error.networkResponse.statusCode : -1, error.getMessage()), Toast.LENGTH_LONG).show());
        // Añade la petición al RequestQueue.
        addToRequestQueue(request);
    }

    public String getURL(String suffix) {
        return ctx.getString(R.string.backend_url).concat(suffix);
    }

    public static class JsonRequest extends JsonObjectRequest {

        public JsonRequest(int method, String url, @Nullable Object tag, @Nullable JSONObject jsonRequest, Response.Listener<JSONObject> listener, @Nullable Response.ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener);
            if (tag != null) setTag(tag);
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = super.getHeaders();
            if (headers == null || headers.equals(Collections.emptyMap())) {
                headers = new HashMap<>();
            }
            headers.put("Content-Type", "application/json;charset=UTF-8");
            headers.put("Accept", "application/json");
            headers.put("Authorization", ""/* getToken() */);
            return headers;
        }
    }
}
