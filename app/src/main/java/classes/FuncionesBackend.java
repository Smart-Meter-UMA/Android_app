package classes;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

public class FuncionesBackend {

    private static String urlPOST= "kproject/POST";
    private static String urlGET = "kproject/GET" ;
    private static String responseGetMedidas;
    private static String getResponseGetHogares;
    private static String tokenGoogle;
    private static String emailGoogle;
    private static String password;
    private static String tokenDispositivo = null;

    public static String getTokenDispositivo() {
        return tokenDispositivo;
    }

    public static void setTokenDispositivo(String tokenDispositivo) {
        FuncionesBackend.tokenDispositivo = tokenDispositivo;
    }

    public static void setTokenGoogle(String tokenGoogle) {
        FuncionesBackend.tokenGoogle = tokenGoogle;
    }

    public static String getTokenGoogle() {
        return tokenGoogle;
    }

    public static void setEmailGoogle(String emailGoogle) {
        FuncionesBackend.emailGoogle = emailGoogle;
    }

    public static String getEmailGoogle() {
        return emailGoogle;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String getWifi(Context context){


        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = mWifiManager.getConnectionInfo();

        return info.getSSID();
    }

    public static void setPassword(String password) {
        FuncionesBackend.password = password;
    }

    public static String getPassword() {
        return password;
    }

    public static String postInfo(String nombre, JSONObject hogar) throws IOException, JSONException {

            URL url = new URL("https://api-kproject.herokuapp.com/kproject/dispositivos/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");
            conn.setRequestProperty("Authorization", getTokenGoogle());
            conn.setDoOutput(true);
            conn.setDoInput(true);

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            // Aquí se escribiraán los datos que sean necesarios
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nombre", nombre);
            jsonObject.put("hogar", hogar);
            System.out.println("El json es " + jsonObject.toString());
            writer.write(jsonObject.toString());
            System.out.println("Ha escrito");

            writer.flush();
            writer.close();

        System.out.println("Response code:" +conn.getResponseCode());
        String json_response = "";
        InputStreamReader in = new InputStreamReader(conn.getInputStream());
        BufferedReader br = new BufferedReader(in);
        String text = "";
        while ((text = br.readLine()) != null) {
            json_response += text;
        }
        System.out.println("La response es: "+ json_response);
        br.close();

            JSONObject jsonObjectToken = new JSONObject(json_response);
            setTokenDispositivo(jsonObjectToken.getString("token"));

        System.out.println("El token nuevo es " + getTokenDispositivo());
            conn.disconnect();
            return conn.getResponseMessage();
}

        public static void getRequestMedidas(Context context){
            RequestQueue ExampleRequestQueue = Volley.newRequestQueue(context);
                String url = "https://api-kproject.herokuapp.com/kproject/medidas";

                StringRequest ExampleStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String r) {
                                //This code is executed if the server responds, whether or not the response contains data.
                                setResponseGetMedidas(r);
                        }
                }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                        @Override
                        public void onErrorResponse(VolleyError error) {
                                //This code is executed if there is an error.
                        }
                })
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Authorization", tokenGoogle);
                        return params;
                    }
                };
                ExampleRequestQueue.add(ExampleStringRequest);
        }


    public static void getRequestHogares(Context context){
        RequestQueue ExampleRequestQueue = Volley.newRequestQueue(context);
        String url = "https://api-kproject.herokuapp.com/kproject/hogars";

        StringRequest ExampleStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String r) {
                //This code is executed if the server responds, whether or not the response contains data.
                setGetResponseGetHogares(r);
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", tokenGoogle);
                return params;
            }
        };
        ExampleRequestQueue.add(ExampleStringRequest);
    }


    public static String getGetResponseGetHogares() {
        return getResponseGetHogares;
    }

    public static void setGetResponseGetHogares(String getResponseGetHogares) {
        FuncionesBackend.getResponseGetHogares = getResponseGetHogares;
    }

    public static String getResponseGetMedidas() {
        return responseGetMedidas;
    }

    public static void setResponseGetMedidas(String responseGetMedidas) {
        FuncionesBackend.responseGetMedidas = responseGetMedidas;
    }
}
