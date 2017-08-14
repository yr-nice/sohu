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
public class CCTVAnalyzer
{
    private static String progPattern = "\\]\\s*=\\s*new[^\\n]+'([^'\\s]+?.shtml)'";      //list id



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
        p.setSource("cctv");
        p.setUseWebName(Boolean.valueOf(useWebName));

        try{
        if(p.getEpisodeList()==null)
            p.setEpisodeList(new ArrayList<Episode>());
        List<Episode> epiList = p.getEpisodeList();
        String webpage = WebUtil.getWebPage(url, "gb2312");
        ArrayList<String> arr = RegexUtil.getAllUniqueMatch(webpage, progPattern);
        //if(epiList.size()<arr.size())
        //{
        Log.info(name+" Episodes URLs:");
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
                continue;
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


    static public Episode getEpisode(String webpageUrl, String name, Program p)
    {
        Episode e = EpisodeDAO.getByWebPageURL(webpageUrl);
        if(e==null)
        {
            e = new Episode();
            e.setWebpageUrl(webpageUrl);
            e.setProgram(p);
        }
        if(e.getStatus() == EpisodeStatus.Skipped)
            return e;
       //get web id and name
        if(e.getWebId()==null || e.getWebId().isEmpty())
        {
            try{
            String page = WebUtil.getWebPage(webpageUrl, "GB2312");
            //get pid
            String pid = RegexUtil.getFirstMatchByIndex(page, "\"videoCenterId\",\\s*\"([^\"\\s]+?)\"", 1);
            Log.info(pid);
            e.setWebId(pid);
            //get name
            if(name == null || name.isEmpty())
            {
                name = RegexUtil.getFirstMatchByIndex(page, "var\\s+c_title\\s*=\\s*\"([^\"]+?)\"", 1);
                Log.info(name);
            }
            name = StringUtil.prepareFileName(name);
            e.setName(name);
            }catch(Exception x) {Log.error(x);}
        }

        // get mp4 url
        if(e.getTaskUrl()==null || e.getTaskUrl().isEmpty())
        {
            try{
            String s = WebUtil.getWebPage("http://vdd.player.cntv.cn/index.php?pid="+e.getWebId(), "GB2312");
            s = s.replaceAll("\\\\/", "/");
            Log.info(s);
            if(s.indexOf("chapters")==-1)
                return null;
            String[] arr = s.split("chapters");
            s = arr[arr.length-1];
            arr = s.split("\\]");
            s = arr[0];
            Log.info(s);
            ArrayList<String> urls = RegexUtil.getAllUniqueMatch(s, "\"(http[^\"\\s;]+?mp4)\"");
            for(String t : urls)
                Log.info(t);
            StringBuilder sb = new StringBuilder();
            for(int i=0; i<urls.size(); i++)
                sb.append(urls.get(i)+"\n");
            e.setTaskUrl(sb.toString());
            }catch(Exception x) {Log.error(x);}
        }


        List<DownloadTask> tasks = e.getTaskList();
        if(tasks==null || tasks.size()==0)
        {
            tasks = new ArrayList<DownloadTask>();
            String[] arr = e.getTaskUrl().split("\n");
            int i=0;
            for(String url : arr)
            {
                i++;
                String taskName = e.getName()+"-"+i+StringUtil.getFileType(url);
                String dest = p.getFolder().getPath()+File.separator+taskName;
                DownloadTask t = getTask(url, taskName, dest);
                t.setEpisode(e);
                tasks.add(t);
            }
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
        e.setWebId(null);
        e.setTaskUrl(null);
        e.setTaskList(null);
        e.setStatus(EpisodeStatus.Init);
        DownloadTaskDAO.removeByEpisode(e);
        EpisodeDAO.update(e);
        e = getEpisode(e.getWebpageUrl(), e.getName(), e.getProgram());
        EpisodeDAO.update(e);
        }catch(Exception x) {Log.error(x);}

    }



}
