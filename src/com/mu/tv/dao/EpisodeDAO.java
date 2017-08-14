package com.mu.tv.dao;

import com.mu.tv.dao.filter.Filter;
import com.mu.tv.entity.Episode;
import javax.persistence.Query;

/**
 *
 * @author Peng Mu
 */
public class EpisodeDAO extends BaseDao
{
    public static Episode getByWebId(String webId)
    {
        Filter f = new Filter("Episode");
        f.addEqual("webId", webId);
        return (Episode)getSingle(f);
    }
    public static Episode getByWebPageURL(String url)
    {
        Filter f = new Filter("Episode");
        f.addEqual("webpageUrl", url);
        return (Episode)getSingle(f);
    }

    public static void remove(Episode e)
    {
        em.getTransaction().begin();
        Query q1 = em.createNativeQuery("delete from DOWNLOAD_TASK where episode_id="+ e.getId());
        Query q2 = em.createNativeQuery("delete from Episode where id="+ e.getId());
        q1.executeUpdate();
        q2.executeUpdate();
        em.getTransaction().commit();

    }
}
