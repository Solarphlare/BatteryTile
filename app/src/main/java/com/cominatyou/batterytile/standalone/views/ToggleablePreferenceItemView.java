package com.cominatyou.batterytile.standalone.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cominatyou.batterytile.standalone.R;
import com.google.android.material.materialswitch.MaterialSwitch;

public class ToggleablePreferenceItemView extends PreferenceItemView {
    private MaterialSwitch switchView;

    public ToggleablePreferenceItemView(@NonNull Context context) {
        super(context);
    }

    public ToggleablePreferenceItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ToggleablePreferenceItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context, AttributeSet attrs) {
        super.init(context, attrs);

        switchView = new MaterialSwitch(context);
        ((LinearLayout) findViewById(R.id.preference_item_view_container)).addView(switchView);

        setOnClickListener(v -> switchView.toggle());
    }

    /**
     * Set the state of this preference's switch.
     * @param checked The new state of the switch.
     */
    public void setChecked(boolean checked) {
        switchView.setChecked(checked);
    }

    /**
     * Get the state of this preference's switch.
     * @return The current state of the switch.
     */
    public boolean isChecked() {
        return switchView.isChecked();
    }

    /**
     * Toggle the state of this preference's switch.
     */
    public void toggle() {
        switchView.toggle();
    }

    /**
     * Set a listener for when this preference's switch is toggled.
     * @param listener The listener to set.
     */
    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        switchView.setOnCheckedChangeListener(listener);
    }

    /**
     * Get a reference to this preference's switch.
     * @return A reference to the switch.
     */
    public MaterialSwitch getSwitch() {
        return switchView;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        switchView.setEnabled(enabled);
    }
}