package net.sf.jhunlang.jmorph.util;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TreeSet;

// not thread safe; removes oldest if size exceeds capacity.
public abstract class CacheHashtable extends Hashtable
{
  protected long time;
  protected TreeSet entries = new TreeSet();

  // remove drop oldest keys
  public void remove(int drop)
  {
    while(drop-- > 0)
    {
      Entry e = (Entry)entries.first();
      remove(e.key());
      entries.remove(e);
    }
  }

  public Object remove(Object key)
  {
    Entry o = (Entry)super.remove(key);
    if ( o != null )
    {
      entries.remove(o);
    }
    return o == null ? null : o.value();
  }

  public Object put(Object key, Object value)
  {
    Entry e = new Entry(key, value);
    Entry o = (Entry)super.put(key, e);

    if (o != null)
    {
      entries.remove(o);
    }

    entries.add(e);
    remove(drop());

    return o == null ? null : o.value();
  }

  public Object first()
  {
    Entry e = (Entry)entries.first();
    return e == null ? null : e.value();
  }

  public Object last()
  {
    Entry e = (Entry)entries.last();
    return e == null ? null : e.value();
  }

  public Object get(Object key)
  {
    Entry o = (Entry)super.get(key);
    return o == null ? null : o.value();
  }

  public void clear()
  {
    super.clear();
    entries.clear();
  }

  public Enumeration elements()
  {
    return new EntryEnum(super.elements());
  }

  public Object clone()
  {
    CacheHashtable cht = (CacheHashtable)super.clone();
    cht.entries = (TreeSet)entries.clone();
    return cht;
  }

  protected abstract int drop();

  private class EntryEnum implements Enumeration
  {
    Enumeration en;

    EntryEnum(Enumeration en)
    {
      this.en = en;
    }

    public boolean hasMoreElements()
    {
      return en.hasMoreElements();
    }

    public Object nextElement()
    {
      return ((Entry)en.nextElement()).value();
    }
  }

  private class Entry implements Comparable
  {
    protected Long age;
    protected Object key;
    protected Object value;
    protected int hashCode;

    Entry(Object key, Object value)
    {
      age = new Long(time++);
      this.key = key;
      this.value = value;
      hashCode = this.value.hashCode();
    }

    Object key()
    {
      return key;
    }

    Object value()
    {
      return value;
    }

    Long age()
    {
      return age;
    }

    public int hashCode()
    {
      return hashCode;
    }

    public boolean equals(Object o)
    {
      return o != null && (o instanceof Entry) && compareTo(o) == 0;
    }

    public int compareTo(Object o)
    {
      return age.compareTo(((Entry)o).age());
    }
  }
}

