package com.mu.tv.dao.filter;

import com.mu.util.StringUtil;
import com.mu.util.log.Log;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.persistence.EntityManager;
import javax.persistence.Query;
/**
 *
 * @author Peng Mu
 */
public class Updater extends Criteria
{
    private HashMap<String, Object> set = new HashMap<String, Object>();


    public Updater(String name)
    {
        super(name);
    }

    public void clear()
    {
        set.clear();
        super.clear();
    }
    public void addSet(String key, Object value)
    {
        set.put("set_"+key, value);
    }

    public Query getQuery(EntityManager em)
    {
        StringBuffer sb = new StringBuffer("UPDATE " + entityName + " o set ");
        sb.append(formatSetter(" o.%s=:%s, ", set));
        sb.append(getCriteria("o"));
        String s = sb.toString();
        Log.info("Updater: %s", s);
        Query q = em.createQuery(s);
        setParams(q);

        return q;
    }

    private String formatSetter(String criteria, HashMap map)
    {
        StringBuilder sb = new StringBuilder();
        for(Object e : map.keySet())
            sb.append(String.format(criteria, extractEntityAttr((String)e), e));
        return " " + StringUtil.trim(sb.toString(), ", ");
    }

    protected void setParams(Query q)
    {
        for(Entry<String, Object> e : set.entrySet())
            q.setParameter(e.getKey(), e.getValue());
        super.setParams(q);
    }

}
