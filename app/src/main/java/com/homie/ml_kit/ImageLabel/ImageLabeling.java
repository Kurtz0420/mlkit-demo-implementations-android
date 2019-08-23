package com.homie.ml_kit.ImageLabel;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
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
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.homie.ml_kit.BuildConfig;
import com.homie.ml_kit.ChooserActivity;
import com.homie.ml_kit.R;
import com.homie.ml_kit.Utils.Permissions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.CAMERA_SERVICE;

public class ImageLabeling extends Fragment {

    private static final String TAG = "ImageLabeling";

    public static final int PICK_IMAGE_GALLERY = 1;
    public static final int PICK_IMAGE_CAMERA = 3;
    public static final String READ_EXTERNAL_STORAGE=Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final int REQUEST_EXTERNAL_PERMISSION_CODE = 666;


    private Button mOpenGalleryBtn,mOpenCamera;
    private ImageView imageView;
    private ListView labels_listView;
    private LabelsAdapter labelsAdapter;
    private List<Label> labels_list;
    private String fileloc;
    private Uri imageUri;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_label,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mOpenGalleryBtn=view.findViewById(R.id.open_gallery_image_label_btn);
        imageView=view.findViewById(R.id.image_label_imageView);
        labels_listView=view.findViewById(R.id.labels_listView);
        mOpenCamera=view.findViewById(R.id.open_camera_image_label_btn);
        labels_list=new ArrayList<>();

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
                if(labels_list.size() > 0){
                    labels_list.clear();
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

            Uri uri=data.getData();

            imageView.setImageURI(uri);

            FirebaseVisionImage image;
            try {
                image = FirebaseVisionImage.fromFilePath(getActivity(), uri);
                FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                        .getOnDeviceImageLabeler();
                labeler.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {

                        for (FirebaseVisionImageLabel label: firebaseVisionImageLabels) {
                            String text = label.getText();
                            String entityId = label.getEntityId();
                            float confidence = label.getConfidence();

                            labels_list.add(new Label(text,entityId,confidence));


                            Log.d(TAG, "onSuccess: -----------------------");
                            Log.d(TAG, "onSuccess: Text : "+text);
                            Log.d(TAG, "onSuccess: Entity Id : "+entityId);
                            Log.d(TAG, "onSuccess: Confidence : "+confidence);
                            Log.d(TAG, "onSuccess: -----------------------");
                        }

                        labelsAdapter=new LabelsAdapter(getActivity(),R.layout.label_item,labels_list);
                        labels_listView.setAdapter(labelsAdapter);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error Labeling", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }



        }

        //CAMERA
        if(requestCode == PICK_IMAGE_CAMERA && resultCode == RESULT_OK){

            FirebaseVisionImage image;
            Toast.makeText(getActivity(), "Image Selected", Toast.LENGTH_LONG).show();

            Uri uri=imageUri;

            Bitmap bitmap = null;


            bitmap=(Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            image = FirebaseVisionImage.fromBitmap(bitmap);

//                image = FirebaseVisionImage.fromFilePath(getActivity(), uri);
            FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                    .getOnDeviceImageLabeler();


            labeler.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                @Override
                public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {

                    Toast.makeText(getActivity(), "Labeling Success", Toast.LENGTH_LONG).show();
                    for (FirebaseVisionImageLabel label: firebaseVisionImageLabels) {
                        String text = label.getText();
                        String entityId = label.getEntityId();
                        float confidence = label.getConfidence();

                        labels_list.add(new Label(text,entityId,confidence));


                        Log.d(TAG, "onSuccess: -----------------------");
                        Log.d(TAG, "onSuccess: Text : "+text);
                        Log.d(TAG, "onSuccess: Entity Id : "+entityId);
                        Log.d(TAG, "onSuccess: Confidence : "+confidence);
                        Log.d(TAG, "onSuccess: -----------------------");
                    }

                    labelsAdapter=new LabelsAdapter(getActivity(),R.layout.label_item,labels_list);
                    labels_listView.setAdapter(labelsAdapter);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Error Labeling", Toast.LENGTH_LONG).show();
                }
            });


        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static final String[] PERMISSIONS_EXTERNAL_STORAGE = {
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
    };

    public boolean checkExternalStoragePermission(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            return true;
        }

        int readStoragePermissionState = ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE);
        int writeStoragePermissionState = ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);
        boolean externalStoragePermissionGranted = readStoragePermissionState == PackageManager.PERMISSION_GRANTED &&
                writeStoragePermissionState == PackageManager.PERMISSION_GRANTED;
        if (!externalStoragePermissionGranted) {
            requestPermissions(PERMISSIONS_EXTERNAL_STORAGE, REQUEST_EXTERNAL_PERMISSION_CODE);
        }

        return externalStoragePermissionGranted;
    }

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
