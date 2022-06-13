package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import org.json.JSONException;

import java.io.IOException;

import classes.FuncionesBackend;

public class MainActivity extends AppCompatActivity {

    private Button bLogin;
    private Button bConnect;
    private Button bMeasure;
    private Button bGraficas;
    private Button bTest;
    private String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("724046535439-h28ieq17aff119i367el50skelqkdgh4.apps.googleusercontent.com")
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null) {
            System.out.println("Saca las medidas");
            FuncionesBackend.getRequestMedidas(getApplicationContext());
            System.out.println("Saca los hogares");
            FuncionesBackend.getRequestHogares(getApplicationContext());
            System.out.println("Todo asignado");
        }


        this.bLogin=findViewById(R.id.bLoginSimulate);
        this.bConnect=findViewById(R.id.bConnect);
        this.bGraficas=findViewById(R.id.bMedidasGrafica);
        this.bMeasure=findViewById(R.id.bMedida);
        this.bTest = findViewById(R.id.bTest);
        this.bTest.setVisibility(View.INVISIBLE);
        this.bTest.setEnabled(false);

        this.bTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("El token es " + FuncionesBackend.getTokenGoogle());
                System.out.println("El wifi es "+ FuncionesBackend.getWifi(getApplicationContext()));

                FuncionesBackend.getRequestMedidas(MainActivity.this);
                System.out.println("La response es "+FuncionesBackend.getResponseGetMedidas());
            }
        });


        this.bConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(account == null) Toast.makeText(getApplicationContext(),"Por favor inicia sesión", Toast.LENGTH_SHORT).show();
                else if(FuncionesBackend.getResponseGetMedidas() == null  ) {
                    Toast.makeText(getApplicationContext(),"Extrayendo mediciones, espere unos segundos", Toast.LENGTH_SHORT).show();
                    FuncionesBackend.getRequestHogares(getApplicationContext());
                }
                else if(FuncionesBackend.getGetResponseGetHogares() == null){
                    FuncionesBackend.getRequestHogares(getApplicationContext()) ;
                    Toast.makeText(getApplicationContext(),"Extrayendo hogares, espere unos segundos", Toast.LENGTH_SHORT).show();
                }
                else {


                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
                    builder.setTitle("Contraseña");
                    builder.setMessage("Introduzca la contraseña del WiFi");
                    final EditText input = new EditText(getApplicationContext());
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    builder.setView(input);
// Set up the input

// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    builder.setView(input);

// Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            password = input.getText().toString();
                            FuncionesBackend.setPassword(password);

                            Intent i = new Intent(getApplicationContext(), DeviceScanActivity.class);
                            startActivity(i);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();

                }

            }
        });


        this.bGraficas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(account == null) Toast.makeText(getApplicationContext(),"Por favor inicia sesión", Toast.LENGTH_SHORT).show();
                else if(FuncionesBackend.getResponseGetMedidas() == null  ) {
                    Toast.makeText(getApplicationContext(),"Extrayendo mediciones, espere unos segundos", Toast.LENGTH_SHORT).show();
                    FuncionesBackend.getRequestMedidas(getApplicationContext());
                }
                else if(FuncionesBackend.getGetResponseGetHogares() == null){
                    FuncionesBackend.getRequestHogares(getApplicationContext()) ;
                    Toast.makeText(getApplicationContext(),"Extrayendo hogares, espere unos segundos", Toast.LENGTH_SHORT).show();
                }else {
                    Intent i = new Intent(getApplicationContext(), MeasuresGraphActivity.class);
                    startActivity(i);
                }

            }
        });

        this.bMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(account == null) Toast.makeText(getApplicationContext(),"Por favor,inicie sesión", Toast.LENGTH_SHORT).show();
                else if(FuncionesBackend.getResponseGetMedidas() == null  ) {
                    Toast.makeText(getApplicationContext(),"Extrayendo mediciones, espere unos segundos", Toast.LENGTH_SHORT).show();
                    FuncionesBackend.getRequestHogares(getApplicationContext());
                    System.out.println("Medidas"+ FuncionesBackend.getResponseGetMedidas());
                }
                else if(FuncionesBackend.getGetResponseGetHogares() == null){
                    FuncionesBackend.getRequestHogares(getApplicationContext()) ;
                    System.out.println("Hogares: "+FuncionesBackend.getGetResponseGetHogares());
                    Toast.makeText(getApplicationContext(),"Extrayendo hogares, espere unos segundos", Toast.LENGTH_SHORT).show();
                }else {

                    Intent i = new Intent(getApplicationContext(),MeasureActivity.class);
                    startActivity(i);
                }
            }
        });
        this.bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);

            }
        });

    }
}