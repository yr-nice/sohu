package com.mu.tv.dao.filter;

/**
 *
 * @author Peng Mu
 */
public class Sequence
{
    String entityAttrName;
    boolean asc=true;

    public Sequence(String entityAttrName)
    {
        this.entityAttrName = entityAttrName;
    }

    public Sequence(String entityAttrName, boolean isAsc)
    {
        this.entityAttrName = entityAttrName;
        asc = isAsc;
    }

    public String getOrderStr()
    {
        return asc? "asc": "desc";
    }

    public String getEntityAttrName()
    {
        return entityAttrName;
    }

}
