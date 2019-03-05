package nicelee.ui.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamManager extends Thread{
	Process process;
    InputStream inputStream;
    public StreamManager(Process process, InputStream inputStream) {
    	this.process = process;
        this.inputStream = inputStream;
    }
    
    public void run () {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = null;
        try {
            while((line = bufferedReader.readLine()) !=null ) {
            	System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        process.destroy();
        //System.out.println("转码完毕.");
    }
}