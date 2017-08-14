/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mu.util;

import com.mu.util.log.Log;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Peng mu
 */
public class RegexUtil
{
    static public ArrayList<String> getAllMatch(String str, String pattern)
    {
        ArrayList<String> re = new ArrayList<String>();
        try{
            Pattern p = Pattern.compile(pattern, Pattern.MULTILINE|Pattern.DOTALL);
            Matcher m = p.matcher(str);
            //m.matches();
            while(m.find())
            {
                for(int i=1; i<=m.groupCount(); i++)
                    re.add(m.group(i));
            }
        }catch(Exception e)
        {
            Log.error(e);
        }

        return re;
    }

    static public ArrayList<String[]> getAllMatchArr(String str, String pattern)
    {
        ArrayList<String[]> re = new ArrayList<String[]>();
        try{
            Pattern p = Pattern.compile(pattern, Pattern.MULTILINE|Pattern.DOTALL);
            Matcher m = p.matcher(str);
            //m.matches();
            while(m.find())
            {
                String[] s = new String[m.groupCount()];
                for(int i=1; i<=m.groupCount(); i++)
                    s[i-1] = m.group(i);
                re.add(s);
            }
        }catch(Exception e)
        {
            Log.error(e);
        }

        return re;
    }

    static public ArrayList<String> getAllUniqueMatch(String str, String pattern)
    {
        return (ArrayList<String>)ListUtil.getUniqueList(getAllMatch(str, pattern));
    }

    static public String getFirstMatchByIndex(String str, String pattern, int index)
    {
        try{
            Pattern p = Pattern.compile(pattern, Pattern.MULTILINE|Pattern.DOTALL);
            Matcher m = p.matcher(str);
            //m.matches();
            if(m.find())
            {
                return m.group(index);
            }
        }catch(Exception e)
        {
            Log.error(e);
        }

        return null;
    }

}
