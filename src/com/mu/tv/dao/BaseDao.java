package com.mu.tv.dao;

import com.mu.tv.dao.filter.Counter;
import com.mu.tv.dao.filter.Filter;
import com.mu.tv.dao.filter.Updater;
import com.mu.util.log.Log;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;

/**
 *
 * @author Peng Mu
 */
public class BaseDao
{
    static final protected EntityManagerFactory emf = Persistence.createEntityManagerFactory("hibernate_derby");
    static final protected EntityManager em;
    static{
         em = emf.createEntityManager();
    }
    //static private EntityManagerFactory emf;
    synchronized static public void insert(Object e)
    {
        //EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(e);
        em.getTransaction().commit();
        //em.close();

    }
    synchronized static public void insert(List list)
    {
        long l = System.currentTimeMillis();
        //EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        for(int i=0; i<list.size(); i++ )
        {
            Object e = list.get(i);
            em.persist(e);
            if(i%50 == 0)
            {
                em.flush();
                em.clear();
            }
        }
        em.getTransaction().commit();
        //em.close();
        Log.interval("insert "+list.size()+" rows", l);
    }

    synchronized static public void update(Updater u)
    {
        long l = System.currentTimeMillis();
        //EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Query a = u.getQuery(em);
        long num = a.executeUpdate();
        em.getTransaction().commit();
        //em.close();

        Log.interval("update "+num+" rows", l);
    }

    synchronized static public void update(Object e)
    {
        //EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(e);
        em.getTransaction().commit();
        //em.close();
    }

    synchronized static public void remove(Object e)
    {
        //EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Object m = em.merge(e);
        em.remove(m);
        em.getTransaction().commit();
        //em.close();
    }

    static public List get(Filter ft)
    {
        long l = System.currentTimeMillis();

        //EntityManager em = emf.createEntityManager();
        Query a = ft.getQuery(em);
        List re = a.getResultList();
        //em.close();
        int num = re==null? 0:re.size();
        Log.interval("fetch "+num+" rows", l);
        return re;
    }

    static public Object getSingle(Filter ft)
    {
        long l = System.currentTimeMillis();

        //EntityManager em = emf.createEntityManager();
        Query a = ft.getQuery(em);
        Object re =null;
        try{
        re = a.getSingleResult();
        }catch(NoResultException e){}
        //em.close();
        int num = re==null? 0:1;
        Log.interval("fetch "+num+" rows", l);
        return re;
    }


    static public <T> T getById(Class<T> c, Object id)
    {
        //EntityManager em = emf.createEntityManager();
        T re = em.find(c, id);
        //em.close();
        return re;
    }

    static public long count(Counter c)
    {
        long l = System.currentTimeMillis();
        //EntityManager em = emf.createEntityManager();
        Query a = c.getQuery(em);
        long re = (Long)a.getSingleResult();
        //em.close();
        Log.interval("count "+re+" rows", l);
        return re;

    }

    static public void close()
    {
        if(em.isOpen())
            em.close();
    }
}
