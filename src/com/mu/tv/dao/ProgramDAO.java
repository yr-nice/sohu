package com.mu.tv.dao;

import com.mu.tv.dao.filter.Filter;
import com.mu.tv.entity.Program;
import java.util.List;

/**
 *
 * @author Peng Mu
 */
public class ProgramDAO extends BaseDao
{
    public static Program getByUrl(String url)
    {
        Filter f = new Filter("Program");
        f.addEqual("url", url);
        return (Program)getSingle(f);

    }

    public static void deleteByUrl(String url)
    {
        Program p = getByUrl(url);
        remove(p);
    }

    static public List<Program> getAll()
    {
        return (List<Program>)get(new Filter("Program"));
    }


}
