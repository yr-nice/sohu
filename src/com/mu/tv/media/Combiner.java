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
public class Combiner
{
    static private ThreadPoolExecutor exec = (ThreadPoolExecutor)Executors.newFixedThreadPool(1);

    public void combine(Episode e)
    {
        exec.execute(new CombineJob(e));
    }


    static private void combineEpisode(Episode e)
    {
        try{
        List<DownloadTask> list = e.getTaskList();
        if(list.size()==0)
            return;
        /*for(DownloadTask t : list)
            if( t.getStatus()!=TaskStatus.Finished)
                return;
        */

        e.setStatus(EpisodeStatus.Finished);
        File progFolder = (new File(list.get(0).getDest())).getParentFile();
        File outMp4 = new File(progFolder.getAbsolutePath()+File.separator+e.getName()+".mp4");
        File batFile = new File(progFolder, e.getName().replaceAll("\\s", "_")+".bat");
        if(!outMp4.exists())
        {
            FileOutputStream bat = new FileOutputStream(batFile);
            StringBuilder sp = new StringBuilder((new File("mp4tools\\mp4box")).getAbsolutePath() +" ");
            for(DownloadTask t : list)
                sp.append("-cat \""+(new File(t.getDest())).getAbsolutePath()+"\" ");
            sp.append("\""+outMp4.getAbsolutePath()+"\" ");
            bat.write(sp.toString().getBytes("GB2312"));
            bat.close();

            Log.info(batFile.getAbsoluteFile());
            ProcessExecuter.exec("\""+batFile.getAbsolutePath()+"\"", true);
        }
        if(outMp4.exists())
        {
            File processed = new File(progFolder, "processed");
            if(!processed.exists()) processed.mkdirs();

            batFile.renameTo(new File(processed, batFile.getName()));

            for(DownloadTask t : list)
            {
                File f = new File(t.getDest());
                f.renameTo(new File(processed, f.getName()));
            }
            e.setStatus(EpisodeStatus.Finished);
        }
        }catch(Exception x)
        {
            Log.error(x);
        }
    }


    class CombineJob implements Runnable
    {
        Episode e;
        public CombineJob(Episode e)
        {
            this.e = e;
        }
        public void run()
        {
            combineEpisode(e);
        }
    }

}
