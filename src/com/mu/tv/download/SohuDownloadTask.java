/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mu.tv.download;

import com.mu.util.log.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Observable;

/**
 *
 * @author Peng mu
 */
public class SohuDownloadTask extends Observable implements Runnable
{
    private Long id;
    private String url = "";
    private File dest;
    private String name = "";

    private int curSpeed = 0;
    private int averSpeed = 0;
    private long size = 0;
    private long downloaded = 0;
    private long lastScanSize = 0;
    private long lastStartSize = 0;

    private Date enqueueTime;
    private Date createTime = new Date();
    private Date resumeTime;
    private Date finishTime;
    private Date lastScanDate = new Date();
    private TaskStatus status = TaskStatus.Suspended;
    private HashMap<String, String> httpHeader = new HashMap<String, String> ();

    public SohuDownloadTask(String name, String url, String dest)
    {
        this.url = url;
        this.name = name;
        this.dest = new File(dest);
    }

    private void init()throws Exception
    {
        httpHeader.put("Referer", "http://tv.sohu.com/upload/swf/20091230/Player.swf");
        if(!dest.getParentFile().exists())
            dest.getParentFile().mkdirs();


        status = TaskStatus.Initing_Task;
        size = getTotalSize();
        downloaded = dest.length();
        lastStartSize = downloaded;
        resumeTime = new Date();

        if(downloaded == size)
            status = TaskStatus.Finished;
        else if(downloaded > size)
            status = TaskStatus.File_Corrupt;
        else
            status = TaskStatus.Start_Downloading;

    }

    private InputStream getHttpInputStream() throws Exception
    {
        URL u = new URL(url);
        HttpURLConnection hu = (HttpURLConnection) u.openConnection();
        for(String k : httpHeader.keySet())
            hu.setRequestProperty(k, httpHeader.get(k));
        hu.setRequestProperty("RANGE","bytes="+downloaded+"-");
        return hu.getInputStream();
    }

    private long getTotalSize() throws Exception
    {
        if(size >0)
            return size;

        URL u = new URL(url);
        HttpURLConnection hu = (HttpURLConnection) u.openConnection();
        for(String k : httpHeader.keySet())
            hu.setRequestProperty(k, httpHeader.get(k));
        return hu.getContentLength();
    }


    public void run()
    {
        Log.info("Start downloading %s, id:%d, url:%s", name, id, url);
        InputStream is = null;
        FileOutputStream fo = null;
        try{
        init();
        if(status != TaskStatus.Start_Downloading)
            return;

        is = getHttpInputStream();
        fo = new FileOutputStream(dest, true);
        byte[] buff = new byte[10240];

        int retryCount = 0;
        while(status == TaskStatus.Start_Downloading)
        {
            updateSpeed();
            int i = is.read(buff);
            if(i == -1)
            {
                if(downloaded == size)
                    status = TaskStatus.Finished;
                else if(retryCount>=5)
                {
                    status = TaskStatus.Failed;
                    break;
                }
                else
                {
                    Log.log("Retry "+(retryCount+1)+"...");
                    is.close();
                    Thread.sleep(3*1000);
                    is = getHttpInputStream();
                    retryCount++;
                    continue;
                }
            }
            fo.write(buff, 0, i);
            downloaded+=i;
        }

        fo.close();
        is.close();

        }catch(InterruptedException e)
        {
            try{
            if(fo != null)
                fo.close();
            if(is != null)
                is.close();
            status =TaskStatus.Suspended;
            }catch(Exception x){Log.error(x);}

        }
        catch(Exception e){
            status=TaskStatus.Failed;
            Log.error(e);
        }
        updateSpeed();
        //DownloadTaskDAO.update((CCTVDownloadTask)t);

    }

    private void updateSpeed()
    {
        if(status!=TaskStatus.Start_Downloading)
        {
            curSpeed =0;
            return;
        }
        long interval = System.currentTimeMillis() - lastScanDate.getTime();
        if(interval<2*1000)
            return;
        long retrieved = downloaded - this.lastScanSize;
        curSpeed = Integer.parseInt(retrieved*1000/1024/interval+"");

        interval = System.currentTimeMillis() - this.resumeTime.getTime();
        retrieved = downloaded - this.lastStartSize;
        averSpeed = Integer.parseInt(retrieved*1000/1024/interval+"");

        lastScanSize = downloaded;
        lastScanDate = new Date();

    }

    public int getAverSpeed()
    {
        return averSpeed;
    }

    public void setAverSpeed(int averSpeed)
    {
        this.averSpeed = averSpeed;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public int getCurSpeed()
    {
        return curSpeed;
    }

    public void setCurSpeed(int curSpeed)
    {
        this.curSpeed = curSpeed;
    }


    public long getDownloaded()
    {
        return downloaded;
    }

    public void setDownloaded(long downloaded)
    {
        this.downloaded = downloaded;
    }

    public Date getEnqueueTime()
    {
        return enqueueTime;
    }

    public void setEnqueueTime(Date enqueueTime)
    {
        this.enqueueTime = enqueueTime;
    }

    public Date getFinishTime()
    {
        return finishTime;
    }

    public void setFinishTime(Date finishTime)
    {
        this.finishTime = finishTime;
    }

    public HashMap<String, String> getHttpHeader()
    {
        return httpHeader;
    }

    public void setHttpHeader(HashMap<String, String> httpHeader)
    {
        this.httpHeader = httpHeader;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Date getLastScanDate()
    {
        return lastScanDate;
    }

    public void setLastScanDate(Date lastScanDate)
    {
        this.lastScanDate = lastScanDate;
    }

    public long getLastScanSize()
    {
        return lastScanSize;
    }

    public void setLastScanSize(long lastScanSize)
    {
        this.lastScanSize = lastScanSize;
    }

    public long getLastStartSize()
    {
        return lastStartSize;
    }

    public void setLastStartSize(long lastStartSize)
    {
        this.lastStartSize = lastStartSize;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }


    public Date getResumeTime()
    {
        return resumeTime;
    }

    public void setResumeTime(Date resumeTime)
    {
        this.resumeTime = resumeTime;
    }

    public long getSize()
    {
        return size;
    }

    public void setSize(long size)
    {
        this.size = size;
    }

    public TaskStatus getStatus()
    {
        return status;
    }

    public void setStatus(TaskStatus status)
    {
        this.status = status;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public boolean canStart()
    {
        if(status==TaskStatus.Failed||status==TaskStatus.Suspended)
            return true;
        return false;
    }
    
    public boolean canStop()
    {
        if(status!=TaskStatus.Finished && status!=TaskStatus.Removed)
            return true;
        return false;
    }
}
