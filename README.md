Dog vs Cat Image Classifier (TensorFlow Lite / Android)

This project is an Android mobile application that classifies images as either a dog or a cat using an on-device TensorFlow Lite machine learning model. Users can select an image from their device storage or camera and receive a real-time prediction directly on the phone.

Features

Select image from device storage or camera

On-device image classification (no internet required)

Real-time prediction display (Dog or Cat)

Simple and user-friendly interface

Fully offline TensorFlow Lite inference

Technologies Used

Android Studio

Kotlin

TensorFlow & Keras (Model Training)

TensorFlow Lite (Mobile Inference)

Google Colab (Model Training Environment)

How the App Works

The user selects an image from the device.

The image is resized and normalized.

The TensorFlow Lite model processes the image.

The app displays the predicted class: Dog or Cat.

Code Sources and Attribution

The following external tools and references were used to support the development of this project. All code was reviewed, understood, and adapted by the project team.

TensorFlow & Keras Documentation – Used as reference for building and exporting the machine learning model.

TensorFlow Lite Android Documentation – Used as reference for loading the .tflite model and performing on-device inference.

Android Developer Documentation – Used as reference for image selection and UI implementation.

All remaining application logic, UI structure, model integration, testing setup, and documentation were created by the project team.

How to Run the Project

Clone the repository from GitHub.

Open the project in Android Studio.

Sync Gradle files.

Run the app on an Android emulator or physical Android device.

Select an image and press Classify to view the result.
