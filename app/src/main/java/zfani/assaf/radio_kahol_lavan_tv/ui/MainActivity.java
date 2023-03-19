package zfani.assaf.radio_kahol_lavan_tv.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.Calendar;

import zfani.assaf.radio_kahol_lavan_tv.R;
import zfani.assaf.radio_kahol_lavan_tv.ui.fragments.BroadcastScheduleFragment;
import zfani.assaf.radio_kahol_lavan_tv.ui.fragments.LiveBroadcastFragment;

public class MainActivity extends AppCompatActivity implements View.OnFocusChangeListener, View.OnClickListener {

    private ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View mainTab1 = findViewById(R.id.mainTab1);
        View mainTab2 = findViewById(R.id.mainTab2);
        View mainTab3 = findViewById(R.id.mainTab3);

        mainTab1.setOnFocusChangeListener(this);
        mainTab2.setOnFocusChangeListener(this);
        mainTab3.setOnFocusChangeListener(this);

        mainTab1.setOnClickListener(this);
        mainTab2.setOnClickListener(this);
        mainTab3.setOnClickListener(this);

        mainTab3.setNextFocusDownId(getSelectedTabByDayId(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1));

        viewPager = findViewById(R.id.viewPager);

        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new LiveBroadcastFragment(true);
                    case 1:
                        return new BroadcastScheduleFragment();
                    case 2:
                        return new LiveBroadcastFragment(false);
                }
                return new Fragment();
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(2);
    }

    @Override
    public void onClick(View v) {
        viewPager.setCurrentItem(Integer.parseInt(v.getTag().toString()));
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            viewPager.setCurrentItem(Integer.parseInt(v.getTag().toString()));
            v.requestFocus();
        }
    }

    private int getSelectedTabByDayId(int day) {
        switch (day) {
            case 0:
                return R.id.tab1;
            case 1:
                return R.id.tab2;
            case 2:
                return R.id.tab3;
            case 3:
                return R.id.tab4;
            case 4:
                return R.id.tab5;
            case 5:
                return R.id.tab6;
            case 6:
                return R.id.tab7;
        }
        return 0;
    }
}
