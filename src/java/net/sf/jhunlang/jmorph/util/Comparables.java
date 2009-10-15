package net.sf.jhunlang.jmorph.util;

/**
 * 
 */
public class Comparables implements Comparable
{
  static long id;

  protected Comparable a;
  protected Comparable b;

  protected int hashCode;

  protected boolean identified;

  protected long lid;

  static synchronized long next()
  {
    return id++;
  }

  /**
   * <code>identified</code> tells if <code>a</code> and <code>b</code>
   * together identify this Comparables. Identified Comparables can be used
   * to differentiate otherwise equal Comparables instances by ordering them
   * by their creation order.
   */
  public Comparables(Comparable a, Comparable b, boolean identified)
  {
    this.a = a;
    this.b = b;
    this.identified = identified;
    hashCode = a.hashCode() * 31 + b.hashCode();
    lid = next();
  }
  
  public Comparables(Comparable a, long b, boolean identified)
  {
    this(a, new Long(b), identified);
  }

  public Comparables(Comparable a, int b, boolean identified)
  {
    this(a, new Integer(b), identified);
  }

  public Comparables(long a, long b, boolean identified)
  {
    this(new Long(a), new Long(b), identified);
  }

  public Comparables(long a, int b, boolean identified)
  {
    this(new Long(a), new Integer(b), identified);
  }

  public Comparable getA()
  {
    return a;
  }

  public Comparable getB()
  {
    return b;
  }

  /**
   * Compare this object with <code>o</o>.
   * Throws ClassCastException if o is not a Comparables.
   * @param o the other object
   * @return the relation of this object and <code>o</code> 
   */
  public int compareTo(Object o)
  {
    Comparables oc = (Comparables)o;

    int rel = a.compareTo(oc.getA());

    if (rel != 0)
    {
      return rel;
    }
    
    rel = b.compareTo(oc.getB());

    if (!identified || rel != 0)
    {
      return rel;
    }
    // if identified and they are equal then return
    // the relation of order of their creation
    return lid < oc.lid ? -1 : (lid > oc.lid ? 1 : 0);
  }

  public int hashCode()
  {
    return hashCode;
  }

  public boolean equals(Object o)
  {
    return
      o != null &&
      (o instanceof Comparables) &&
      hashCode == ((Comparables)o).hashCode &&
      compareTo(o) == 0;
  }

  // utility method for short class name
  public String shortClassname()
  {
    return shorten(getClass().getName());
  }

  // utility method for short class name
  public  static String shorten(String s)
  {
    return s.substring(s.lastIndexOf('.') + 1);
  }

  public String toString()
  {
    return a + "\t" + b;
  }
}
