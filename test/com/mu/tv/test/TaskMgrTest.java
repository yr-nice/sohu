package com.mu.tv.test;

import com.mu.tv.dao.DownloadTaskDAO;
import com.mu.tv.download.TaskMgr;
import com.mu.tv.entity.DownloadTask;
import com.mu.util.log.Log;

/**
 *
 * @author Peng Mu
 */
public class TaskMgrTest
{
    static{
        Log.loadCfg("log4j.properties", "hellomkt");
    }
    static public void main(String[] argu)
    {
        TaskMgrTest ins = new TaskMgrTest();
        ins.testDownload();
    }

    private void testDownload()
    {
        DownloadTask t = DownloadTaskDAO.getByUrl("http://data.vod.itc.cn/tv/20090205/4761NH_001.mp4");
        TaskMgr mgr = new TaskMgr();
        mgr.start(t);
        t = DownloadTaskDAO.getByUrl("http://data.vod.itc.cn/tv/20090205/4761NH_002.mp4");
        mgr.start(t);
        t = DownloadTaskDAO.getByUrl("http://data.vod.itc.cn/tv/20090205/4761NH_003.mp4");
        mgr.start(t);
        t = DownloadTaskDAO.getByUrl("http://data.vod.itc.cn/tv/20090205/4761NH_004.mp4");
        mgr.start(t);
        Log.info(">>>>>>>>>> Added all Tasks");
    }


}
