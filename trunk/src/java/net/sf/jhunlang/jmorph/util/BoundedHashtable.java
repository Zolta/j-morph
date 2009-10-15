package net.sf.jhunlang.jmorph.util;

// not thread safe; removes oldest if size exceeds capacity.
public class BoundedHashtable extends CacheHashtable
{
  protected int capacity;
  protected int dropLimit;

  public BoundedHashtable(int capacity, int dropLimit)
  {
    super();
    this.capacity = capacity;
    this.dropLimit = dropLimit;
  }

  public void setCapacity(int capacity)
  {
    this.capacity = capacity;
    remove(size() - capacity);
  }

  protected int drop()
  {
    int drop = size() - capacity;
    return drop > dropLimit ? drop : 0;
  }

  public Object clone()
  {
    BoundedHashtable bht = (BoundedHashtable)super.clone();
    bht.capacity = capacity;
    return bht;
  }
}

