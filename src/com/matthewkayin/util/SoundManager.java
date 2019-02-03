package com.matthewkayin.util;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import javax.sound.sampled.*;

public class SoundManager{

    private ArrayList<Clip> clips;
    private ArrayList<String> tags;
    private SourceDataLine audioLine;

    public SoundManager(){

        clips = new ArrayList<Clip>();
        tags = new ArrayList<String>();
    }

    public void loadSound(String path, String tag){

        try{

            File sf = new File(path);
            AudioInputStream ain = AudioSystem.getAudioInputStream(sf);
            Clip clip = AudioSystem.getClip();
            clip.open(ain);
            clips.add(clip);
            tags.add(tag);

        }catch(Exception e){

            e.printStackTrace();
        }
    }

    public void playSound(String tag){

        int i = tags.indexOf(tag);
        if(clips.get(i).isActive()){

            clips.get(i).stop();
        }
        clips.get(i).setMicrosecondPosition(0);
        clips.get(i).start();
    }

    public void pauseSound(String tag){

        int i = tags.indexOf(tag);
        if(clips.get(i).isActive()){

            clips.get(i).stop();
        }
    }

    public void resetSound(String tag){

        int i = tags.indexOf(tag);
        clips.get(i).setMicrosecondPosition(0);
    }

    public void loopSound(String tag){

        int i = tags.indexOf(tag);
        clips.get(i).loop(Clip.LOOP_CONTINUOUSLY);
    }
}
