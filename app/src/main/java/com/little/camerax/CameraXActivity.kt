package com.little.camerax

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.little.camerax.fragment.CameraFragment
import java.io.File

class CameraXActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        var fragment = CameraFragment() // Kotlin和Dart一样, 创建对象不使用"new"关键字
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit()
    }

    companion object {
        fun getOutputDirectory(context: Context): File {
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, "LittleCameraX").apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else context.applicationContext.filesDir
        }
    }
}
