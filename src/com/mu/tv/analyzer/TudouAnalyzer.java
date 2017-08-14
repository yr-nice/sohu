package com.mu.tv.analyzer;

import com.mu.tv.EpisodeStatus;
import com.mu.tv.ProgramStatus;
import com.mu.tv.dao.DownloadTaskDAO;
import com.mu.tv.dao.EpisodeDAO;
import com.mu.tv.dao.ProgramDAO;
import com.mu.tv.entity.DownloadTask;
import com.mu.tv.entity.Episode;
import com.mu.tv.entity.Program;
import com.mu.util.RegexUtil;
import com.mu.util.StringUtil;
import com.mu.util.WebUtil;
import com.mu.util.log.Log;
import hello.mu.util.MuLog;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Peng Mu
 */
public class TudouAnalyzer
{
    private static String progPattern = "iid:([\\d]{6,10})\\s*\\n";      //list id



    static public Program getProg(String url, String name)
    {
        return getProg(url, name, false);
    }
    static public Program getProg(String url, String name, boolean useWebName)
    {
        //check if it's in db already
        Program p = ProgramDAO.getByUrl(url);
        if(p == null)
            p = new Program(url, name);
        p.setSource("tudou");
        p.setUseWebName(Boolean.valueOf(useWebName));

        try{
        if(p.getEpisodeList()==null)
            p.setEpisodeList(new ArrayList<Episode>());
        List<Episode> epiList = p.getEpisodeList();
        String webpage = WebUtil.getWebPage(url, "gb2312");
        ArrayList<String> arr = RegexUtil.getAllUniqueMatch(webpage, progPattern);
        //if(epiList.size()<arr.size())
        //{
        Log.info(name+" Episodes ids:");
        for(String s :arr)
            Log.info("\t" + s);

        // add episodes one by one
        int n = String.valueOf(arr.size()).length();
        int i=1;
        for(String s :arr)
        {
            Episode e = null;
            if(!useWebName)
                e = getEpisode(s, p.getName()+StringUtil.padLeft(String.valueOf(i++), n, "0"), p);
            else
                e = getEpisode(s, null, p);
            //stop if can not get mp4 url
            if(e==null)
                break;
            //add only when the episode is not there
            if(!epiList.contains(e))
                epiList.add(e);
        }

        //p.setEpisodeList(epiList);
        p.setStatus(ProgramStatus.Episode_Url_Retieved);

        //}
        }catch(Exception e){Log.error(e);}
        return p;

    }


    static public Episode getEpisode(String id, String name, Program p)
    {
        Episode e = EpisodeDAO.getByWebId(id);
        if(e==null)
        {
            e = new Episode();
            e.setWebId(id);
            e.setProgram(p);
        }
        if(e.getStatus() == EpisodeStatus.Skipped)
            return e;

        // get mp4 url and name
        if(e.getTaskUrl()==null || e.getTaskUrl().isEmpty())
        {
            try{
            String s = WebUtil.getWebPage("http://v2.tudou.com/v?hd=2&it="+e.getWebId(), "GB2312");
            Log.info(s);
            String url = RegexUtil.getFirstMatchByIndex(s, ">(http:[^\\s><]+key=[^\\s><&]+)&", 1);
            e.setTaskUrl(url);
            Log.info(url);

            if(name == null || name.isEmpty())
            {
                name = RegexUtil.getFirstMatchByIndex(s, "title=\"([^\"]+)\"", 1);
                name = StringUtil.unescapeHtmlUnicodeStr(name);
                name = StringUtil.prepareFileName(name);
                Log.info(name);
            }
            e.setName(name);

            }catch(Exception x) {Log.error(x);}
        }


        List<DownloadTask> tasks = e.getTaskList();
        if(tasks==null || tasks.size()==0)
        {
            tasks = new ArrayList<DownloadTask>();
            String url = e.getTaskUrl();
            String ext = "mp4";
            if(url.indexOf("flv")!=-1)
                ext = "flv";
            else if (url.indexOf("f4v")!=-1)
                ext = "f4v";
            String taskName = e.getName()+"."+ ext;
            String dest = p.getFolder().getPath()+File.separator+taskName;
            DownloadTask t = getTask(url, taskName, dest);
            t.setEpisode(e);
            tasks.add(t);
            e.setTaskList(tasks);
            e.setStatus(EpisodeStatus.Url_Retrieved);
        }
        return e;
    }

    static public DownloadTask getTask(String url, String name, String dest)
    {
        DownloadTask d = DownloadTaskDAO.getByUrl(url);
        if(d==null)
            d = new DownloadTask(url, name, dest);
        return d;
    }

    static public void reanalyzeEpisode(Episode e)
    {
        try{
        e.setTaskUrl(null);
        e.setTaskList(null);
        e.setStatus(EpisodeStatus.Init);
        DownloadTaskDAO.removeByEpisode(e);
        EpisodeDAO.update(e);
        e = getEpisode(e.getWebId(), e.getName(), e.getProgram());
        EpisodeDAO.update(e);
        }catch(Exception x) {Log.error(x);}

    }



}
