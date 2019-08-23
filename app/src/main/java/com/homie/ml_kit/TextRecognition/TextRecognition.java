package com.homie.ml_kit.TextRecognition;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionCloudDocumentRecognizerOptions;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;
import com.homie.ml_kit.ImageLabel.Label;
import com.homie.ml_kit.ImageLabel.LabelsAdapter;
import com.homie.ml_kit.R;
import com.homie.ml_kit.Utils.Permissions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class TextRecognition extends Fragment {

    private static final String TAG = "ImageLabeling";

    public static final int PICK_IMAGE_GALLERY = 1;
    public static final int PICK_IMAGE_CAMERA = 3;
    public static final int PICK_IMAGE_DOCUMENT = 2;
    public static final String READ_EXTERNAL_STORAGE=Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final int REQUEST_EXTERNAL_PERMISSION_CODE = 666;


    private Button mOpenGalleryBtn,mOpenCamera,mScan_Documents_Btn;
    private ImageView imageView;
    private ListView labels_listView;
    private ListViewAdapter listViewAdapter;
    private List<String> lines_list;
    private String fileloc;
    private Uri imageUri;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_text_recognition,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mOpenGalleryBtn=view.findViewById(R.id.open_gallery_image_label_btn);
        imageView=view.findViewById(R.id.image_label_imageView);
        labels_listView=view.findViewById(R.id.labels_listView);
        mOpenCamera=view.findViewById(R.id.open_camera_image_label_btn);
        mScan_Documents_Btn=view.findViewById(R.id.scan_documents_image_label_btn);
        lines_list=new ArrayList<>();


        mScan_Documents_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_DOCUMENT);
            }
        });

        mOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //checkExternalStoragePermission(getActivity());

                opencamera();
            }
        });


        mOpenGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lines_list.size() > 0){
                    lines_list.clear();
                }
                openGallery();
            }
        });



    }


    private void opencamera()
    {
        if(checkPermission(Permissions.CAMERA_PERMISSIONS[0])){
            Log.d(TAG, "onClick: Starting camera");
            Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent,PICK_IMAGE_CAMERA);

        }
    }
    private void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //GALLERY
        if (requestCode == PICK_IMAGE_GALLERY) {
            Toast.makeText(getActivity(), "Image Selected", Toast.LENGTH_LONG).show();

            Uri uri = data.getData();

            imageView.setImageURI(uri);

            FirebaseVisionImage image;
            try {
                image = FirebaseVisionImage.fromFilePath(getActivity(), uri);
                FirebaseVisionTextRecognizer text_recognizer = FirebaseVision.getInstance()
                        .getOnDeviceTextRecognizer();

                Task<FirebaseVisionText> result =
                        text_recognizer.processImage(image)
                                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                    @Override
                                    public void onSuccess(FirebaseVisionText firebaseVisionText) {

                                        String resultText=firebaseVisionText.getText();

                                        for(FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()){
                                            String blockText=block.getText();
                                            Float blockConfidence = block.getConfidence();
                                            List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
                                            Point[] blockCornerPoints = block.getCornerPoints();
                                            Rect blockFrame = block.getBoundingBox();

                                            Log.d(TAG, "onSuccess: -----------BLOCKS---------------");
                                            Log.d(TAG, "onSuccess: blockText : "+blockText);
                                            Log.d(TAG, "onSuccess: blockConfidence : "+blockConfidence);
                                            Log.d(TAG, "onSuccess: blockLanguages : "+blockLanguages.size());
                                            Log.d(TAG, "onSuccess: blockCornerPoints : "+blockCornerPoints);
                                            Log.d(TAG, "onSuccess: blockFrame : "+blockFrame);
                                            Log.d(TAG, "onSuccess: -----------BLOCKS---------------");

                                            for(FirebaseVisionText.Line line : block.getLines()){

                                                String lineText=line.getText();
                                                Float lineConfidence = line.getConfidence();
                                                List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                                                Point[] lineCornerPoints = line.getCornerPoints();
                                                Rect lineFrame = line.getBoundingBox();

                                                lines_list.add(lineText);

                                                Log.d(TAG, "onSuccess: -----------LINES---------------");
                                                Log.d(TAG, "onSuccess: lineText : "+lineText);
                                                Log.d(TAG, "onSuccess: lineConfidence : "+lineConfidence);
                                                Log.d(TAG, "onSuccess: lineLanguages : "+lineLanguages.size());
                                                Log.d(TAG, "onSuccess: lineCornerPoints : "+lineCornerPoints);
                                                Log.d(TAG, "onSuccess: lineFrame : "+lineFrame);
                                                Log.d(TAG, "onSuccess: -----------LINES---------------");


                                                for(FirebaseVisionText.Element element : line.getElements()){

                                                    String elementText=element.getText();
                                                    Float elementConfidence = element.getConfidence();
                                                    List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                                                    Point[] elementCornerPoints = element.getCornerPoints();
                                                    Rect elementFrame = element.getBoundingBox();

                                                    Log.d(TAG, "onSuccess: -----------LINES---------------");
                                                    Log.d(TAG, "onSuccess: elementText : "+elementText);
                                                    Log.d(TAG, "onSuccess: elementConfidence : "+elementConfidence);
                                                    Log.d(TAG, "onSuccess: elementLanguages : "+elementLanguages.size());
                                                    Log.d(TAG, "onSuccess: elementCornerPoints : "+elementCornerPoints);
                                                    Log.d(TAG, "onSuccess: elementFrame : "+elementFrame);
                                                    Log.d(TAG, "onSuccess: -----------LINES---------------");

                                                }
                                            }
                                        }

                                        listViewAdapter=new ListViewAdapter(getActivity(),R.layout.text_recognition_item,lines_list);
                                        labels_listView.setAdapter(listViewAdapter);



                                        //Log.d(TAG, "onSuccess: resultText " +resultText);
                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                            }
                                        });



            } catch (IOException e) {
                e.printStackTrace();
            }

        } //CAMERA
        if(requestCode == PICK_IMAGE_CAMERA && resultCode == RESULT_OK){

            FirebaseVisionImage image;
            Toast.makeText(getActivity(), "Image Selected", Toast.LENGTH_LONG).show();

            Uri uri=imageUri;

            Bitmap bitmap = null;


            bitmap=(Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            image = FirebaseVisionImage.fromBitmap(bitmap);

//                image = FirebaseVisionImage.fromFilePath(getActivity(), uri);
            FirebaseVisionTextRecognizer text_recognizer = FirebaseVision.getInstance()
                    .getOnDeviceTextRecognizer();

            Task<FirebaseVisionText> result =
                    text_recognizer.processImage(image)
                            .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                @Override
                                public void onSuccess(FirebaseVisionText firebaseVisionText) {

                                    String resultText=firebaseVisionText.getText();

                                    for(FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()){
                                        String blockText=block.getText();
                                        Float blockConfidence = block.getConfidence();
                                        List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
                                        Point[] blockCornerPoints = block.getCornerPoints();
                                        Rect blockFrame = block.getBoundingBox();

                                        Log.d(TAG, "onSuccess: -----------BLOCKS---------------");
                                        Log.d(TAG, "onSuccess: blockText : "+blockText);
                                        Log.d(TAG, "onSuccess: blockConfidence : "+blockConfidence);
                                        Log.d(TAG, "onSuccess: blockLanguages : "+blockLanguages.size());
                                        Log.d(TAG, "onSuccess: blockCornerPoints : "+blockCornerPoints);
                                        Log.d(TAG, "onSuccess: blockFrame : "+blockFrame);
                                        Log.d(TAG, "onSuccess: -----------BLOCKS---------------");

                                        for(FirebaseVisionText.Line line : block.getLines()){

                                            String lineText=line.getText();
                                            Float lineConfidence = line.getConfidence();
                                            List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                                            Point[] lineCornerPoints = line.getCornerPoints();
                                            Rect lineFrame = line.getBoundingBox();

                                            lines_list.add(lineText);

                                            Log.d(TAG, "onSuccess: -----------LINES---------------");
                                            Log.d(TAG, "onSuccess: lineText : "+lineText);
                                            Log.d(TAG, "onSuccess: lineConfidence : "+lineConfidence);
                                            Log.d(TAG, "onSuccess: lineLanguages : "+lineLanguages.size());
                                            Log.d(TAG, "onSuccess: lineCornerPoints : "+lineCornerPoints);
                                            Log.d(TAG, "onSuccess: lineFrame : "+lineFrame);
                                            Log.d(TAG, "onSuccess: -----------LINES---------------");


                                            for(FirebaseVisionText.Element element : line.getElements()){

                                                String elementText=element.getText();
                                                Float elementConfidence = element.getConfidence();
                                                List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                                                Point[] elementCornerPoints = element.getCornerPoints();
                                                Rect elementFrame = element.getBoundingBox();

                                                Log.d(TAG, "onSuccess: -----------LINES---------------");
                                                Log.d(TAG, "onSuccess: elementText : "+elementText);
                                                Log.d(TAG, "onSuccess: elementConfidence : "+elementConfidence);
                                                Log.d(TAG, "onSuccess: elementLanguages : "+elementLanguages.size());
                                                Log.d(TAG, "onSuccess: elementCornerPoints : "+elementCornerPoints);
                                                Log.d(TAG, "onSuccess: elementFrame : "+elementFrame);
                                                Log.d(TAG, "onSuccess: -----------LINES---------------");

                                            }
                                        }
                                    }

                                    listViewAdapter=new ListViewAdapter(getActivity(),R.layout.text_recognition_item,lines_list);
                                    labels_listView.setAdapter(listViewAdapter);



                                    //Log.d(TAG, "onSuccess: resultText " +resultText);
                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                            // ...
                                        }
                                    });


        }


        //DOCUMENTS FROM GALLERY
        if(requestCode == PICK_IMAGE_DOCUMENT && resultCode == RESULT_OK){
            Toast.makeText(getActivity(), "Document Selected", Toast.LENGTH_LONG).show();

            Uri uri = data.getData();

            imageView.setImageURI(uri);

            FirebaseVisionImage image;

            try {
                image = FirebaseVisionImage.fromFilePath(getActivity(), uri);

                FirebaseVisionDocumentTextRecognizer detector = FirebaseVision.getInstance()
                        .getCloudDocumentTextRecognizer();


                detector.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionDocumentText>() {
                            @Override
                            public void onSuccess(FirebaseVisionDocumentText result) {

                                String resultText=result.getText();

                                //get recognized data in blocks
                                for (FirebaseVisionDocumentText.Block block: result.getBlocks()){

                                    String blockText = block.getText();
                                    Float blockConfidence = block.getConfidence();
                                    List<RecognizedLanguage> blockRecognizedLanguages = block.getRecognizedLanguages();
                                    Rect blockFrame = block.getBoundingBox();

                                    Log.d(TAG, "onSuccess: -----------BLOCKS---------------");
                                    Log.d(TAG, "onSuccess: blockText : "+blockText);
                                    Log.d(TAG, "onSuccess: blockConfidence : "+blockConfidence);
                                    Log.d(TAG, "onSuccess: blockLanguages : "+blockRecognizedLanguages.size());
                                    Log.d(TAG, "onSuccess: blockFrame : "+blockFrame);
                                    Log.d(TAG, "onSuccess: -----------BLOCKS---------------");


                                    //separate into paragragraphs

                                    for (FirebaseVisionDocumentText.Paragraph paragraph: block.getParagraphs()) {
                                        String paragraphText = paragraph.getText();
                                        Float paragraphConfidence = paragraph.getConfidence();
                                        List<RecognizedLanguage> paragraphRecognizedLanguages = paragraph.getRecognizedLanguages();
                                        Rect paragraphFrame = paragraph.getBoundingBox();

                                        Log.d(TAG, "onSuccess: -----------PARAGRAPH---------------");
                                        Log.d(TAG, "onSuccess: paragraphText : "+paragraphText);
                                        Log.d(TAG, "onSuccess: paragraphConfidence : "+paragraphConfidence);
                                        Log.d(TAG, "onSuccess: paragraphRecognizedLanguages : "+paragraphRecognizedLanguages.size());
                                        Log.d(TAG, "onSuccess: paragraphFrame : "+paragraphFrame);
                                        Log.d(TAG, "onSuccess: -----------PARAGRAPH---------------");

                                        for (FirebaseVisionDocumentText.Word word: paragraph.getWords()) {
                                            String wordText = word.getText();
                                            Float wordConfidence = word.getConfidence();
                                            List<RecognizedLanguage> wordRecognizedLanguages = word.getRecognizedLanguages();
                                            Rect wordFrame = word.getBoundingBox();

                                            Log.d(TAG, "onSuccess: -----------WORDS---------------");
                                            Log.d(TAG, "onSuccess: wordText : "+wordText);
                                            Log.d(TAG, "onSuccess: wordConfidence : "+wordConfidence);
                                            Log.d(TAG, "onSuccess: wordRecognizedLanguages : "+wordRecognizedLanguages.size());
                                            Log.d(TAG, "onSuccess: wordFrame : "+wordFrame);
                                            Log.d(TAG, "onSuccess: -----------WORDS---------------");

                                            for (FirebaseVisionDocumentText.Symbol symbol: word.getSymbols()) {
                                                String symbolText = symbol.getText();
                                                Float symbolConfidence = symbol.getConfidence();
                                                List<RecognizedLanguage> symbolRecognizedLanguages = symbol.getRecognizedLanguages();
                                                Rect symbolFrame = symbol.getBoundingBox();

                                                Log.d(TAG, "onSuccess: -----------SYMBOLS---------------");
                                                Log.d(TAG, "onSuccess: symbolText : "+symbolText);
                                                Log.d(TAG, "onSuccess: symbolConfidence : "+symbolConfidence);
                                                Log.d(TAG, "onSuccess: symbolRecognizedLanguages : "+symbolRecognizedLanguages.size());
                                                Log.d(TAG, "onSuccess: symbolFrame : "+symbolFrame);
                                                Log.d(TAG, "onSuccess: -----------SYMBOLS---------------");
                                            }

                                        }

                                    }
                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                // ...
                                Toast.makeText(getActivity(), "Processing Failed", Toast.LENGTH_LONG).show();
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }



        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static final String[] PERMISSIONS_EXTERNAL_STORAGE = {
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 69) {
               opencamera();
            }
        }
    }


    //Checks a single permission passed from checkpermissionArray
    public boolean checkPermission(String permission) {
        Log.d(TAG, "checkPermission: checking permission"+permission);
        int permissionRequest= ActivityCompat.checkSelfPermission(getActivity(),permission);
        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermission:  permission was not granted for "+permission);
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, 69);
            return false;
        }
        else{
            Log.d(TAG, "checkPermission: Permission was granted for " +permission);
            //ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, 69);

            return true;
        }
    }


}
