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
public class SohuAnalyzer
{
    private static String progPattern = "var vid *= *\"([0-9]+)\".*" +    //epi id
                                "var pid *= *\"([0-9]+)\".*"      +        //program id
                                "var vrs_playlist_id *= *\"([0-9]*)\"";      //list id

    private static String episodePattern="\"videoId\":([0-9]+).+?" +          // episode id
                                    //"\"videoName\":\"(.+?)\"";            // title
                                    "\"videoName\":\"(.+?)\"";              //title


    static public Program getProg(String url, String name)
    {
        return getProg(url, name, false);
    }
    static public Program getProg(String url, String name, boolean useWebName)
    {
        Program p = ProgramDAO.getByUrl(url);
        if(p == null)
            p = new Program(url, name);
        p.setSource("sohu");
        p.setUseWebName(useWebName);

        try{

        if(p.getEpisodeList()==null)
            p.setEpisodeList(new ArrayList<Episode>());
        List<Episode> epiList = p.getEpisodeList();

        //get programe web id
        String webpage = WebUtil.getUnzippedWebPage(url, "gb2312", null);
        ArrayList<String> arr = RegexUtil.getAllUniqueMatch(webpage, progPattern);
        if(arr.size() != 3)
        {
            Log.error("Failed to retrieve Program Info!");
            Log.info(arr);
            return p;
        }
        String epiId = arr.get(0);
        p.setWebId(arr.get(1));
        if(!arr.get(2).trim().isEmpty())
            p.setWebId(arr.get(2));

        // retrieve episodes ids
        MuLog.log(String.format("Progam %s, programId=%s, epiId=%s", name, p.getWebId(), epiId));
        String epiStr = WebUtil.getWebPage(String.format("http://hot.vrs.sohu.com/vrs_videolist.action?playlist_id=%s", p.getWebId()), "GB2312");
        ArrayList<String[]> ea = RegexUtil.getAllMatchArr(epiStr, episodePattern);
        if(ea==null || ea.size()==0)
        {
            epiStr = WebUtil.getWebPage(String.format("http://hot.vrs.sohu.com/vrs_videolist.action?vid=%s&pid=%s", epiId, p.getWebId()), "GB2312");
            //Log.log(epiStr);
            ea = RegexUtil.getAllMatchArr(epiStr, episodePattern);
        }
        //update episodes
        //if(epiList.size()!=ea.size())
        //{
            //Log.logCollection(arr);
        //ArrayList<SohuEpisode> seArr = new ArrayList<SohuEpisode>();
        int n = String.valueOf(ea.size()).length();
        for(int i=0; i<ea.size(); i++)
        {
            String epiName = ea.get(i)[1];
            if(!useWebName)
                epiName = p.getName()+StringUtil.padLeft(String.valueOf(i+1), n, "0");
            Episode e = getEpisode(ea.get(i)[0], epiName, p);
            if(!epiList.contains(e))
                epiList.add(e);
        }
        //p.setEpisodeList(epiList);
        p.setStatus(ProgramStatus.Episode_Url_Retieved);

        //}
        }catch(Exception e){Log.error(e);}
        return p;

    }

    static public void reanalyzeEpisode(Episode e)
    {
        /*try{
        //reformat name
        e.setName(StringUtil.prepareFileName(e.getName()));
        //get episod info json
        String re = WebUtil.getWebPage("http://hot.vrs.sohu.com/vrs_flash.action?ver=21&vid="+e.getWebId(), "GB2312");
        //get ip
        String ip = RegexUtil.getFirstMatchByIndex(re, "\"allot\":\"([^\"\\s]+?)\"", 1);
        //get all old url
        ArrayList<String> l = RegexUtil.getAllUniqueMatch(re, "\"http://data.vod.itc.cn([^\"\\s]+?mp4|flv)\"");
        //get all new url
        String n = RegexUtil.getFirstMatchByIndex(re, "\"su\":\\[(.+)\"\\]", 1);
        n= n.replaceAll("\"", "");
        String[] newURL = n.split(",");
        //concat to actual url
        Log.info(l);
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<l.size(); i++)
            sb.append("http://"+ip+"/?file="+l.get(i) + "&new="+newURL[i]+"\n");
        e.setTaskUrl(sb.toString());
        }catch(Exception x) {Log.error(x);}

        DownloadTaskDAO.removeByEpisode(e);
        List<DownloadTask> tasks = e.getTaskList();
        tasks.clear();
        //tasks = new ArrayList<DownloadTask>();
        String[] arr = e.getTaskUrl().split("\n");
        int i=0;
        for(String url : arr)
        {
            i++;
            String taskName = e.getName()+"-"+i+StringUtil.getFileType(url);
            String dest = e.getProgram().getFolder().getPath()+File.separator+taskName;
            DownloadTask t = getTask(url, taskName, dest);
            t.setEpisode(e);
            tasks.add(t);
        }
        //e.setTaskList(tasks);
        e.setStatus(EpisodeStatus.Url_Retrieved);*/
        try{
        e.setStatus(EpisodeStatus.Init);
        e.setTaskUrl(null);
        e.setTaskList(null);
        DownloadTaskDAO.removeByEpisode(e);
        EpisodeDAO.update(e);
        e = getEpisode(e.getWebId(), e.getName(), e.getProgram());
        EpisodeDAO.update(e);
        }catch(Exception x) {Log.error(x);}

    }

    static public Episode getEpisode(String webId, String name, Program p)
    {
        name = StringUtil.prepareFileName(name);
        Episode e = EpisodeDAO.getByWebId(webId);
        if(e==null)
            e = new Episode(webId, name);
        if(e.getStatus() == EpisodeStatus.Skipped)
            return e;
        e.setProgram(p);
        if(e.getTaskUrl()==null || e.getTaskUrl().isEmpty())
        {
            try{
            //get episod info json
            String re = WebUtil.getWebPage("http://hot.vrs.sohu.com/vrs_flash.action?ver=21&vid="+webId, "GB2312");
            //get ip
            String ip = RegexUtil.getFirstMatchByIndex(re, "\"allot\":\"([^\"\\s]+?)\"", 1);
            //get all old url
            ArrayList<String> l = RegexUtil.getAllUniqueMatch(re, "\"http://data.vod.itc.cn([^\"\\s]+?mp4|flv)\"");
            //get all new url
            String n = RegexUtil.getFirstMatchByIndex(re, "\"su\":\\[(.+)\"\\]", 1);
            n= n.replaceAll("\"", "");
            String[] newURL = n.split(",");
            //concat to actual url
            Log.info(l);
            StringBuilder sb = new StringBuilder();
            for(int i=0; i<l.size(); i++)
                sb.append("http://"+ip+"/?file="+l.get(i) + "&new="+newURL[i]+"\n");
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




}
