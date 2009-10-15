package net.sf.jhunlang.jmorph;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.LinkedList;

import java.io.Serializable;

/**
 * AffixEntries stands for storing affix entry instances so that finding them
 * by their append be fast and easy.
 */
public class AffixEntries implements Serializable
{
  /**
   * AffixEntries instances for affix entries with append longer than the
   * path to this instance, indexed by the character in their append next to
   * the character this instance stands for.
   */
  protected AffixEntries[] affixes;
  /**
   * The size of {@link #affixes}.
   */
  protected int size;
  /**
   * Affix entries belonging to this instance hashed by their strip or null.
   */
  protected Map byStrip;

  /**
   * Create an affix entries instance for empty append string.
   */
  public AffixEntries()
  {}

  /**
   * Return the map of affix entries hashed by their strip or
   * <code>null</code> if this instance has no associated affix entries.
   * @return the map {@link #byStrip}
   */
  public Map getByStrip()
  {
    return byStrip;
  }

  /**
   * Return the AffixEntries instance associated with <code>c</code> in this
   * instance or <code>null</code>.
   * @param c the character
   * @return the AffixEntries instance associated with <code>c</code> or
   * <code>null</code>
   */
  public AffixEntries getMap(char c)
  {
    return c < size ? affixes[c] : null;
  }

  /**
   * Add <code>entry</code> to this instance the associated character path
   * starting at the beginning of <code>entry</code>'s append.
   * @param entry the affixentry to add
   */
  public void add(AffixEntry entry)
  {
    add(entry, entry.getAppend().length() - 1);
  }

  /**
   * Add <code>entry</code> to this instance the associated character path
   * starting at the <code>i</code>th position of <code>entry</code>'s append.
   * @param entry the affix entry to add
   * @param i the starting position in append of <code>entry</code>
   */
  protected void add(AffixEntry entry, int i)
  {    
    if (i >= 0)
    {
      char affix = entry.getAppendCharAt(i);
      assure(affix);
      AffixEntries aff = affixes[affix];
      if (aff == null)
      {
        aff = new AffixEntries();
        affixes[affix] = aff;
      }
      aff.add(entry, i - 1);
      return;
    }

    String strip = entry.getStrip();
    
    if (byStrip == null)
    {
      // use TreeMap instead of HashMap for trying affixes with shorter
      // (especially empty) strip first 
      byStrip = new TreeMap();
    }
    
    Object o = byStrip.get(strip);

    if (o == null)
    {
      byStrip.put(strip, entry);
    }
    else if (o instanceof Collection)
    {
      ((Collection)o).add(entry);
    }
    else
    {
      Collection stripColl = new LinkedList();
      byStrip.put(strip, stripColl);
      stripColl.add(o);
      stripColl.add(entry);
    }
  }

  /**
   * Assure that the length of {@link #affixes} be greater than
   * <code>index</code>.
   * @param index the index to assure
   */
  protected void assure(int index)
  {
    if (size <= index)
    {
      AffixEntries[] at = new AffixEntries[index + 1];
      if (affixes != null)
      {
        System.arraycopy(affixes, 0, at, 0, size);
      }
      size = index + 1;
      affixes = at;
    }
  }
  
  public String toString()
  {
    return "AffixEntries[" + 
      (byStrip == null ? " none " : "" + byStrip.size()) + ", " + size + "]";
  }
}
