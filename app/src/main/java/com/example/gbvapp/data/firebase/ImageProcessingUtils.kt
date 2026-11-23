package com.example.gbvapp.data.firebase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import java.io.ByteArrayOutputStream

object ImageProcessingUtils {
    fun processImageToJpegBytes(context: Context, uri: Uri): ByteArray? {
        return try {
            val contentResolver = context.contentResolver

            var rotation = 0
            try {
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    val exifInterface = ExifInterface(inputStream)
                    val orientation = exifInterface.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED
                    )
                    rotation = when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270
                        else -> 0
                    }
                }
            } catch (_: Exception) {}

            var bitmap: Bitmap? = contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            } ?: return null

            val nonNullBitmap = bitmap ?: return null

            if (rotation != 0) {
                val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
                val rotatedBitmap = Bitmap.createBitmap(nonNullBitmap, 0, 0, nonNullBitmap.width, nonNullBitmap.height, matrix, true)
                if (rotatedBitmap != nonNullBitmap) {
                    nonNullBitmap.recycle()
                    bitmap = rotatedBitmap
                }
            }

            val finalBitmap = bitmap ?: nonNullBitmap
            val outputStream = ByteArrayOutputStream()
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            finalBitmap.recycle()
            outputStream.toByteArray()
        } catch (_: Exception) {
            null
        }
    }
}
