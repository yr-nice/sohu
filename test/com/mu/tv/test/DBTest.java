package com.mu.tv.test;

import com.mu.tv.dao.BaseDao;
import com.mu.tv.entity.DownloadTask;
import com.mu.tv.entity.Episode;
import com.mu.tv.entity.Program;
import com.mu.util.log.Log;

/**
 *
 * @author Peng Mu
 */
public class DBTest
{
    static{
        Log.loadCfg("log4j.properties", "hellomkt");
    }
    static public void main(String[] argu)
    {
        DBTest ins = new DBTest();
        ins.init();
    }

    private void init()
    {
        BaseDao.getById(Program.class, 1L);
        BaseDao.getById(Episode.class, 1L);
        BaseDao.getById(DownloadTask.class, 1L);
    }


}
