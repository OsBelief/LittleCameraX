package com.little.camerax

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.little.camerax.fragment.CameraFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().add(R.id.fragment_container, CameraFragment()).commit()
    }
}
