package net.sf.jhunlang.jmorph.analysis;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

public class Cache
{
  int capacity = 180;
  int initialCapacity = 300;

  // list of maps
  Map[] lom = new HashMap[2];
  // index of lom to put into
  int index;

  public Cache()
  {
    this(100, 60);
  }

  public Cache(int initialCapacity, int capacity)
  {
    this.initialCapacity = initialCapacity;
    this.capacity = Math.max(initialCapacity, capacity);
    for(int i = 0; i < 2; i++)
    {
      lom[i] = new HashMap(initialCapacity, 0.75f);
    }
  }

  public synchronized void clear()
  {
    index = 0;
    lom[0].clear();
    lom[1].clear();
  }

  public synchronized Collection getCollection(Object p)
  {
    Collection c;

    for(int i = 0; i <= index; i++)
    {
      if ((c = (Collection)lom[i].get(p)) != null)
      {
        return c;
      }
    }
    return null;
  }

  public synchronized Collection putCollection(Object p, Collection c)
  {
    Collection old = (Collection)lom[index].put(p, c);

    if (lom[index].size() > capacity)
    {
      if (index < 1)
      {
        index++;
      }
      else
      {
        lom[0] = lom[1];
        lom[1] = new HashMap(initialCapacity, 0.80f);
      }
    }
    return old;
  }
}

