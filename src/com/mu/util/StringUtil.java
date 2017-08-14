package com.mu.util;

import com.mu.util.log.Log;
import java.lang.Character.UnicodeBlock;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Peng Mu
 */
public class StringUtil
{
    static private String _SUBMISSION_ = "submission";
    static private String _TRANSMITTER_ = ":53286/submission/transmitter";

    static public void main(String[] argu)
    {
        System.out.println(unescapeHtmlUnicodeStr("&#29233;&#26080;&#24724;[&#27743;&#21335;&#31532;&#19968;&#24215;]01"));
    }
    //static private String _TRANSMITTERPORT_ = ":53286/";
    //static private String _REGEX_SYMBOL_ = "[\\^$.|?*+(){}";
    static public String getTransmitterUrl(String rtdpUrl)
    {
        //int i = rtdpUrl.indexOf(_SUBMISSION_);
        //String re = rtdpUrl.substring(0, i+_SUBMISSION_.length())+_TRANSMITTER_;
        String re = rtdpUrl.replaceAll(":\\d+/.*$", _TRANSMITTER_);

        return re;
    }

    static public boolean isEmpty(String s)
    {
        if(s==null)
            return true;
        s = s.trim().toLowerCase();
        if(s.isEmpty() || s.equals("null"))
            return true;
        return false;
    }

    static public String trim(String src, String toBeTrimed)
    {
        if(src==null)
            return null;

        int start = 0;
        int end = src.length();

        for(int i=0; i<src.length(); i++)
        {
            char c = src.charAt(i);
            if(toBeTrimed.indexOf(c)==-1)
                break;
            start = i+1;
        }

        for(int i=src.length()-1; i>=0; i--)
        {
            char c = src.charAt(i);
            if(toBeTrimed.indexOf(c)==-1)
                break;
            end = i;
        }
        if(end>start)
            return src.substring(start, end);
        else
            return "";
    }

    static public String trimLeft(String src, String toBeTrimed)
    {
        if(src==null)
            return null;

        int start = 0;
        int end = src.length();

        for(int i=0; i<src.length(); i++)
        {
            char c = src.charAt(i);
            if(toBeTrimed.indexOf(c)==-1)
                break;
            start = i+1;
        }
        return src.substring(start, src.length());
    }

    static public String trimRight(String src, String toBeTrimed)
    {
        if(src==null)
            return null;

        int end = src.length();

        for(int i=src.length()-1; i>=0; i--)
        {
            char c = src.charAt(i);
            if(toBeTrimed.indexOf(c)==-1)
                break;
            end = i;
        }
        return src.substring(0, end);
    }

	public static String canonicalizeMobilePhone(String mobilePhone) 
    {
		// Don't suffer from the null pointer problem.
		if (mobilePhone == null)
			return null;

		// Check for explicit country code given.
		boolean hasCountryCode = mobilePhone.matches("^\\s*\\+");
		
		// Keep digits only.
		String m = mobilePhone.replaceAll("\\D", "");
		
		// If bogus mobilePhone, then return original.
		if (m.equals(""))
			return mobilePhone;
		
		// Add country code: always Singapore unless otherwise specified.
		if (!hasCountryCode && !m.startsWith("65")&& m.length() <= 8)
            m = "65" + m;
		
		return "+" + m;
	}

	public static boolean isValidMobile(String mobilePhone)
    {
        // Don't suffer from the null pointer problem.
		if (mobilePhone == null)
			return false;

        // only digits and '+' are accepted.
        if(mobilePhone.matches("^.*[^\\d\\+]+.*"))
            return false;

        if(!mobilePhone.startsWith("+"))
            mobilePhone = canonicalizeMobilePhone(mobilePhone);

        //local mobile should be >=11;
        if(mobilePhone.startsWith("+65") && mobilePhone.length()<11)
            return false;

		return true;
	}

	public static boolean isSingaporeMobile(String mobilePhone)
    {
        // Don't suffer from the null pointer problem.
		if (mobilePhone == null)
			return false;

        if(!mobilePhone.startsWith("+"))
            mobilePhone = canonicalizeMobilePhone(mobilePhone);

        //local mobile should be >=11;
        if(mobilePhone.startsWith("+65"))
            return true;

        return false;
	}


    static public int getSmsParamCount(String smsTemplate)
    {
        int re = 0;
        if(smsTemplate == null)
            return re;

        for(int i = 1; ; i++)
        {
            if(smsTemplate.indexOf("<PARAM"+i+">") !=-1)
                re = i;
            else
                break;
        }
        return re;
    }

    static public String getFileType(String url)
    {
        String re = "";
        try{

        url = url.trim();
        Pattern p = Pattern.compile("(\\..{1,3}$)");
        Matcher m = p.matcher(url);
        if(m.find())
            re = m.group(1);
        }catch(Exception e){Log.log(e);}
        return re;
    }
    public static String padLeft(String s, int n, String append)
    {
        String re = s;
        while(re.length() < n)
            re = append + re;
        return re;
    }

    public static String padRight(String s, int n, String append)
    {
        String re = s;
        while(re.length() < n)
            re += append;
        return re;
    }


    static public String toReadable(Object o)
    {
        if(o==null)
            return "null";
        StringBuilder sb = new StringBuilder("");
        for(Method f : o.getClass().getDeclaredMethods())
        {
            try{
            if(ReflectionUtil.isGetter(f))
            {
               Object v = f.invoke(o);
                 /*if(ReflectionUtil.isImplemented(v, Collection.class))
                    sb.append(StringUtil.padRight(f.getName().substring(3)+":", 12, " ") + Arrays.toString(((Collection)v).toArray()) +"\n");
                else*/
                    sb.append(StringUtil.padRight(f.getName().substring(3)+":", 12, " ") + v +"\n");
            }
            //sb.append(f.getName() + ":\t" +"\n");
            }catch(Exception e){Log.error(e);}
        }
        return sb.toString();

    }
    static public String prepareFileName(String name)
    {
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<name.length(); i++)
        {
            UnicodeBlock u = Character.UnicodeBlock.of(name.charAt(i));
            if(u == UnicodeBlock.BASIC_LATIN || u == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
                sb.append(name.charAt(i));
            else
                sb.append(" ");
        }
        return sb.toString().trim();
    }

    static public String unescapeHtmlUnicodeStr(String s)
    {
        ArrayList<String> arrs = RegexUtil.getAllUniqueMatch(s, "&#([\\d]+);");
        for(String d : arrs)
            s = s.replaceAll("&#"+d+";", String.valueOf((char)Integer.parseInt(d)));
        return s;
    }
}
