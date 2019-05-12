package com.little.camera2;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.little.camera2.fragment.Camera2Fragment;
import com.little.camerax.R;

public class Camera2Activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Fragment fragment = Fragment.instantiate(this, Camera2Fragment.class.getName());
        getSupportFragmentManager().beginTransaction().add(fragment, "camera2").commit();
    }
}
