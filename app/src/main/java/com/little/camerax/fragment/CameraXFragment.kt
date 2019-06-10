package com.little.camerax.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.util.DisplayMetrics
import android.util.Log
import android.util.Rational
import android.view.*
import android.webkit.MimeTypeMap
import android.widget.ImageButton
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.little.camerax.CameraXActivity
import com.little.camerax.R
import com.little.camerax.uitls.FileUtils
import java.io.File

private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

private const val TAG = "CameraX"

private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
private const val FILE_EXTENSION = ".jpg"

class CameraFragment : Fragment() {
    private var lensFacing = CameraX.LensFacing.BACK
    private var imageCapture: ImageCapture? = null  // imageCapture可以为null

    private lateinit var viewFinder: TextureView    // lateinit标识延迟初始化, 否则或者在定义时初始化, 或者在构造函数中初始化
    private lateinit var container: ConstraintLayout

    private lateinit var outputDirectory: File

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frament_camera, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        container = view as ConstraintLayout    // as强制类型转换
        viewFinder = view.findViewById(R.id.view_finder)

        outputDirectory = CameraXActivity.getOutputDirectory(requireContext())

        if (allPermissionsGranted()) {
            viewFinder.post {
                updateCameraUI()
                buildCameraUseCase()
            }
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }
    }

    private fun updateCameraUI() {
        container.findViewById<ConstraintLayout>(R.id.camera_ui_container)?.let {
            container.removeView(it)
        }

        var controls = View.inflate(requireContext(), R.layout.camera_ui_container, container)
        controls.findViewById<ImageButton>(R.id.camera_capture).setOnClickListener {
            imageCapture?.let { imageCapture ->
                var photoFile = FileUtils.createFile(outputDirectory, FILENAME, FILE_EXTENSION)
                var metaData = ImageCapture.Metadata().apply {
                    isReversedHorizontal = lensFacing == CameraX.LensFacing.FRONT
                }

                imageCapture.takePicture(photoFile, imageSavedListener, metaData)

                // 拍摄的动画
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    container.postDelayed({
                        container.foreground = ColorDrawable(Color.WHITE)
                        container.postDelayed({
                            container.foreground = null
                        }, 50)
                    }, 100)
                }
            }
        }
    }

    private fun buildCameraUseCase() {
        // 预览
        var metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }   // also当前对象作为参数, 返回当前对象
        var screenAspectRatio = Rational(metrics.widthPixels, metrics.heightPixels)

        val previewConfig = PreviewConfig.Builder().apply {
            setTargetAspectRatio(screenAspectRatio) // 宽高比
            setTargetRotation(viewFinder.display.rotation)
        }.build()   // apply操作当前对象, 返回当前对象

        var preview = Preview(previewConfig)
        preview.setOnPreviewOutputUpdateListener {
            viewFinder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }

        // 拍摄
        var imageCaptureConfig = ImageCaptureConfig.Builder().apply {
            setLensFacing(lensFacing) // 前置or后置
            setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)    // 优先考虑拍摄时间
            setTargetAspectRatio(screenAspectRatio)
            setTargetRotation(viewFinder.display.rotation)
        }.build()
        imageCapture = ImageCapture(imageCaptureConfig)

        CameraX.bindToLifecycle(this, preview, imageCapture)    // 将支持的功能绑定到组件生命周期上
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewFinder.post {
                    updateCameraUI()
                    buildCameraUseCase()
                }
            } else {
                Toast.makeText(activity, "Permissions not granted by user.", Toast.LENGTH_SHORT).show()
                activity?.finish()  // activity是可空类型(Nullable Type), 使用时要加?, 如果activity为null, 则这行代码不会执行
            }
        }
    }

    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED)
                return false
        }
        return true
    }

    private fun updateTransform() {
        var matrix = Matrix()
        var centerX = viewFinder.width / 2f
        var centerY = viewFinder.height / 2f
        var rotationDegrees = when (viewFinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        viewFinder.setTransform(matrix)
    }

    private val imageSavedListener = object : ImageCapture.OnImageSavedListener {
        override fun onImageSaved(photoFile: File) {
            Log.i(TAG, "photo capture succeed: ${photoFile.absolutePath}")
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                requireActivity().sendBroadcast(
                    Intent(android.hardware.Camera.ACTION_NEW_PICTURE).setData(
                        Uri.fromFile(
                            photoFile
                        )
                    )
                )
            }
            var mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(photoFile.extension)
            MediaScannerConnection.scanFile(context, arrayOf(photoFile.absolutePath), arrayOf(mimeType), null)
        }

        override fun onError(useCaseError: ImageCapture.UseCaseError, message: String, cause: Throwable?) {
            Log.e(TAG, "photo capture failed: $message", cause)
        }
    }
}