package net.sf.jhunlang.jmorph.util;

public class Pair
{
  protected final Object a;
  protected final Object  b;

  protected boolean computed;
  protected int hash;

  public Pair(Object a, Object b)
  {
    if (a == null || b == null)
    {
      throw new IllegalArgumentException("Neither component can be null");
    }
    this.a = a;
    this.b = b;
  }

  public Object getA()
  {
    return a;
  }

  public Object getB()
  {
    return b;
  }

  public synchronized int hashCode()
  {
    if (!computed)
    {
      hash = computeHashCode();
      computed = true;
    }
    return hash;
  }

  public boolean equals(Object o)
  {
    if (o == null || o.getClass() != getClass())
    {
      return false;
    }
    return tellEquals(o);
  }

  protected boolean tellEquals(Object o)
  {
    Pair op = (Pair)o;
    return hashCode() == op.hashCode() &&
      a.equals(op.getA()) && b.equals(op.getB());
  }

  protected int computeHashCode()
  {
    return a.hashCode() * 31 + b.hashCode();
  }

  public static String shorten(String s)
  {
    return s.substring(s.lastIndexOf('.') + 1);
  }

  public static String shorten(Class clz)
  {
    return shorten(clz.getName());
  }

  public String contentString()
  {
    return a + ", " + b;
  }

  public String toString()
  {
    return shorten(getClass()) + "[" + contentString() + "]";
  }
}
