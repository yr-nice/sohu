package com.mu.tv.download;

import com.mu.tv.entity.DownloadTask;
import com.mu.util.log.Log;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Peng Mu
 */
public class TaskMgr
{
    //private ThreadPoolExecutor exec = (ThreadPoolExecutor)Executors.newFixedThreadPool(1);
    private ResizableThreadPool exec = new ResizableThreadPool();

    public void startAfterCurOne(DownloadTask t)
    {
        if(t.isFinished())
            return;
        stop(t);
        t.setStatus(TaskStatus.Waiting_for_Start);
        exec.addFirst(t);
    }
    public void startAfterCurOne(List<DownloadTask> l)
    {
        for(int i=l.size()-1; i>=0; i--)
            startAfterCurOne(l.get(i));
    }
    public void start(DownloadTask t)
    {
        if(t.isFinished() || exec.contain(t))
            return;
        t.setStatus(TaskStatus.Waiting_for_Start);
        exec.add(t);
    }

    public void start(List<DownloadTask> l)
    {
        for(DownloadTask t : l)
            start(t);

    }

    public void stop(DownloadTask t)
    {
        if(!exec.contain(t))
            return;
        exec.remove(t);
        t.setStatus(TaskStatus.Suspended);

    }

    public void stop(List<DownloadTask> l)
    {
        for(DownloadTask t : l)
            stop(t);
    }

    public void reStart(DownloadTask t)
    {
        stop(t);
        (new File(t.getDest())).delete();
        start(t);
    }

    public void reStart(List<DownloadTask> l)
    {
        for(DownloadTask t : l)
            reStart(t);
    }

    public void moreThread()
    {
        Log.info("Before More, max:%s, core:%s, cur:%s, keepAlive:%d",  exec.getMaximumPoolSize(), exec.getCorePoolSize(), exec.getPoolSize(), exec.getKeepAliveTime(TimeUnit.NANOSECONDS));
        exec.resizeTo(exec.getCorePoolSize() + 1);
        Log.info("After More, max:%s, core:%s, cur:%s, keepAlive:%d",  exec.getMaximumPoolSize(), exec.getCorePoolSize(), exec.getPoolSize(), exec.getKeepAliveTime(TimeUnit.NANOSECONDS));
    }
    public void lessThread()
    {
        Log.info("Before Less, max:%s, core:%s, cur:%s, keepAlive:%d",  exec.getMaximumPoolSize(), exec.getCorePoolSize(), exec.getPoolSize(), exec.getKeepAliveTime(TimeUnit.NANOSECONDS));
        exec.resizeTo(exec.getCorePoolSize() - 1);
        Log.info("After Less, max:%s, core:%s, cur:%s, keepAlive:%d",  exec.getMaximumPoolSize(), exec.getCorePoolSize(), exec.getPoolSize(), exec.getKeepAliveTime(TimeUnit.NANOSECONDS));
    }

}
