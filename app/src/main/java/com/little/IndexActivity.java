package com.little;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.little.camera2.Camera2Activity;
import com.little.camerax.CameraXActivity;
import com.little.camerax.R;

public class IndexActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        findViewById(R.id.project_camera2).setOnClickListener(mOnClickListener);
        findViewById(R.id.project_camerax).setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.project_camera2:
                    Intent intent1 = new Intent(IndexActivity.this, Camera2Activity.class);
                    startActivity(intent1);
                    break;
                case R.id.project_camerax:
                    Intent intent2 = new Intent(IndexActivity.this, CameraXActivity.class);
                    startActivity(intent2);
                    break;
            }
        }
    };
}
