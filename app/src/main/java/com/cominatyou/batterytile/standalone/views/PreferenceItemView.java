package com.cominatyou.batterytile.standalone.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.cominatyou.batterytile.standalone.R;
import com.cominatyou.batterytile.standalone.databinding.PreferenceItemViewBinding;

public class PreferenceItemView extends LinearLayout {
    protected TextView titleView;
    protected TextView descriptionView;

    public PreferenceItemView(@NonNull Context context) {
        this(context, null);
    }

    public PreferenceItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreferenceItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        final View textLayout = LayoutInflater.from(context).inflate(R.layout.preference_item_view, this, true);
        final PreferenceItemViewBinding binding = PreferenceItemViewBinding.bind(textLayout);


        titleView = binding.preferenceItemTitle;
        descriptionView = binding.preferenceItemDescription;

        if (attrs != null) {
            try (final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PreferenceItemView)) {
                String title = a.getString(R.styleable.PreferenceItemView_preferenceTitle);
                String description = a.getString(R.styleable.PreferenceItemView_preferenceDescription);

                if (title != null) titleView.setText(title);
                if (description != null) descriptionView.setText(description);
            }
        }
    }

    /**
     * Set the title of this preference.
     * @param title The new title of the preference.
     */
    public void setTitle(CharSequence title) {
        titleView.setText(title);
    }

    /**
     * Set the title of this preference.
     * @param resId The resource ID of the string to use as the title.
     */
    public void setTitle(@StringRes int resId) {
        titleView.setText(resId);
    }

    /**
     * Set the description of this preference.
     * @param description The new description of the preference.
     */
    public void setDescription(CharSequence description) {
        descriptionView.setText(description);
    }

    /**
     * Set the description of this preference.
     * @param resId The resource ID of the string to use as the description.
     */
    public void setDescription(@StringRes int resId) {
        descriptionView.setText(resId);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        titleView.setAlpha(enabled ? 1.0f : 0.4f);
        descriptionView.setAlpha(enabled ? 1.0f : 0.4f);
    }
}
