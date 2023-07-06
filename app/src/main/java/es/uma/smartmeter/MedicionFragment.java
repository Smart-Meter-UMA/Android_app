package es.uma.smartmeter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import es.uma.smartmeter.databinding.ActivityMeasureBinding;
import es.uma.smartmeter.utils.FuncionesBackend;

public class MedicionFragment extends Fragment {
    private ActivityMeasureBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ActivityMeasureBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println("La response es " + FuncionesBackend.getResponseGetMedidas());
        try {
            JSONArray json = new JSONArray(FuncionesBackend.getResponseGetMedidas());
            //this.power.setText((String)(json.get(json.length()-1));
            JSONObject jsonObject = ((JSONObject) json.get(json.length() - 1));
            System.out.println(jsonObject);

            binding.tPower.setText(jsonObject.get("kw") + "KW");
            System.out.println(binding.tPower.getText());

            SimpleDateFormat dfISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            SimpleDateFormat dfTxt = new SimpleDateFormat("dd/MM/yyyy");
            System.out.println("Lo que llega es " + jsonObject.get("fecha"));
            Date date = dfISO.parse(jsonObject.get("fecha").toString());
            System.out.println("Date: " + date);

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            binding.tFecha.setText(dfTxt.format(date));

        } catch (JSONException | ParseException | NullPointerException e) {
            e.printStackTrace();
        }

        binding.tCorreo.setText(binding.tCorreo.getText().toString().replace("CORREO", FuncionesBackend.getEmailGoogle()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}