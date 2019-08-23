package com.homie.ml_kit;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.homie.ml_kit.ImageLabel.ImageLabeling;
import com.homie.ml_kit.LanguageIdentification.LanguageIdentification;
import com.homie.ml_kit.LanguageTranslator.LanguageTranslator;
import com.homie.ml_kit.SmartReply.Chat.ChatFragment;
import com.homie.ml_kit.TextRecognition.TextRecognition;

public class ChooserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);

        Intent intent=getIntent();

        if(intent.hasExtra("smart_reply")){
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, ChatFragment.newInstance())
                        .commitNow();
            }
        }
        if(intent.hasExtra("image_labeling")){
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new ImageLabeling())
                        .commitNow();
            }
        }

        if(intent.hasExtra("text_recognizer")){
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new TextRecognition())
                        .commitNow();
            }
        }
        if(intent.hasExtra("language_identifier")){
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new LanguageIdentification())
                        .commitNow();
            }
        }
        if(intent.hasExtra("language_translation")){
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new LanguageTranslator())
                        .commitNow();
            }
        }
    }
}
