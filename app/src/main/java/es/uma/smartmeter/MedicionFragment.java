package es.uma.smartmeter;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import es.uma.smartmeter.databinding.ActivityMeasureBinding;
import es.uma.smartmeter.utils.GoogleLoginManager;
import es.uma.smartmeter.utils.NetworkManager;

public class MedicionFragment extends Fragment {
    public static final String TAG = "SmartMeter-Medicion";
    private ActivityMeasureBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ActivityMeasureBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NetworkManager.getInstance(getContext()).newMeasurementsRequest(response -> {
            System.out.println("La response es " + response.toString());
            try {
                JSONObject jsonObject = ((JSONObject) response.get(response.length() - 1));
                System.out.println(jsonObject);

                binding.include.tvPower.setText(jsonObject.get("kw") + "KW");
                int progress = 0;
                int max_kw = 3000; //KW máximos alcanzables estimados. Usado para calcular el porcentage de progressbar coloreado
                progress += Integer.parseInt(jsonObject.get("kw").toString());
                progress = progress/max_kw * 100;
                binding.include.progressBar.setProgress(progress);
                System.out.println(binding.include.tvPower.getText());

                SimpleDateFormat dfISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                SimpleDateFormat dfTxt = new SimpleDateFormat("dd/MM/yyyy");
                System.out.println("Lo que llega es " + jsonObject.get("fecha"));
                Date date = dfISO.parse(jsonObject.get("fecha").toString());
                System.out.println("Date: " + date);

                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                binding.tFecha.setText(dfTxt.format(date));
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }, TAG);

        binding.tCorreo.setText(binding.tCorreo.getText().toString().replace("CORREO",GoogleLoginManager.getInstance(getContext()).getEmail()));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        NetworkManager.getInstance(getContext()).cancelAllRequests(TAG);
        binding = null;
    }
}