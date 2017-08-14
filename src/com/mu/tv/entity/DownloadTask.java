package com.mu.tv.entity;

import com.mu.tv.EpisodeStatus;
import com.mu.tv.dao.DownloadTaskDAO;
import com.mu.tv.download.TaskStatus;
import com.mu.tv.media.Checker;
import com.mu.util.log.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Observable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author Peng Mu
 */
@Entity
@Table(name = "DOWNLOAD_TASK")
@NamedQueries(
{
    @NamedQuery(name = "DownloadTask.findAll", query = "SELECT d FROM DownloadTask d")
})
public class DownloadTask  extends Observable implements Serializable, Runnable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "url", length = 255, nullable = false)
    private String url;
    @Column(name = "dest", length = 255)
    private String dest;
    @Column(name = "name", length = 255)
    private String name;
    @Column(name = "resumable")
    private boolean resumable=false;
    @Column(name = "total")
    private long total=0L;
    @Column(name = "downloaded")
    private long downloaded=0L;
    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    @Column(name = "finish_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishTime;
    @Column(name = "last_update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;
    @Column(name = "status", length = 50)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "episode_id")
    private Episode episode;

    @Transient
    private Date enqueueTime;
    @Transient
    private Date resumeTime;
    @Transient
    private Date lastScanDate;
    @Transient
    private long lastScanSize = 0;
    @Transient
    private long lastStartSize = 0;
    @Transient
    private int curSpeed = 0;
    @Transient
    private int averSpeed = 0;
    @Transient
    private HttpURLConnection hu;

    @Transient
    public boolean canStart()
    {
        if(status==TaskStatus.Suspended || status==TaskStatus.Failed || status==TaskStatus.File_Corrupt)
            return true;
        return false;
    }
    @Transient
    public boolean isFinished()
    {
        
        File destFile = new File(dest);
        File processed = new File(destFile.getParent()+File.separator+"processed", destFile.getName());
        /*if(total<=0)
            return false;*/
        if(processed.exists() || (destFile.exists() && Checker.check(destFile.getAbsolutePath()) && destFile.length()==total))
        {
            if(status != TaskStatus.Finished)
            {
                setStatus(TaskStatus.Finished);
                DownloadTaskDAO.update(this);

            }
            return true;
        }
        return false;
    }

    @Transient
    public boolean canStop()
    {
        if(status!=TaskStatus.Finished && status!=TaskStatus.Removed && status != TaskStatus.Failed && status!=TaskStatus.File_Corrupt)
            return true;
        return false;
    }


    public DownloadTask()
    {
    }

    public DownloadTask(Long id)
    {
        this.id = id;
    }

    public DownloadTask(String url, String name, String dest)
    {
        this.url = url;
        this.name = name;
        this.dest = dest;
        status = TaskStatus.Suspended;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getDest()
    {
        return dest;
    }

    public void setDest(String dest)
    {
        this.dest = dest;
    }


    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getFinishTime()
    {
        return finishTime;
    }

    public void setFinishTime(Date finishTime)
    {
        this.finishTime = finishTime;
    }

    public Date getLastUpdate()
    {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate)
    {
        this.lastUpdate = lastUpdate;
    }

    public Episode getEpisode()
    {
        return episode;
    }

    public void setEpisode(Episode episode)
    {
        this.episode = episode;
    }


    public int getAverSpeed()
    {
        return averSpeed;
    }

    public void setAverSpeed(int averSpeed)
    {
        this.averSpeed = averSpeed;
        notifyObservers();

    }

    public int getCurSpeed()
    {
        return curSpeed;
    }

    public void setCurSpeed(int curSpeed)
    {
        this.curSpeed = curSpeed;
        notifyObservers();
    }

    public long getDownloaded()
    {
        return downloaded;
    }

    public void setDownloaded(long downloaded)
    {
        this.downloaded = downloaded;
        notifyObservers();
    }

    public Date getEnqueueTime()
    {
        return enqueueTime;
    }

    public void setEnqueueTime(Date enqueueTime)
    {
        this.enqueueTime = enqueueTime;
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

    public boolean getResumable()
    {
        return isResumable();
    }
    public boolean isResumable()
    {
        return resumable;
    }

    public void setResumable(boolean resumable)
    {
        this.resumable = resumable;
    }

    public Date getResumeTime()
    {
        return resumeTime;
    }

    public void setResumeTime(Date resumeTime)
    {
        this.resumeTime = resumeTime;
    }

    public TaskStatus getStatus()
    {
        return status;
    }

    public void setStatus(TaskStatus status)
    {
        this.status = status;
        notifyObservers();
    }

    public long getTotal()
    {
        return total;
    }

    public void setTotal(long total)
    {
        this.total = total;
        notifyObservers();
    }


    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DownloadTask))
        {
            return false;
        }
        DownloadTask other = (DownloadTask) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    private void initMegaInfo()
    {
        try{
        status = TaskStatus.Initing_Task;
        notifyObservers();
        URL u = new URL(url);
        HttpURLConnection h = (HttpURLConnection) u.openConnection();
        String source = episode.getProgram().getSource();
        if(source.equals("sohu"))
            h.setRequestProperty("Referer", "http://tv.sohu.com/upload/swf/20091230/Player.swf");
        //else if(source.equals("tudou"))
        //    hu.setRequestProperty("User-Agent", "Mozilla/5.0 (iPad; U; CPU OS 4_2_1 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8C148 Safari/6533.18.5");
        h.setRequestProperty("RANGE", "bytes=0-");
        Log.info(h.getContentLength());
        Log.info(h.getResponseCode());
        total = h.getContentLength();
        resumable = h.getResponseCode()==206? true:false;
        h.disconnect();
        }catch(Exception e){Log.error(e);}
    }

    public static void main(String[] arg)
    {
        try{
        URL u = new URL("http://220.181.61.229/?file=/tv/20090201/3517NH_001.mp4&new=/175/123/nst3RZB14hpdLg3v6dFy8.mp4");
        HttpURLConnection h = (HttpURLConnection) u.openConnection();
        h.setRequestProperty("Referer", "http://tv.sohu.com/upload/swf/20091230/Player.swf");
        h.setRequestProperty("RANGE", "bytes=0-");
        Log.info(h.getContentLength());
        Log.info(h.getResponseCode());
        h.disconnect();
        }catch(Exception e){Log.error(e);}
    }


    private void initDownloading()throws Exception
    {
        File destFile = new File(dest);
        if(!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();
        dest = destFile.getAbsolutePath();
        if(!resumable)
        {
            destFile.delete();
            downloaded=0L;
        }
        else
            downloaded=destFile.length();

        URL u = new URL(url);
        hu = (HttpURLConnection) u.openConnection();
        String source = episode.getProgram().getSource();
        if(source.equals("sohu"))
            hu.setRequestProperty("Referer", "http://tv.sohu.com/upload/swf/20091230/Player.swf");
        //else if(source.equals("tudou"))
        //    hu.setRequestProperty("User-Agent", "Mozilla/5.0 (iPad; U; CPU OS 4_2_1 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8C148 Safari/6533.18.5");

        hu.setRequestProperty("RANGE", "bytes="+downloaded+"-");

        //total = hu.getContentLength();
        resumeTime = new Date();
        lastScanDate = new Date();
        status = TaskStatus.Start_Downloading;
        notifyObservers();
        DownloadTaskDAO.update(this);

    }

    public void run()
    {
        Log.info("Start downloading %s, id:%d, url:%s", name, id, url);
        initMegaInfo();

        if(!isFinished())
        {

            InputStream is = null;
            FileOutputStream fo = null;

            try{
            initDownloading();
            episode.setStatus(EpisodeStatus.Downloading);
            is = hu.getInputStream();
            fo = new FileOutputStream(dest, true);

            byte[] buff = new byte[10240];

            while(!Thread.interrupted())
            {
                updateSpeed();
                int i = is.read(buff);
                if(i == -1)
                {
                    if(downloaded == total)
                        status = TaskStatus.Finished;
                    else
                        status = TaskStatus.Failed;
                    break;
                }
                fo.write(buff, 0, i);
                downloaded+=i;
            }

            fo.close();
            is.close();

            }catch(InterruptedException e)
            {
                try{
                if(fo != null) fo.close();
                if(is != null) is.close();
                status =TaskStatus.Suspended;
                }catch(Exception x){Log.error(x);}
            }
            catch(Exception e){
                status=TaskStatus.Failed;
                Log.error(e);
            }
        }
        hu.disconnect();
        updateSpeed();
        DownloadTaskDAO.update(this);
        notifyObservers();
        episode.combineEpi();

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
        
        //Log.info(this);
        long retrieved = downloaded - this.lastScanSize;
        curSpeed = Integer.parseInt(retrieved*1000/1024/interval+"");

        interval = System.currentTimeMillis() - this.resumeTime.getTime();
        retrieved = downloaded - this.lastStartSize;
        averSpeed = Integer.parseInt(retrieved*1000/1024/interval+"");

        lastScanSize = downloaded;
        lastScanDate = new Date();
        notifyObservers();

    }

    public String getPercentage()
    {
        return String.valueOf(total>0? downloaded*100/total : 0);
    }

    public String toString()
    {
        return name;
       
    }

    public void notifyObservers()
    {
        setChanged();
        super.notifyObservers();
    }

}
