package com.example.vitorbgs_pc.myapplication;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class VoiceSynthesizer {

    private android.speech.tts.TextToSpeech mTTS;
    private String lastSpeech = "";

    public VoiceSynthesizer(Context context){
        mTTS = new android.speech.tts.TextToSpeech(context, new android.speech.tts.TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == android.speech.tts.TextToSpeech.SUCCESS){
                    int result = mTTS.setLanguage(new Locale("pt", "br"));
                    if(result == android.speech.tts.TextToSpeech.LANG_MISSING_DATA || result == android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.i("TTS", "Language Not Supported");
                    }
                }
                else{
                    Log.i("TTS", "Initialization failed");
                }
            }
        });
    }

    public void speak(String text){
        if(text != lastSpeech){
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            lastSpeech = text;
        }
    }

}
