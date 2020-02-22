package com.gzseeing.tts.feature;

public interface TTS {
    void playText(String playText);

    void stopSpeak();

    void shutdown();

    void resumeTTS();
}

