package com.mu.tv.entity;

import com.mu.tv.ProgramStatus;
import java.io.File;
import java.io.Serializable;
import java.util.List;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author Peng Mu
 */
@Entity
@Table(name = "PROGRAM")
@NamedQueries(
{
    @NamedQuery(name = "Program.findAll", query = "SELECT p FROM Program p")
})
public class Program implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "web_id", length = 30)
    private String webId;
    @Column(name = "url", length = 255)
    private String url;
    @Column(name = "name", length = 100)
    private String name;
    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private ProgramStatus status;
    @Column(name = "source", length = 100)
    private String source;
    @Column(name = "use_web_name")
    private Boolean useWebName;

    @OneToMany(cascade=CascadeType.ALL, mappedBy="program", fetch=FetchType.EAGER)
    List<Episode> episodeList;

    public Program()
    {
    }
    public Program(String url, String name)
    {
        this.url = url;
        this.name = name;
        this.status = ProgramStatus.Init;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getWebId()
    {
        return webId;
    }

    public void setWebId(String webId)
    {
        this.webId = webId;
    }


    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public ProgramStatus getStatus()
    {
        return status;
    }

    public void setStatus(ProgramStatus status)
    {
        this.status = status;
    }

    public List<Episode> getEpisodeList()
    {
        return episodeList;
    }

    public void setEpisodeList(List<Episode> episodeList)
    {
        this.episodeList = episodeList;
    }

    public Boolean getUseWebName()
    {
        return useWebName;
    }

    public void setUseWebName(Boolean useWebName)
    {
        this.useWebName = useWebName;
    }

    @Transient
    public File getFolder()
    {
        return new File("download"+File.separator+name);
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
        if (!(object instanceof Program))
        {
            return false;
        }
        Program other = (Program) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
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

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

}
