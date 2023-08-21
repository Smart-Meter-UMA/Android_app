package es.uma.smartmeter;

import android.Manifest;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.TypedValue;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

import es.uma.smartmeter.utils.GoogleLoginManager;

public class NavigationBar extends AppCompatActivity {

    private final ActivityResultLauncher<String> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result) {
                    System.out.println("onActivityResult: PERMISSION GRANTED");
                } else {
                    System.out.println("onActivityResult: PERMISSION DENIED");
                }
            });
    private BottomNavigationView tbNavigationBar;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_bar);

        tbNavigationBar = findViewById(R.id.tbNavigationBar);

        TypedValue typedValue = new TypedValue();
        int[] colorAttr = new int[] { R.attr.colorSurface };
        int indexOfAttrColor = 0;
        TypedArray a = tbNavigationBar.getContext().obtainStyledAttributes(typedValue.data, colorAttr);
        int color = a.getColor(indexOfAttrColor, -1);
        a.recycle();

        getWindow().setNavigationBarColor(color);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new AdaptadorFragmentos(getSupportFragmentManager(), getLifecycle()));
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tbNavigationBar.setSelectedItemId(tbNavigationBar.getMenu().getItem(position).getItemId());
            }
        });
        tbNavigationBar.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.item_measurement:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.item_graphs:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.item_connect:
                    viewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        });

        //Cosas del main para poder lanzar las actividades
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        GoogleSignInAccount account = GoogleLoginManager.getInstance(getApplicationContext()).getAccount();
        if (account != null) {
            System.out.println("Todo asignado");
        }
    }

    //Métodos que estan en el main (para compronar si se puede acceder a las actividades

    class AdaptadorFragmentos extends FragmentStateAdapter {

        public AdaptadorFragmentos(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new MedicionFragment();
            } else if (position == 1) {
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
}