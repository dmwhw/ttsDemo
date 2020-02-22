package com.gzseeing.tts.feature;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button btn_say;
    private EditText et_content;
    private TTS tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_say=this.findViewById(R.id.btn_say);
        et_content =this.findViewById(R.id.et_content);
        btn_say.setOnClickListener(this);
        tts=SystemTTS.getInstance(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==btn_say.getId()){
            Log.i("SAY",et_content.getText().toString());
            tts.playText(et_content.getText().toString());
        }
    }
    @Override
    public void onDestroy(){
        tts.shutdown();
        Log.i("destroyedME","aaaa");
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        //横屏切换，或者灭屏会导致MainActivity发生回收。导致ttsShutdown。但是instance没有置空
        super.onResume();
        tts.resumeTTS();
    }
}
