package com.pettech.dogcatclassifier.ml

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.ByteBuffer
import java.nio.ByteOrder

// Marianna McCue – Dec 2025 – TensorFlow Lite dog vs cat classifier

class DogCatClassifier(private val context: Context) {

    private val interpreter: Interpreter
    private val labels: List<String>
    private val imageSize = 150

    init {
        interpreter = Interpreter(loadModelFile())
        labels = loadLabels()
    }

    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd("dog_cat_model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun loadLabels(): List<String> {
        return context.assets.open("labels.txt").bufferedReader().useLines { it.toList() }
    }

    fun classify(input: FloatArray): String {
        val output = Array(1) { FloatArray(labels.size) }
        interpreter.run(input, output)

        val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: 0
        return labels[maxIndex]
    }
}
