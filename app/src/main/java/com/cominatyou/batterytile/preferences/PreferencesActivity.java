package com.cominatyou.batterytile.preferences;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cominatyou.batterytile.R;
import com.github.k1rakishou.fsaf.FileChooser;
import com.github.k1rakishou.fsaf.callback.FSAFActivityCallbacks;
import com.google.android.material.color.DynamicColors;

public class PreferencesActivity extends AppCompatActivity implements FSAFActivityCallbacks {
    private FileChooser fileChooser;
    private int pendingFsafRequestCode = - 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivityIfAvailable(this);

        fileChooser = new FileChooser(this);
        fileChooser.setCallbacks(this);

        setContentView(R.layout.activity_preferences);
        new PreferencesBottomSheet().show(getSupportFragmentManager(), "preferences");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fileChooser.removeCallbacks();
    }

    private final ActivityResultLauncher<Intent> fsafLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (fileChooser != null && pendingFsafRequestCode != -1) {
            fileChooser.onActivityResult(pendingFsafRequestCode, result.getResultCode(), result.getData());
            pendingFsafRequestCode = -1;
        }
    });

    @Override
    public void fsafStartActivityForResult(@NonNull Intent intent, int requestCode) {
        this.pendingFsafRequestCode = requestCode;
        fsafLauncher.launch(intent);
    }

    public FileChooser getFileChooser() {
        return fileChooser;
    }
}
