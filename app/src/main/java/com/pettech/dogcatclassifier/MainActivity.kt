package com.pettech.dogcatclassifier

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.pettech.dogcatclassifier.ml.DogCatClassifier

// Marianna McCue – Dec 2025 – Main activity using system file picker (Activity version)

class MainActivity : Activity() {

    private lateinit var imgPreview: ImageView
    private lateinit var btnSelectImage: Button
    private lateinit var btnClassify: Button
    private lateinit var txtResult: TextView

    private lateinit var classifier: DogCatClassifier
    private var selectedBitmap: Bitmap? = null

    private val IMAGE_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgPreview = findViewById(R.id.imgPreview)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnClassify = findViewById(R.id.btnClassify)
        txtResult = findViewById(R.id.txtResult)

        classifier = DogCatClassifier(this)

        btnClassify.isEnabled = false
        txtResult.text = "Select an image to begin"

        btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_REQUEST_CODE)
        }

        btnClassify.setOnClickListener {
            selectedBitmap?.let {
                txtResult.text = "Classifying..."
                val result = classifier.classify(it)
                txtResult.text = "Prediction: $result"
            } ?: run {
                txtResult.text = "Please select an image first"
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            imageUri?.let {
                contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            selectedBitmap = bitmap
            imgPreview.setImageBitmap(bitmap)

            // Enable classify now that we have an image
            btnClassify.isEnabled = true
            txtResult.text = "Ready to classify"
        }
    }
}
