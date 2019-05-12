package com.little.camerax

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.little.camerax.fragment.CameraFragment

class CameraXActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        var fragment = CameraFragment() // Kotlin和Dart一样, 创建对象不使用"new"关键字
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit()
    }
}
