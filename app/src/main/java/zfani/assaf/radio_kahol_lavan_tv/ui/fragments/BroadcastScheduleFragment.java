package zfani.assaf.radio_kahol_lavan_tv.ui.fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import zfani.assaf.radio_kahol_lavan_tv.App;
import zfani.assaf.radio_kahol_lavan_tv.R;
import zfani.assaf.radio_kahol_lavan_tv.adapters.BroadcastAdapter;
import zfani.assaf.radio_kahol_lavan_tv.base.BaseFragment;
import zfani.assaf.radio_kahol_lavan_tv.model.Broadcast;

public class BroadcastScheduleFragment extends BaseFragment {

    private ListView lvBroadcastList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_broadcast_schedule, container, false);
        LinearLayout llBroadcastTabs = root.findViewById(R.id.llBroadcastTabs);
        String[] daysTabTitles = getResources().getStringArray(R.array.tab_titles);
        for (int i = 0; i < daysTabTitles.length; i++) {
            TextView tvText = (TextView) llBroadcastTabs.getChildAt(i);
            tvText.setText(daysTabTitles[i]);
            tvText.setOnClickListener(View::requestFocus);
            tvText.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    bindData(Integer.parseInt(v.getTag().toString()));
                }
            });
            if (i == 6) {
                tvText.setOnKeyListener((v, keyCode, event) -> keyCode == KeyEvent.KEYCODE_DPAD_LEFT);
            }
        }
        lvBroadcastList = root.findViewById(R.id.lvBroadcastList);
        lvBroadcastList.setOnKeyListener(this);
        View currentDayTab = llBroadcastTabs.getChildAt(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1);
        currentDayTab.performClick();
        lvBroadcastList.setNextFocusUpId(currentDayTab.getId());
        return root;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT;
    }

    private void bindData(int position) {
        App.broadcasts.observe(getViewLifecycleOwner(), map -> {
            List<Broadcast> broadcastList = map.get(App.daysArray[position]);
            if (broadcastList != null) {
                Collections.sort(broadcastList);
                lvBroadcastList.setAdapter(new BroadcastAdapter(requireContext(), broadcastList));
                scrollToCurrentBroadcast(broadcastList);
            }
        });
    }

    private void scrollToCurrentBroadcast(List<Broadcast> broadcasts) {
        Date now = new Date(System.currentTimeMillis());
        for (int i = 0; i < broadcasts.size(); i++) {
            Broadcast broadcast = broadcasts.get(i);
            if (now.after(broadcast.startDate) && now.before(broadcast.endDate)) {
                lvBroadcastList.setSelection(i);
            }
        }
    }
}
