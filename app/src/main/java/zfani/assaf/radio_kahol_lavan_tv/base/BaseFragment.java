package zfani.assaf.radio_kahol_lavan_tv.base;

import android.view.KeyEvent;
import android.view.View;

import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment implements View.OnKeyListener {

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT;
    }
}
