package com.little.camerax.uitls

import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {
    fun createFile(baseFolder: File, format: String, extension: String): File {
        return File(baseFolder, SimpleDateFormat(format, Locale.US).format(System.currentTimeMillis()) + extension)
    }
}