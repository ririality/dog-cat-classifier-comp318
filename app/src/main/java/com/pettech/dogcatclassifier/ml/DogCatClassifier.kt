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
// Modified by Joshua Gates - Dec 2025 - Added comments and documentation

class DogCatClassifier(context: Context) {

    private val interpreter: Interpreter
    private val imageSize = 150 // Input image size expected by the TFLite model


    init {
        // Load the model from assets when the class is first created
        interpreter = Interpreter(loadModelFile(context))
    }

    /**
     * Loads the TensorFlow Lite model from the app's assets folder.
     * @param context The application context to access assets.
     * @return A MappedByteBuffer containing the loaded model.
     */
    private fun loadModelFile(context: Context): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd("dog_cat_model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    /**
    * Classifies a given bitmap image as either a "dog" or a "cat".
    * @param bitmap The input image to classify.
    * @return A string result: "dog" or "cat".
    */
    fun classify(bitmap: Bitmap): String {

        // Convert the bitmap to a format the model can understand
        val byteBuffer = convertBitmapToByteBuffer(bitmap)

        // Create an array to hold the model's output (a single float value)
        val output = Array(1) { FloatArray(1) }

        // Run inference on the model
        interpreter.run(byteBuffer, output)

        // The output is a confidence score
        val confidence = output[0][0]

        // If confidence is > 0.5, it's a dog, otherwise it's a cat.
        return if (confidence > 0.5f) "dog" else "cat"
    }

    /**
     * Converts a bitmap into a ByteBuffer suitable for the TFLite model.
     * This involves resizing the image and normalizing pixel values.
     * @param bitmap The bitmap to convert.
     * @return A ByteBuffer with the normalized image data.
     */
    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {

        // Resize the bitmap to the required 150x150 size
        val resized = Bitmap.createScaledBitmap(bitmap, imageSize, imageSize, true)

        // Allocate a direct buffer to hold the image data
        val buffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3) // 4 bytes/float, 3 channels (RGB)
        buffer.order(ByteOrder.nativeOrder())

        // Loop through each pixel and add its normalized value to the buffer
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
