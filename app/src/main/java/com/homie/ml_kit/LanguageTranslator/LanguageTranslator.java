package com.homie.ml_kit.LanguageTranslator;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.homie.ml_kit.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class LanguageTranslator extends Fragment {

    private static final String TAG = "ImageLabeling";

    private TextInputEditText text_et;
    private TextView language_result_tv;
    private Button submit_language_btn;
    private String input_text;

    private Spinner toSpinner,fromSpinner;

    private List<String> languages_list;
    private List<String> names_list;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_language_translator,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        text_et=view.findViewById(R.id.language_iden_edit_text);
        language_result_tv=view.findViewById(R.id.language_result_tv);
        submit_language_btn=view.findViewById(R.id.submit_language_btn);
        toSpinner=view.findViewById(R.id.to_spinnner);
        fromSpinner=view.findViewById(R.id.from_spinnner);

        languages_list= new ArrayList<>();
        languages_list= Arrays.asList(getActivity().getResources().getStringArray(R.array.languages));




//        Set<Integer> langugaes=FirebaseTranslateLanguage.getAllLanguages();


        toSpinnerSetup();
        fromSpinnerSetup();







        submit_language_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(text_et.getText().toString())){
                    Toast.makeText(getActivity(), "To Identify Enter Some Text First", Toast.LENGTH_LONG).show();
                }else {
                    input_text=text_et.getText().toString();

                        translate(fromSpinner.getSelectedItem().toString(),toSpinner.getSelectedItem().toString(),input_text);



                }

            }
        });







    }

    public static String getFirstTwoCharacters(String s , int character_count) {
        String upToNCharacters = s.substring(0, Math.min(s.length(), character_count));
        return upToNCharacters;

    }

    private void translate(String from, String to, final String input_text){
        //DE - germany
        //CA - french
        //EN - English


        String from_modified=getFirstTwoCharacters(from,2).toLowerCase();
        int from_int_code=FirebaseTranslateLanguage.languageForLanguageCode(from_modified);
        String to_modified=getFirstTwoCharacters(to,2);
        int to_int_code=FirebaseTranslateLanguage.languageForLanguageCode(to_modified);


        FirebaseTranslatorOptions options = null;
        options =
                new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(from_int_code)
                        .setTargetLanguage(to_int_code)
                        .build();
            final FirebaseTranslator englishGermanTranslator =
                    FirebaseNaturalLanguage.getInstance().getTranslator(options);

            FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                    .requireWifi()
                    .build();
            englishGermanTranslator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void v) {
                                    Log.d(TAG, "onSuccess: model");
                                    englishGermanTranslator.translate(input_text)
                                            .addOnSuccessListener(
                                                    new OnSuccessListener<String>() {
                                                        @Override
                                                        public void onSuccess(@NonNull String translatedText) {

                                                            Log.d(TAG, "onSuccess: complete " +translatedText);
                                                            language_result_tv.setText("Translated Text : \n"+translatedText);
                                                        }
                                                    })
                                            .addOnFailureListener(
                                                    new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Error.
                                                            // ...
                                                        }
                                                    });
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Model couldn’t be downloaded or other internal error.
                                    // ...
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

    private void toSpinnerSetup(){


        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity()
                ,android.R.layout.simple_spinner_dropdown_item,languages_list);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(adapter);

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //Toast.makeText(getActivity(), ""+parent.getItemAtPosition(position)+" Selected", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void fromSpinnerSetup(){

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity()
                ,android.R.layout.simple_spinner_dropdown_item,languages_list);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(adapter);

        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //Toast.makeText(getActivity(), ""+parent.getItemAtPosition(position)+" Selected", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
