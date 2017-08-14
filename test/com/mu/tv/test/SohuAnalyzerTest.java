package com.mu.tv.test;

import com.mu.tv.analyzer.SohuAnalyzer;
import com.mu.tv.dao.ProgramDAO;
import com.mu.tv.entity.DownloadTask;
import com.mu.tv.entity.Episode;
import com.mu.tv.entity.Program;
import com.mu.util.RegexUtil;
import com.mu.util.WebUtil;
import com.mu.util.log.Log;
import java.util.ArrayList;

/**
 *
 * @author Peng Mu
 */
public class SohuAnalyzerTest
{
    static{
        Log.loadCfg("log4j.properties", "hellomkt");
    }
    static public void main(String[] argu)
    {
        SohuAnalyzerTest ins = new SohuAnalyzerTest();
        //ins.getWebpage();
        //ins.getProg();
        //ins.deleteProg();
        ins.testGetTaskUrl();
    }


    private void getWebpage()
    {
        try{
        String webpage = WebUtil.getUnzippedWebPage("http://tv.sohu.com/20090517/n264008659.shtml", "gb2312", null);
        Log.info(webpage);
        }catch(Exception e){Log.error(e);}
    }
    private void getProg()
    {
        Program p = SohuAnalyzer.getProg("http://tv.sohu.com/20090205/n262070992.shtml", "bian_ji_bu");
        for(Episode e : p.getEpisodeList())
        {
            Log.info("Episode %s, webid %s, prog %s", e.getName(), e.getWebId(), e.getProgram().getName());
            for(DownloadTask t : e.getTaskList())
                Log.info("\tTask %s, url %s, episode name %s", t.getName(), t.getUrl(), t.getEpisode().getName());
        }
        ProgramDAO.update(p);
    }

    private void testGetTaskUrl()
    {
        try{
        String re = WebUtil.getWebPage("http://hot.vrs.sohu.com/vrs_flash.action?ver=21&vid=390286", "GB2312");
        //get ip
        String ip = RegexUtil.getFirstMatchByIndex(re, "\"allot\":\"([^\"\\s]+?)\"", 1);
        //get all old url
        ArrayList<String> l = RegexUtil.getAllUniqueMatch(re, "\"http://data.vod.itc.cn([^\"\\s]+?mp4|flv)\"");
        //get all new url
        String n = RegexUtil.getFirstMatchByIndex(re, "\"su\":\\[(.+)\"\\]", 1);
        n= n.replaceAll("\"", "");
        String[] newURL = n.split(",");
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<l.size(); i++)
            sb.append("http://"+ip+"/?file="+l.get(i) + "&new="+newURL[i]+"\n");

        Log.info(sb.toString());
        //concat to actual url
        }catch(Exception x) {Log.error(x);}
    }
    private void deleteProg()
    {
        ProgramDAO.deleteByUrl("http://tv.sohu.com/20090205/n262070992.shtml");
    }


}
