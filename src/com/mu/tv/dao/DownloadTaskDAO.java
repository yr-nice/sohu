package com.mu.tv.dao;

import com.mu.tv.dao.filter.Filter;
import com.mu.tv.entity.DownloadTask;
import com.mu.tv.entity.Episode;
import javax.persistence.Query;

/**
 *
 * @author Peng Mu
 */
public class DownloadTaskDAO extends BaseDao
{
    public static DownloadTask getByUrl(String url)
    {
        Filter f = new Filter("DownloadTask");
        f.addEqual("url", url);
        return (DownloadTask)getSingle(f);

    }

 
    public static void removeByEpisode(Episode e)
    {
        em.getTransaction().begin();
        Query q1 = em.createNativeQuery("delete from DOWNLOAD_TASK where episode_id="+ e.getId());
        q1.executeUpdate();
        em.getTransaction().commit();

    }

    public static void remove(DownloadTask t)
    {
        em.getTransaction().begin();
        Query q1 = em.createNativeQuery("delete from DOWNLOAD_TASK where id="+ t.getId());
        q1.executeUpdate();
        em.getTransaction().commit();

    }

}
