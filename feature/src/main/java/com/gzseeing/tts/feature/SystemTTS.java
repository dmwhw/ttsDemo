package com.gzseeing.tts.feature;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.Locale;


public class SystemTTS extends UtteranceProgressListener implements TTS, TextToSpeech.OnUtteranceCompletedListener ,TextToSpeech.OnInitListener {
    private Context mContext;
    private static SystemTTS singleton;
    private TextToSpeech textToSpeech; // 系统语音播报类
    private boolean isSuccess = true;

    public static SystemTTS getInstance(Context context) {
        if (singleton == null) {
            synchronized (SystemTTS.class) {
                if (singleton == null) {
                    singleton = new SystemTTS(context);
                }
            }
        }
        return singleton;
    }

    private SystemTTS(Context context) {
        this.mContext = context.getApplicationContext();
        this.initTTS();
    }

    private void initTTS(){
        textToSpeech = new TextToSpeech(mContext, this);

    }

    public void resumeTTS(){
        boolean usable=ismServiceConnectionUsable(textToSpeech);
        if (!usable){
            Log.e("SAY","no avaliable,init again....");
            this.shutdown();
            this.initTTS();
        }
    }

    public void playText(String playText) {
        //check connection
        this.resumeTTS();
        if (!isSuccess) {
            Log.e("SAY","no sucess...");

            return;
        }
        if (textToSpeech != null) {
            textToSpeech.speak(playText,
                    TextToSpeech.QUEUE_ADD, null, null);
        }
    }

    public void stopSpeak() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
    }


    //播报完成回调
    @Override
    public void onUtteranceCompleted(String utteranceId) {
        Log.e("SAY","onUtteranceCompleted"+utteranceId);

    }

    @Override
    public void onStart(String utteranceId) {
        Log.e("SAY","started"+utteranceId);

    }

    @Override
    public void onDone(String utteranceId) {
        Log.e("SAY","done"+utteranceId);

    }

    @Override
    public void onError(String utteranceId) {
        Log.e("SAY","error"+utteranceId);
    }

    @Override
    public void onInit(int status) {
        //系统语音初始化成功
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.CHINA);
            textToSpeech.setPitch(1.0f);// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
            textToSpeech.setSpeechRate(1.0f);
            textToSpeech.setOnUtteranceProgressListener(SystemTTS.this);
            textToSpeech.setOnUtteranceCompletedListener(SystemTTS.this);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                //系统不支持中文播报
                isSuccess = false;
            }

        }
        Log.e("SAY","success flag..."+(status == TextToSpeech.SUCCESS));

    }



    public void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.stop(); // 不管是否正在朗读TTS都被打断
            textToSpeech.shutdown(); // 关闭，释放资源
            textToSpeech = null;
        }
    }

    private static boolean ismServiceConnectionUsable(TextToSpeech tts) {

        boolean isBindConnection = true;
        if (tts == null){
            return false;
        }
        Field[] fields = tts.getClass().getDeclaredFields();
        for (int j = 0; j < fields.length; j++) {
            fields[j].setAccessible(true);
            if ("mServiceConnection".equals(fields[j].getName()) && "android.speech.tts.TextToSpeech$Connection".equals(fields[j].getType().getName())) {
                try {
                    if(fields[j].get(tts) == null){
                        isBindConnection = false;
                        Log.e("TTS", "*******反射判断 TTS -> mServiceConnection == null*******");
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return isBindConnection;
    }

}
