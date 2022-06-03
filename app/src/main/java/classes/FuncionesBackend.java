package classes;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private static String responseGet;
    private static String tokenGoogle;
    private static String emailGoogle;

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

    public static String[] getWifi(Context context, String password){
        String [] array = new String[2];

        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = mWifiManager.getConnectionInfo();

        array[0]= info.getSSID();
        array[1] = password;

        return array;
    }



    public static String postInfo(String nombre, String hogar) throws IOException, JSONException {

            URL url = new URL(urlPOST);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("nombre", nombre);
            jsonParam.put("hogar", hogar);

            Log.i("JSON", jsonParam.toString());

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            os.writeBytes(jsonParam.toString());

            os.flush();
            os.close();

            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
            Log.i("MSG" , conn.getResponseMessage());

            conn.disconnect();
            return conn.getResponseMessage();
}

        public static void getRequest(Context context, String response){
            RequestQueue ExampleRequestQueue = Volley.newRequestQueue(context);
                String url = "https://www.google.com/search?q=Android+GET+request";
                StringRequest ExampleStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String r) {
                                //This code is executed if the server responds, whether or not the response contains data.
                                setResponseGet(r);
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




        public static String getResponseGet() {
                return responseGet;
        }

        public static void setResponseGet(String responseGet) {
                FuncionesBackend.responseGet = responseGet;
        }
}
