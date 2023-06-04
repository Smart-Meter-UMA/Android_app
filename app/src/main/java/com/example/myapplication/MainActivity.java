package com.example.myapplication;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
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

/* La actividad MainActivity es en la que aparece el menú principal, y se compone de varios
 botones para saltar a otras actividades. */
public class MainActivity extends AppCompatActivity {

    private Button bLogin;
    private Button bConnect;
    private Button bMeasure;
    private Button bGraficas;
    private Button bTest;
    private String password;

    /*
    ActivityResultLauncher <String> (espera un String como resultado) proporciona un mecanismo para definir como se maneja el resultado de una actividad.
    Para registrar el objeto ActivityResultLauncher se utiliza el método registerForActivityResult(ActivityResultContract, ActivityResultCallback<T>):
     - ActivityResultContract define cómo se lanza la actividad y como se procesa el resultado,
            .RequestPermission() es un método que permite solicitar un permiso en concreto pasado como parámetro
            en el método onCreate(), se pide el permiso de localización (precisa y aproximada) con ACCESS_COARSE_LOCATION y ACCESS_FINE_LOCATION
     - ActivityResultCallback define las acciones que se llevan a cabo si el permiso es aceptado o denegado,
            en este caso en concreto se imprime en pantalla el resultado.
    */
    private ActivityResultLauncher<String> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        System.out.println("onActivityResult: PERMISSION GRANTED");
                    } else {
                        System.out.println("onActivityResult: PERMISSION DENIED");
                    }
                }
            });

    /*
    El método onCreate() inicializa la actividad cuando esta es creada.
    Establece el diseño de la interfaz con setContentView(R.layout.activity_main)
    Establece una política sin restricciones (acceso a la red, accedso a bases de datos, acceso a recursos externos ...) con StrincMode.ThreadPolicy.Builder().permitAll()
    Establece una configuracion de inicio de sesión en la que pide el acceso básico a la informacion del perfil y correo, además del ID de cliente de la aplicación
    Comprueba la última cuenta registrada en la app y si esta no existe (NO TENGO MUY CLARO QUÉ HACE)
    Asigna a cada botón su vista en la interfaz e invisibiliza e inhabilita el botón "bTest".
    Además, establece un clicklistener a cada botón, que lanza nuevas actividades dependiendo del botón pulsado.
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.mPermissionResult.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
        this.mPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION);

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

        /*
        El botón bTest imprime el token de google y la wifi en la consola. Parece que es para probar si funciona la comunicacion Bluetooth y eso
         */
        this.bTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("El token es " + FuncionesBackend.getTokenGoogle());
                System.out.println("El wifi es "+ FuncionesBackend.getWifi(getApplicationContext()));

                FuncionesBackend.getRequestMedidas(MainActivity.this);
                System.out.println("La response es "+FuncionesBackend.getResponseGetMedidas());
            }
        });


        /*
        El botón bConnect comprueba si se ha iniciado sesión y si los parámetros GetResponseHogares y ...ResponseMedidas de FuncionesBackend no son nulos.
        Si es así, lanza un cuadro de diálogo para pedir la contraseña wifi, y dos botones, uno para aceptar y otro para cancelar.GetResponseHogares y ...ResponseMedidas de FuncionesBackend no son nulos
        Si la contraseña es correcta, lanza la actividad DeviceScannActivity
        */
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
                    //Crea un EditText en el nuevo cuadro de diálogo que recibe un texto normal pero oculta los caracteres ingresados
                    final EditText input = new EditText(getApplicationContext());
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    builder.setView(input);

                    //Si la contraseña es correcta se lanza una nueva actividad DevideScannActivity
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

        /*
        El botón bGráficas que comprueba primero si se ha iniciado sesión y si los parámetros GetResponseHogares y ...ResponseMedidas de FuncionesBackend no son nulos.
        Si es así, lanza la actividad MeasureGraphActivity.
         */

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

        /*
        El botón bMeasure comprueba como los anteriores si se ha iniciado sesion y si los Strings de FuncionesBackend no son nulos,
        y lanza la actividad MeasureActivity
         */
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
        /*
        El botón bLogin lanza la actividad LoginActivity (no comprueba nada pues es para hacer el login)
         */
        this.bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);

            }
        });

    }
}