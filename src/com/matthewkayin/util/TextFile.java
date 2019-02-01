//Java Text Files are stupidly obtuse so I wrote an abstraction to make it easier

package com.matthewkayin.util;

import java.io.*;
import java.util.ArrayList;

public class TextFile{

    private ArrayList<String> data;

    public TextFile(){

        data = null;
    }

    public TextFile(String path){

        data = null;
        read(path);
    }

    public void read(String path){

        try{

            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String line = null;
            int count = 0;
            data = new ArrayList<String>();
            while((line = br.readLine()) != null){

                data.add(line);
            }

            br.close();

        }catch(Exception e){

            e.printStackTrace();
        }
    }

    public void write(String path){

        if(data == null){

            System.out.println("ERROR! Cannot save an empty text file");
            return;
        }

        try{

            FileWriter fw = new FileWriter(path);
            BufferedWriter bw = new BufferedWriter(fw);

            for(String s : data){

                bw.write(s);
                bw.newLine();
            }

            bw.close();

        }catch(Exception e){

            e.printStackTrace();
        }
    }

    public String getLine(int i){

        if(data == null){

            System.out.println("ERROR! Cannot get line from empty file!");

            return null;
        }

        return data.get(i);
    }

    public void addLine(String v){

        if(data == null){

            data = new ArrayList<String>();
        }

        data.add(v);
    }

    public void setLine(int i, String v){

        if(data == null){

            System.out.println("ERROR! Cannot set line in empty file!");

            return;
        }

        data.set(i, v);
    }

    public void removeLine(int i){

        if(data == null){

            System.out.println("ERROR! Cannot remove line from empty file!");

            return;
        }

        data.remove(i);
    }

    public int noLines(){

        if(data == null){

            System.out.println("ERROR! Cannot get line numbers of empty file!");

            return -1;
        }

        return data.size();
    }

    public void forget(){

        data = null;
    }

    public boolean isInitialized(){

        return data == null;
    }
}
