package com.homie.ml_kit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button mSmart_Reply_Btn, mImageLabe_Btn,mText_Recognizer_Btn,mLanguage_identifier_Btn,mLanguage_Translation_Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSmart_Reply_Btn=findViewById(R.id.smart_reply_button);
        mImageLabe_Btn=findViewById(R.id.image_labeling_button);
        mText_Recognizer_Btn=findViewById(R.id.text_recognizer_button);
        mLanguage_identifier_Btn=findViewById(R.id.language_iden_button);
        mLanguage_Translation_Btn=findViewById(R.id.language_translation_button);

        mText_Recognizer_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ChooserActivity.class).
                        putExtra("text_recognizer","text_recognizer"));
            }
        });

        mSmart_Reply_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ChooserActivity.class)
                .putExtra("smart_reply","smart_reply"));
            }
        });

        mImageLabe_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,ChooserActivity.class)
                        .putExtra("image_labeling","image_labeling"));

            }
        });

        mLanguage_identifier_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ChooserActivity.class)
                        .putExtra("language_identifier","language_identifier"));
            }
        });

        mLanguage_Translation_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ChooserActivity.class)
                        .putExtra("language_translation","language_translation"));
            }
        });


    }
}