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
public class CCTVAnalyzerTest
{
    static{
        Log.loadCfg("log4j.properties", "hellomkt");
    }
    static public void main(String[] argu)
    {
        CCTVAnalyzerTest ins = new CCTVAnalyzerTest();
        //ins.getWebpage();
        //ins.getProg();
        //ins.deleteProg();
        //ins.testGetEpiUrl();
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

    private void testGetEpiUrl()
    {
        try{
        //String re = WebUtil.getWebPage("http://dianshiju.cntv.cn/revolution/51haobingzhan/videopage/index.shtml", "GB2312");
        String re = WebUtil.getWebPage("http://dianshiju.cntv.cn/wanghaitaojinnian41/videopage/index.shtml", "GB2312");
        //Log.info(re);
        ArrayList<String> l = RegexUtil.getAllUniqueMatch(re, "\\]\\s*=\\s*new[^\\n]+'([^'\\s]+?.shtml)'");
        for(String s : l)
            Log.info(s);


        //concat to actual url
        }catch(Exception x) {Log.error(x);}
    }

    private void testGetTaskUrl()
    {
        try{
        String re = WebUtil.getWebPage("http://food.cntv.cn/tiantianyinshi/classpage/video/20110210/100182.shtml", "GB2312");
        //get pid
        String pid = RegexUtil.getFirstMatchByIndex(re, "\"videoCenterId\",\\s*\"([^\"\\s]+?)\"", 1);
        Log.info(pid);
        //get name
        String name = RegexUtil.getFirstMatchByIndex(re, "var\\s+c_title\\s*=\\s*\"([^\"]+?)\"", 1);
        Log.info(name);
        //get mp4 urls
        re = WebUtil.getWebPage("http://vdd.player.cntv.cn/index.php?pid="+pid, "GB2312");
        re = re.replaceAll("\\\\/", "/");
        Log.info(re);
        String[] arr = re.split("chapters");
        re = arr[arr.length-1];
        arr = re.split("\\]");
        re = arr[0];
        Log.info(re);
        ArrayList<String> urls = RegexUtil.getAllUniqueMatch(re, "\"(http[^\"\\s;]+?mp4)\"");
        for(String t : urls)
            Log.info(t);


        /*//get all old url
        ArrayList<String> l = RegexUtil.getAllUniqueMatch(re, "\"http://data.vod.itc.cn([^\"\\s]+?mp4|flv)\"");
        //get all new url
        String n = RegexUtil.getFirstMatchByIndex(re, "\"su\":\\[(.+)\"\\]", 1);
        n= n.replaceAll("\"", "");
        String[] newURL = n.split(",");
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<l.size(); i++)
            sb.append("http://"+ip+"/?file="+l.get(i) + "&new="+newURL[i]+"\n");

        Log.info(sb.toString());*/
        //concat to actual url
        }catch(Exception x) {Log.error(x);}
    }
    private void deleteProg()
    {
        ProgramDAO.deleteByUrl("http://tv.sohu.com/20090205/n262070992.shtml");
    }


}
