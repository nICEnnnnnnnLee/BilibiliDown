package nicelee.ui.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import nicelee.ui.Global;

public class StreamManager extends Thread{
	Process process;
    InputStream inputStream;
    public StreamManager(Process process, InputStream inputStream) {
    	this.process = process;
        this.inputStream = inputStream;
    }
    
    public void run () {
        try {
        	InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        	BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        	String line = null;
            while((line = bufferedReader.readLine()) !=null ) {
            	if(Global.debugCmd)
            		System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        process.destroy();
        //System.out.println("转码完毕.");
    }
}