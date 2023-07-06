package es.uma.smartmeter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import es.uma.smartmeter.databinding.ActivityMeasuresGraphBinding;
import es.uma.smartmeter.utils.FuncionesBackend;

public class GraficasFragment extends Fragment {
    private ActivityMeasuresGraphBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ActivityMeasuresGraphBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.bDatos.setOnClickListener(v -> {
            Intent i = new Intent(this.getContext(), MedicionesDatosActivity.class);
            i.putExtra("mes", "TODOS");
            startActivity(i);
        });
        binding.graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    Format formatter = new SimpleDateFormat("dd/MM");
                    return formatter.format(new Date((long) value));
                }
                return super.formatLabel(value, isValueX);
            }
        });
        binding.graphView.getGridLabelRenderer().setTextSize(28);

        try {
            addData();
        } catch (JSONException | ParseException | NullPointerException e) {
            e.printStackTrace();
        }


        binding.graphView.setTitle(" \n Lista de mediciones históricas para " + FuncionesBackend.getEmailGoogle());
        binding.graphView.setTitleTextSize(28);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void addData() throws JSONException, ParseException {

        JSONArray json = new JSONArray(FuncionesBackend.getResponseGetMedidas());
        System.out.println("Medidas: " + FuncionesBackend.getResponseGetMedidas());
        DataPoint[] array = new DataPoint[5];
        int j = 0;
        for (int i = json.length() - 1; i > json.length() - 6; i--) {
            JSONObject jsonObject = ((JSONObject) json.get(i));
            double d = jsonObject.getDouble("kw");

            SimpleDateFormat dfISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            SimpleDateFormat dfTxt = new SimpleDateFormat("dd/MM/yyyy");
            Date date = dfISO.parse(jsonObject.get("fecha").toString());
            System.out.println(date.getTime());

            Date dateFinal = new Date(date.getTime());
            System.out.println(dateFinal);
            System.out.println("Los valores son " + dateFinal + " : " + d);
            DataPoint dt = new DataPoint(dateFinal, d);
            array[j] = dt;
            j++;
        }

        showArray(array);
        List<DataPoint> list = Arrays.asList(array);
        System.out.println(list);
        Collections.reverse(list);
        System.out.println(list);
        array = list.toArray(array);
        System.out.println("Length" + array.length);
        showArray(array);
        LineGraphSeries<DataPoint> points = new LineGraphSeries<>(array);
        binding.graphView.addSeries(points);

    }

    private void showArray(DataPoint[] array) {
        System.out.println("Length " + array.length);
        for (int i = 0; i < 5; i++) {
            System.out.print("DataPointX: " + array[i].getX() + ";");
            System.out.println("DataPointY: " + array[i].getY());
        }
    }

}