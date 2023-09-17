package es.uma.smartmeter;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import es.uma.smartmeter.databinding.FragmentConectarBinding;
import es.uma.smartmeter.utils.FuncionesBackend;

public class ConectarFragment extends Fragment {
    private FragmentConectarBinding binding;

    private String password;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentConectarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.button.setOnClickListener(v -> conect());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void conect(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_pasword, null);
        builder.setTitle("Contraseña");
        builder.setMessage("Introduzca la contraseña del WiFi");
        EditText etContraseña = dialogView.findViewById(R.id.etContraseña);
        //No comprueba si la contraseña es correcta y lanza una nueva actividad DevideScannActivity
        builder.setPositiveButton("OK", (dialog, which) -> {
            password = etContraseña.getText().toString();
            FuncionesBackend.setPassword(password);
            //He cambiado getApplicationContext() por this.getContext()
            Intent i = new Intent(getContext(), DeviceScanActivity.class);
            startActivity(i);
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.setView(dialogView);
        builder.show();
    }
}