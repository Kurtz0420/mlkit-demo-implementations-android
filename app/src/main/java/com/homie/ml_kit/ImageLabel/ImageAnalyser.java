package com.homie.ml_kit.ImageLabel;

//
//import android.media.Image;
//
//import com.google.firebase.ml.vision.common.FirebaseVisionImage;
//import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
//
//private class ImageAnalyser implements ImageAnalysis.Analyzer {
//
//    private int degreesToFirebaseRotation(int degrees) {
//        switch (degrees) {
//            case 0:
//                return FirebaseVisionImageMetadata.ROTATION_0;
//            case 90:
//                return FirebaseVisionImageMetadata.ROTATION_90;
//            case 180:
//                return FirebaseVisionImageMetadata.ROTATION_180;
//            case 270:
//                return FirebaseVisionImageMetadata.ROTATION_270;
//            default:
//                throw new IllegalArgumentException(
//                        "Rotation must be 0, 90, 180, or 270.");
//        }
//    }
//
//    @Override
//    public void analyze(ImageProxy imageProxy, int degrees) {
//        if (imageProxy == null || imageProxy.getImage() == null) {
//            return;
//        }
//        Image mediaImage = imageProxy.getImage();
//        int rotation = degreesToFirebaseRotation(degrees);
//        FirebaseVisionImage image =
//                FirebaseVisionImage.fromMediaImage(mediaImage, rotation);
//        // Pass image to an ML Kit Vision API
//        // ...
//    }
//}