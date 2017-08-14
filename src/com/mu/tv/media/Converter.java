package com.mu.tv.media;

import com.mu.util.ProcessExecuter;
import com.mu.util.log.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Peng Mu
 */
public class Converter extends Thread
{
    static private ConcurrentLinkedQueue<String[]> dirs = new ConcurrentLinkedQueue<String[]>();
    private boolean stop = false;

    public void addMonitorDir(String inDir, String outDir, String quality, boolean includeSubDir)
    {
        String[] array = {inDir, outDir, quality, includeSubDir+""};
        dirs.add(array);
        Thread.interrupted();
    }
    public void removeMonitorDir(String inDir)
    {
        for(String[] s : dirs)
            if(s[0].equalsIgnoreCase(inDir))
                dirs.remove(s);
    }
    public Queue <String[]> getMonitorDirs()
    {
        return dirs;
    }

    public void run()
    {
        while(!stop)
        {
            try{
            String[] arr = getNextFile();
            if(arr==null)
            {
                Log.info("No File to Convert, Sleep 15sec...");
                Thread.sleep(15*1000);
            }
            else
            {
                convertFile(arr);
            }
            }catch(Exception e){Log.error(e);}
        }
    }
    private void convertFile(String[] arr)
    {
        try{
        File batFile = File.createTempFile("converter", ".bat");
        FileOutputStream bat = new FileOutputStream(batFile);
        String s = String.format("\"C:\\Program Files\\Handbrake\\HandBrakeCLI.exe\" -i \"%s\" -o \"%s\" --preset=\"iPad\" -v -q %s -B auto", arr[0], arr[1], arr[2]);
        Log.info(s);
        bat.write(s.getBytes("GB2312"));
        bat.close();
        int i = ProcessExecuter.exec(batFile.getAbsolutePath(), true);
        Log.info("Result:%d, Path:%s", i, batFile.getAbsoluteFile());
        if(i!=0)
        {
            File f = new File(arr[0]);
            f.renameTo(new File(f.getAbsolutePath()+".err"));
        }
        }catch(Exception e){
            Log.error(e);
            File f = new File(arr[0]);
            f.renameTo(new File(f.getAbsolutePath()+".err"));
        }

    }

    private String[] getNextFile()
    {
        for(String[] s : dirs)
        {
            File inDir = new File(s[0]);
            File outDir = new File(s[1]);
            for(File f : inDir.listFiles())
            {
                if(f.isFile())
                {
                    int dot = f.getName().lastIndexOf(".");
                    String name = f.getName().substring(0, dot);
                    String ext = f.getName().substring(dot);
                    if(!ext.equalsIgnoreCase(".mp4")||!ext.equalsIgnoreCase(".err"))
                    {
                        File outFile = new File(outDir, name+".mp4");
                        if(!outFile.exists())
                        {
                            String[] re = {f.getAbsolutePath(), outFile.getAbsolutePath(), s[2], s[3]};
                            return re;
                        }
                    }
                }
            }
        }
        return null;
    }
}
