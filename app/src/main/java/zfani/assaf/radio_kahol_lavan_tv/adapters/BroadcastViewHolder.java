package zfani.assaf.radio_kahol_lavan_tv.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import zfani.assaf.radio_kahol_lavan_tv.R;
import zfani.assaf.radio_kahol_lavan_tv.model.Broadcast;

public class BroadcastViewHolder {

    private final TextView tvName, tvBroadcastName, tvDescription, tvTimes;
    private final ImageView ivIcon;
    private final View mainView;

    public BroadcastViewHolder(@NonNull View mainView) {
        this.mainView = mainView;
        tvName = mainView.findViewById(R.id.tvName);
        tvBroadcastName = mainView.findViewById(R.id.tvBroadcastName);
        tvDescription = mainView.findViewById(R.id.tvDescription);
        tvTimes = mainView.findViewById(R.id.tvTimes);
        ivIcon = mainView.findViewById(R.id.ivIcon);
    }

    public void bindData(Broadcast broadcast) {
        if (broadcast == null) {
            mainView.setVisibility(View.GONE);
        } else {
            mainView.setVisibility(View.VISIBLE);
            Glide.with(mainView.getContext()).load(broadcast.broadcasterImageUrl).circleCrop().into(ivIcon);
            tvName.setText(broadcast.name);
            tvBroadcastName.setText(broadcast.broadcasterName);
            tvDescription.setText(broadcast.description);
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            tvTimes.setText(new StringBuilder(dateFormat.format(broadcast.startDate) + "\n" + "עד" + "\n" + dateFormat.format(broadcast.endDate)));
            Date now = new Date(System.currentTimeMillis());
            mainView.findViewById(R.id.clContainer).setBackgroundResource(now.after(broadcast.startDate) && now.before(broadcast.endDate) ? R.drawable.bg_white : R.drawable.bg_gray);
        }
    }

    public View getMainView() {
        return mainView;
    }
}
