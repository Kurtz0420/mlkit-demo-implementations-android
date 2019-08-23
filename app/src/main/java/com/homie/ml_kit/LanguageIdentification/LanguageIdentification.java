package com.homie.ml_kit.LanguageIdentification;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.homie.ml_kit.ImageLabel.Label;
import com.homie.ml_kit.ImageLabel.LabelsAdapter;
import com.homie.ml_kit.R;
import com.homie.ml_kit.Utils.Permissions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class LanguageIdentification extends Fragment {

    private static final String TAG = "ImageLabeling";

    private TextInputEditText text_et;
    private TextView language_result_tv;
    private Button submit_language_btn;
    private String input_text;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_language_identification,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        text_et=view.findViewById(R.id.language_iden_edit_text);
        language_result_tv=view.findViewById(R.id.language_result_tv);
        submit_language_btn=view.findViewById(R.id.submit_language_btn);




        submit_language_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(text_et.getText().toString())){
                    Toast.makeText(getActivity(), "To Identify Enter Some Text First", Toast.LENGTH_LONG).show();
                }else {
                    input_text=text_et.getText().toString();
                    identifyLanguageFromText(input_text);

                }

            }
        });







    }

    private void identifyLanguageFromText(String input_tex){

        FirebaseLanguageIdentification languageIdentifier =
                FirebaseNaturalLanguage.getInstance().getLanguageIdentification();
        languageIdentifier.identifyLanguage(input_tex)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@Nullable String languageCode) {
                                if (languageCode != "und") {

                                    language_result_tv.setText(languageCode);


                                    Log.i(TAG, "Language: " + languageCode);
                                } else {
                                    Log.i(TAG, "Can't identify language.");
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Model couldn’t be loaded or other internal error.
                                // ...
                            }
                        });

    }


}
