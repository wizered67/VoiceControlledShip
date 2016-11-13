package com.mygdx.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * This class contains native android code. The methods may be called by libGDX.
 * Created by hubert on 19.10.14.
 */
public class ActionResolverAndroid extends Activity implements ActionResolver {

    Handler uiThread;

    Context appContext;
    Game gdx;
    SpeechRecognizer speechRecognizer;

    public ActionResolverAndroid(Context appContext) {
        uiThread = new Handler();
        this.appContext = appContext;
    }

    @Override
    public void showToast(final CharSequence toastMessage, final int toastDuration) {
        uiThread.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(appContext, toastMessage, toastDuration).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void recognizeSpeech() {
        uiThread.post(new Runnable() {
            @Override
            public void run() {
                if (speechRecognizer != null) {
                    speechRecognizer.destroy();
                }
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(appContext);
                speechRecognizer.setRecognitionListener(new MyListener(gdx, appContext));

                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                i.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
                gdx.shakeMicButton();
                speechRecognizer.startListening(i);
            }
        });
    }
    @Override
    public void stopSpeech() {
        uiThread.post(new Runnable() {
            @Override
            public void run() {
                speechRecognizer.stopListening();
                }});
    }

    public void setGdx(Game game) {
        this.gdx = game;
    }

}
