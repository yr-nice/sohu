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
public class ColumnSelector extends Filter
{
    private ArrayList<String> columns = new ArrayList<String>();
    private ArrayList<String> groupBy = new ArrayList<String>();


    public ColumnSelector(String name)
    {
        super(name);
    }

    public void clear()
    {
        columns.clear();
        groupBy.clear();
        super.clear();
    }

    public void addColumns(String col)
    {
        columns.add(col);
    }

    public void addGroupBy(String col)
    {
        groupBy.add(col);
    }


    public Query getQuery(EntityManager em)
    {

        StringBuffer sb = new StringBuffer("SELECT "+formatCol()+" FROM " + entityName + " o ");
        sb.append(getCriteria("o"));
        sb.append(formatOrder());
        sb.append(formatGroupBy());
        String s = sb.toString();
        Log.info("ColumnSelector: %s", s);
        Query q = em.createQuery(s);
        setParams(q);
        return q;
    }

    protected String formatCol()
    {
        StringBuilder sb = new StringBuilder();
        if(columns.size()>0)
        {
            for(String e : columns)
                sb.append(String.format(" %s,", e));
        }
        return " " + StringUtil.trim(sb.toString(), ", ");
    }

    protected String formatGroupBy()
    {
        if(groupBy.size()==0)
            return "";

        StringBuilder sb = new StringBuilder(" group by ");
        for(String e : groupBy)
            sb.append(String.format(" %s,", e));

        return " " + StringUtil.trim(sb.toString(), ", ");
    }
}
