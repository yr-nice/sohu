package com.mu.tv.dao.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import javax.persistence.Query;

/**
 *
 * @author Peng Mu
 */
public class Criteria
{
    private HashMap<String, Object> like = new HashMap<String, Object>();
    private HashMap<String, Object> equal = new HashMap<String, Object>();
    private HashMap<String, List> in = new HashMap<String, List>();
    private HashMap<String, List> notIn = new HashMap<String, List>();
    private HashMap<String, Object> lessThan = new HashMap<String, Object>();
    private HashMap<String, Object> moreThan = new HashMap<String, Object>();
    private HashMap<String, Object> notEqual = new HashMap<String, Object>();
    protected String entityName;


    public Criteria(String name)
    {
        entityName = name;
    }

    public void clear()
    {
        like.clear();
        equal.clear();
        in.clear();
        lessThan.clear();
        moreThan.clear();
        notEqual.clear();
        notIn.clear();
    }

    public void addLike(String key, Object value)
    {
        like.put("like_"+key, value);
    }
    public void addIn(String key, List value)
    {
        in.put("in_"+key, value);
    }
    public void addNotIn(String key, List value)
    {
        notIn.put("notIn_"+key, value);
    }
    public void addEqual(String key, Object value)
    {
        equal.put("equal_"+key, value);
    }
    public void addNotEqual(String key, Object value)
    {
        notEqual.put("notEqual_"+key, value);
    }
    public void addMoreThan(String key, Object value)
    {
        moreThan.put("moreThan_"+key, value);
    }
    public void addLessThan(String key, Object value)
    {
        lessThan.put("lessThan_"+key, value);
    }

    public String getEntityName()
    {
        return entityName;
    }

    public void setEntityName(String entityName)
    {
        this.entityName = entityName;
    }



    protected String getCriteria(String symbol)
    {
        StringBuffer sb = new StringBuffer(" where 1=1 ");
        formatCriteria(" and %s.%s = :%s ", symbol, equal, sb);
        formatCriteria(" and %s.%s != :%s ", symbol, notEqual, sb);
        formatCriteria(" and %s.%s in (:%s) ", symbol, in, sb);
        formatCriteria(" and %s.%s not in (:%s) ", symbol, notIn, sb);
        formatCriteria(" and %s.%s > :%s ", symbol, moreThan, sb);
        formatCriteria(" and %s.%s < :%s ", symbol, lessThan, sb);
        formatCriteria(" and %s.%s like :%s ", symbol, like, sb);
        String s = sb.toString().replaceAll("\\W$", ""); //remove non word char at the end
        return s;
    }

    private void formatCriteria(String criteria, String symbol, HashMap map, StringBuffer sb)
    {
        for(Object e : map.keySet())
            sb.append(String.format(criteria, symbol, extractEntityAttr((String)e), e));
    }


    protected void setParams(Query q)
    {
        for(Entry<String, Object> e : like.entrySet())
            q.setParameter(e.getKey(), e.getValue());

        for(Entry e : in.entrySet())
            q.setParameter((String)e.getKey(), e.getValue());

        for(Entry e : notIn.entrySet())
            q.setParameter((String)e.getKey(), e.getValue());

        for(Entry<String, Object> e : equal.entrySet())
            q.setParameter(e.getKey(), e.getValue());

        for(Entry<String, Object> e : notEqual.entrySet())
            q.setParameter(e.getKey(), e.getValue());

        for(Entry<String, Object> e : moreThan.entrySet())
            q.setParameter(e.getKey(), e.getValue());

        for(Entry<String, Object> e : lessThan.entrySet())
            q.setParameter(e.getKey(), e.getValue());

    }

    protected String extractEntityAttr(String key)
    {
        return key.replaceAll("^.*_", "");
    }


}
