package com.mu.tv.media;

import com.mu.tv.EpisodeStatus;
import com.mu.tv.download.TaskStatus;
import com.mu.tv.entity.DownloadTask;
import com.mu.tv.entity.Episode;
import com.mu.util.log.Log;
import hello.mu.util.ProcessExecuter;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author Peng Mu
 */
public class Checker
{
    public static boolean check(String path)
    {
        //return true if it's not mp4 format
        if(!path.toLowerCase().trim().endsWith("mp4"))
            return true;
        //check mp4
        int i = -1;
        try{
        File batFile = File.createTempFile("checker", ".bat");
        FileOutputStream bat = new FileOutputStream(batFile);
        String s = String.format("%s -info \"%s\"", (new File("mp4tools\\mp4box")).getAbsolutePath(), (new File(path)).getAbsolutePath());
        bat.write(s.getBytes("GB2312"));
        bat.close();
        i = ProcessExecuter.exec(batFile.getAbsolutePath(), true);
        Log.info("Result:%d, Path:%s", i, batFile.getAbsoluteFile());
        }catch(Exception e){Log.error(e);}
        if(i!=0)
            return false;
        else
            return true;
    }

    public static void main(String[] argu)
    {
        Checker.check("C:\\temp\\AB.mp4");
        Checker.check("C:\\temp\\S1_01-1.mp4");
    }
}
