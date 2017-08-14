package com.mu.util;

import com.mu.util.log.Log;
import hello.mu.util.MuLog;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

/**
 *
 * @author Peng Mu
 */
public class ProcessExecuter
{
    static public int exec(String cmd, boolean block)
    {
        int i=0;
       try{
        Process p = Runtime.getRuntime().exec(cmd);
        PrintStream err = new PrintStream(p.getErrorStream());
        PrintStream in = new PrintStream(p.getInputStream());
        err.start();
        in.start();
        if(block)
            i = p.waitFor();
       }catch(Exception e){MuLog.log(e);}
        
        return i;
    }

}

class PrintStream extends Thread
{
    private BufferedReader br;
    public PrintStream(InputStream in)
    {
        br = new BufferedReader(new InputStreamReader(in));
    }

    public void run()
    {
        String line=null;
       try{
        String lastLine=null;
        long lastPrint=0;
        while ( (line = br.readLine()) != null)
        {
            long interval = (System.currentTimeMillis() - lastPrint);
            if(similarity(line, lastLine)<0.65 || interval>2500)
            {
                Log.log(line);
                lastPrint = System.currentTimeMillis();
            }
            lastLine = line;
            
       }
       }catch(Exception e){Log.log(e);}
    }

    public float similarity(String s1, String s2)
    {
        if(s1==null || s2==null)
            return 0F;
        int l = s1.length()>s2.length()? s1.length():s2.length();
        int s = s1.length()<s2.length()? s1.length():s2.length();
        int same =0;
        for(int i=0; i<s; i++)
        {
            if(s1.charAt(i)==s2.charAt(i))
                same++;
        }
        float fs = same;
        return fs/l;
    }

}
