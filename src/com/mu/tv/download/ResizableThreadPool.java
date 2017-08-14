package com.mu.tv.download;

import com.mu.util.log.Log;
import java.lang.Runnable;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Peng Mu
 */
public class ResizableThreadPool extends ThreadPoolExecutor
{
    private LinkedBlockingDeque<Runnable> queue;
    private HashMap<Runnable, Future> map = new HashMap<Runnable, Future>();
    public ResizableThreadPool()
    {
        super(1, 1, 0, TimeUnit.NANOSECONDS,new LinkedBlockingDeque<Runnable>());
        queue = (LinkedBlockingDeque<Runnable>)this.getQueue();
    }

    public void add(Runnable t)
    {
        if(contain(t))
            return;
        map.put(t, submit(t));
    }

    public void addFirst(Runnable t)
    {
        add(t);
        Future f = map.get(t);
        if(queue.contains(f))
        {
            queue.remove(f);
            queue.addFirst((Runnable)f);
        }

    }

    public boolean remove(Runnable t)
    {
        boolean b = false;
        if(contain(t))
        {
            Future f = map.get(t);
            b = f.cancel(true);
            map.remove(t);
            queue.remove(f);
        }
        return b;
    }

    public void resizeTo(int size)
    {
        if(size>0)
        {
            this.setCorePoolSize(size);
            this.setMaximumPoolSize(size);
        }
    }

    public boolean contain(Runnable r)
    {
        return map.containsKey(r);
    }

    protected void afterExecute(Runnable r,Throwable t)
    {

        //Log.info("AfterExecute, Task:%s, before remove, contain:%s", r, map.containsKey(r));
        Runnable key=null;
        for(Entry<Runnable,Future> en : map.entrySet())
        {
            if(en.getKey()==r || en.getValue()==r)
            {
               key = en.getKey();
               break;
            }
        }
        if(key != null)
            map.remove(key);
        
        int curSize = this.getPoolSize();
        int poolsize = this.getCorePoolSize();
        if(curSize > poolsize)
        {
            Log.info("AfterExecute, Terminate current Thread to Reduce Pool Size from " + curSize+ " to "+poolsize);
            throw (new RuntimeException("Terminate current Thread to Reduce Pool Size from " + curSize+ " to "+poolsize));
        }
    }

}
