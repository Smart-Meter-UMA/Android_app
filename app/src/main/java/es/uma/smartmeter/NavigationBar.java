package es.uma.smartmeter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.os.StrictMode;
import android.Manifest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.tabs.TabLayout;

import es.uma.smartmeter.utils.FuncionesBackend;


public class NavigationBar extends AppCompatActivity {

    private TabLayout tbNavigationBar;
    private ViewPager2 viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_bar);
        tbNavigationBar = findViewById(R.id.tbNavigationBar);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new AdaptadorFragmentos(getSupportFragmentManager(),getLifecycle()));
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tbNavigationBar.selectTab(tbNavigationBar.getTabAt(position));
            }
        });
        tbNavigationBar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tbNavigationBar.getSelectedTabPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Cosas del main para poder lanzar las actividades
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.mPermissionResult.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
        this.mPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION);

        //ESto para qué es?
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            System.out.println("Saca las medidas");
            FuncionesBackend.getRequestMedidas(getApplicationContext());
            System.out.println("Saca los hogares");
            FuncionesBackend.getRequestHogares(getApplicationContext());
            System.out.println("Todo asignado");
        }
    }

    class AdaptadorFragmentos extends FragmentStateAdapter{

        public AdaptadorFragmentos(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if(position == 0){
                return new MedicionFragment();
            } else if (position == 1){
                return new GraficasFragment();
            } else {
                return new ConectarFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    //Métodos que estan en el main (para compronar si se puede acceder a las actividades

    private final ActivityResultLauncher<String> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result) {
                    System.out.println("onActivityResult: PERMISSION GRANTED");
                } else {
                    System.out.println("onActivityResult: PERMISSION DENIED");
                }
            });
}