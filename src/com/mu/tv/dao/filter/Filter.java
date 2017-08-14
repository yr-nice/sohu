package com.mu.tv.dao.filter;

import com.mu.util.StringUtil;
import com.mu.util.log.Log;
import java.util.ArrayList;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author Peng Mu
 */
public class Filter extends Criteria
{
    private ArrayList<Sequence> orderBy = new ArrayList<Sequence>();
    private int indexStart = -1;
    private int numOfRow = -1;


    public Filter(String name)
    {
        super(name);
    }

    public void clear()
    {
        orderBy.clear();
        numOfRow = -1;
        indexStart = -1;
        orderBy.clear();
        super.clear();
    }

    public void addOrderBy(Sequence value)
    {
        orderBy.add(value);
    }

    public int getNumOfRow()
    {
        return numOfRow;
    }

    public void setNumOfRow(int numOfRow)
    {
        this.numOfRow = numOfRow;
    }

    public int getIndexStart()
    {
        return indexStart;
    }

    public void setIndexStart(int indexStart)
    {
        this.indexStart = indexStart;
    }



    public Query getQuery(EntityManager em)
    {
        StringBuffer sb = new StringBuffer("SELECT o FROM " + entityName + " o ");
        sb.append(getCriteria("o"));
        sb.append(formatOrder());
        String s = sb.toString();
        Log.debug("Filter: %s", s);
        Query q = em.createQuery(s);
        setParams(q);
        return q;
    }


    protected String formatOrder()
    {
        StringBuilder sb = new StringBuilder();
        if(orderBy.size()>0)
        {
            sb.append(" order by ");
            for(Sequence e : orderBy)
                sb.append(String.format(" o.%s %s,", e.getEntityAttrName(), e.getOrderStr()));
        }
        return " " + StringUtil.trim(sb.toString(), ", ");
    }

    protected void setParams(Query q)
    {
        super.setParams(q);
        if(indexStart >= 0)
            q.setFirstResult(indexStart);
        if(numOfRow > 0)
            q.setMaxResults(numOfRow);

    }



}
