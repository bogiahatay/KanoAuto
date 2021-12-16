package com.kano.auto.ml

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.kano.auto.utils.MLog

object MLKitText {
    const val TAG = "MLKitText";

    interface IOnTextRecognizer {
        fun onSuccess(textBlocks: List<Text.TextBlock>)
        fun onError(why: String)
    }

    private lateinit var recognizer: TextRecognizer
    fun init() {
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    }

    fun detectBitmap(bitmap: Bitmap, listener: IOnTextRecognizer) {
        val image = InputImage.fromBitmap(bitmap, 0)
        // [START run_detector]
        val result = recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // Task completed successfully
                    listener.onSuccess(visionText.textBlocks)
                    MLog.e(TAG, "---------------------")
                    for (block in visionText.textBlocks) {
                        val boundingBox = block.boundingBox
                        val cornerPoints = block.cornerPoints
                        val text = block.text
//                        MLog.e(TAG, text + " -> x=" + block.boundingBox?.left + "  y=" + block.boundingBox?.top)
                        for (line in block.lines) {
                            // ...
                            for (element in line.elements) {
                                // ...
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    listener.onError(e.message!!)
                    // Task failed with an exception
                }
    }
}