package es.uma.smartmeter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

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
}