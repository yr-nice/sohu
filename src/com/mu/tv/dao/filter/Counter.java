package com.mu.tv.dao.filter;

import com.mu.util.log.Log;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author Peng Mu
 */
public class Counter extends Criteria
{
    public Counter(String name)
    {
        super(name);
    }

    public Query getQuery(EntityManager em)
    {
        StringBuffer sb = new StringBuffer("SELECT count(o) FROM " + entityName + " o ");
        sb.append(getCriteria("o"));
        String s = sb.toString();
        Log.info("Counter: %s", s);
        Query q = em.createQuery(s);
        setParams(q);
        return q;
    }


    protected void setParams(Query q)
    {
        super.setParams(q);
    }

}
