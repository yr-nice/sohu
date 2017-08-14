package com.mu.tv.entity;

import com.mu.tv.EpisodeStatus;
import com.mu.tv.download.TaskStatus;
import com.mu.tv.media.Checker;
import com.mu.tv.media.Combiner;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author Peng Mu
 */
@Entity
@Table(name = "EPISODE")
@NamedQueries(
{
    @NamedQuery(name = "Episode.findAll", query = "SELECT e FROM Episode e")
})
public class Episode extends Observable implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "web_id", length = 100)
    private String webId;
    @Column(name = "name", length = 100)
    private String name;
    @Column(name = "task_url", length = 10000)
    private String taskUrl;
    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private EpisodeStatus status;
    @Column(name = "webpage_url", length = 1000)
    private String webpageUrl;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "program_id")
    private Program program;

    @OneToMany(cascade=CascadeType.ALL, mappedBy="episode", fetch=FetchType.LAZY)
    List<DownloadTask> taskList = new ArrayList<DownloadTask>();

    public Episode()
    {
    }

    public Episode(Long id)
    {
        this.id = id;
    }
    public Episode(String webId, String name)
    {
        this.webId = webId;
        this.name = name;
        status = EpisodeStatus.Init;
    }

     public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getWebpageUrl()
    {
        return webpageUrl;
    }

    public void setWebpageUrl(String webpageUrl)
    {
        this.webpageUrl = webpageUrl;
    }

    public Program getProgram()
    {
        return program;
    }

    public void setProgram(Program program)
    {
        this.program = program;
    }

    public String getWebId()
    {
        return webId;
    }

    public void setWebId(String webId)
    {
        this.webId = webId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public EpisodeStatus getStatus()
    {
        return status;
    }

    public void setStatus(EpisodeStatus status)
    {
        this.status = status;
        notifyObservers();
    }

    public String getTaskUrl()
    {
        return taskUrl;
    }

    public void setTaskUrl(String taskUrl)
    {
        this.taskUrl = taskUrl;
    }

    public List<DownloadTask> getTaskList()
    {
        return taskList;
    }

    public void setTaskList(List<DownloadTask> taskList)
    {
        this.taskList = taskList;
    }

   

    @PostLoad
    void onPostLoad()
    {
        //for(DownloadTask t : taskList)
        //    t.getName();
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
        if (!(object instanceof Episode))
        {
            return false;
        }
        Episode other = (Episode) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        if (this.getWebpageUrl()!=null && !this.getWebpageUrl().equals(other.getWebpageUrl()))
        {
            return false;
        }
        if (this.getWebId()!=null && !this.getWebId().equals(other.getWebId()))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return name;
    }
    
    @Transient
    public void combineEpi()
    {
        if(taskList.size()<2)
        {
            status = EpisodeStatus.Finished;
            return;
        }
        if(isComplete())
            (new Combiner()).combine(this);
    }

    @Transient
    public boolean isComplete()
    {
        if(status == EpisodeStatus.Skipped)
            return true;
        List<DownloadTask> list = getTaskList();
        if(list.size()==0)
            return false;
        for(DownloadTask t : list)
            if( t.getStatus()!=TaskStatus.Finished)
                return false;
        
        boolean re = true;
        for(DownloadTask t : list)
        {
            if(!Checker.check(t.getDest())|| (new File(t.getDest())).length() != t.getTotal())
            {
                //(new File(t.getDest())).delete();
                t.setStatus(TaskStatus.Suspended);
                re = false;
            }
            else
                t.setStatus(TaskStatus.Finished);

        }
        return re;

    }


    public void notifyObservers()
    {
        setChanged();
        super.notifyObservers();
    }

}
