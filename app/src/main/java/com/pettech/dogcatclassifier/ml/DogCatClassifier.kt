package com.pettech.dogcatclassifier.ml

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

// Marianna McCue – Dec 2025 – TensorFlow Lite Dog vs Cat classifier (runtime-safe)

class DogCatClassifier(context: Context) {

    private val interpreter: Interpreter
    private val imageSize = 150

    init {
        interpreter = Interpreter(loadModelFile(context))
    }

    private fun loadModelFile(context: Context): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd("dog_cat_model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun classify(bitmap: Bitmap): String {

        val byteBuffer = convertBitmapToByteBuffer(bitmap)

        val output = Array(1) { FloatArray(1) }

        interpreter.run(byteBuffer, output)

        val confidence = output[0][0]

        return if (confidence > 0.5f) "dog" else "cat"
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {

        val resized = Bitmap.createScaledBitmap(bitmap, imageSize, imageSize, true)

        val buffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
        buffer.order(ByteOrder.nativeOrder())

        for (y in 0 until imageSize) {
            for (x in 0 until imageSize) {
                val pixel = resized.getPixel(x, y)

                buffer.putFloat(((pixel shr 16) and 0xFF) / 255f)
                buffer.putFloat(((pixel shr 8) and 0xFF) / 255f)
                buffer.putFloat((pixel and 0xFF) / 255f)
            }
        }

        return buffer
    }
}
