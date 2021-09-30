package com.kano.auto.ml

import android.graphics.Bitmap
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import com.kano.auto.utils.MLog

object MLKit {
    private lateinit var objectDetector: ObjectDetector
    private lateinit var customObjectDetectorOptions: CustomObjectDetectorOptions
    private lateinit var localModel: LocalModel

    fun init() {
        localModel = LocalModel.Builder()
                .setAssetFilePath("lite-model_object_detection_mobile_object_labeler_v1_1.tflite")
//                .setAssetFilePath("lite-ssd_mobilenet_v1_1_metadata_1.tflite")
                // or .setAbsoluteFilePath(absolute file path to model file)
                // or .setUri(URI to model file)
                .build()

        // Multiple object detection in static images
        customObjectDetectorOptions =
                CustomObjectDetectorOptions.Builder(localModel)
                        .setDetectorMode(CustomObjectDetectorOptions.SINGLE_IMAGE_MODE)
                        .enableMultipleObjects()
                        .enableClassification()
                        .setClassificationConfidenceThreshold(0.5f)
                        .setMaxPerObjectLabelCount(3)
                        .build()

        objectDetector = ObjectDetection.getClient(customObjectDetectorOptions)
    }

    fun detectBitmap(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        objectDetector
                .process(image)
                .addOnSuccessListener { arrDetect ->
                    MLog.e("MLKit", "---------------------")
                    for (detect in arrDetect) {
                        val boundingBox = detect.boundingBox
                        val trackingId = detect.trackingId
                        for (label in detect.labels) {
                            val text = label.text
                            val index = label.index
                            val confidence = label.confidence
                            MLog.e("MLKit", "  -> $text")
                        }
                    }
                }.addOnCompleteListener {
                    bitmap.recycle()
                }.addOnFailureListener {
                    it.printStackTrace()
                }
    }
}