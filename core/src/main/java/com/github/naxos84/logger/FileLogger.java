package com.github.naxos84.logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class FileLogger {

    FileHandle fileHandle = Gdx.files.local("SurvislandLog.txt");

    public void log(String message) {
        fileHandle.writeString(message + "\n", true);
    }
}
