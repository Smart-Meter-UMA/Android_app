package es.uma.smartmeter;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import es.uma.smartmeter.databinding.FragmentConectarBinding;
import es.uma.smartmeter.utils.FuncionesBackend;


public class ConectarFragment extends Fragment {
    private FragmentConectarBinding binding;

    private String password;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentConectarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.MyAlertDialogStyle);
        builder.setTitle("Contraseña");
        builder.setMessage("Introduzca la contraseña del WiFi");
        //Crea un EditText en el nuevo cuadro de diálogo que recibe un texto normal pero oculta los caracteres ingresados
        final EditText input = new EditText(this.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        //No comprueba si la contraseña es correcta y lanza una nueva actividad DevideScannActivity
        builder.setPositiveButton("OK", (dialog, which) -> {
            password = input.getText().toString();
            FuncionesBackend.setPassword(password);
            //He cambiado getApplicationContext() por this.getContext()
            Intent i = new Intent(this.getContext(), DeviceScanActivity.class);
            startActivity(i);
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }
}