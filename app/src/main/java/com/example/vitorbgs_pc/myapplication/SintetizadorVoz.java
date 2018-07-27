package com.example.vitorbgs_pc.myapplication;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class SintetizadorVoz {

    private TextToSpeech mTTS;
    private String ultimaFala = "";

    public SintetizadorVoz(Context context){
        mTTS = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int result = mTTS.setLanguage(new Locale("pt", "br"));
                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.i("TTS", "Language Not Supported");
                    }
                }
                else{
                    Log.i("TTS", "Inicialization failed");
                }
            }
        });
    }

    public void falar(String texto){
        if(texto != ultimaFala){
            mTTS.speak(texto, TextToSpeech.QUEUE_FLUSH, null);
            ultimaFala = texto;
        }
    }

}
