package net.sf.jhunlang.jmorph;

import java.io.Serializable;

import java.util.Collection;
import java.util.ArrayList;

/**
 * The common base class of Prefix and Suffix.
 */
public abstract class Affix implements Serializable
{
  /**
   * The name or flag of the affix
   */
  protected char name;
  /**
   * The inflexion rules
   */
  protected AffixEntry[] entries;
  /**
   * If the rules can be crossed
   */
  protected boolean crossable;
  /**
   * Temporary collection of entries; {@link #done()} converts this
   * collection into {@link #entries}
   */
  protected Collection entryList;
  /**
   * The specified number of entries.
   */
  protected int size;

  /**
   * Create a new Affix instance with the given parameters
   * @param name the name or flag
   * @param crossable if the rules can be crossed
   */
  protected Affix(char name, boolean crossable, int size)
  {
    this.name = name;
    this.crossable = crossable;
    this.size = size;
    entryList = new ArrayList(size);
  }

  /**
   * Return the name character of this affix
   * @return the name character of this affix
   */
  public char getName()
  {
    return name;
  }

  /**
   * Return the name character of this affix as byte flag
   * @return the name character of this affix as byte
   */
  public int getFlag()
  {
    return name;
  }

  /**
   * Return the array of {@link AffixEntry} instances belonging
   * to this affix rule
   * @return the entries of this affix rule
   */
  public AffixEntry[] getEntries()
  {
    return entries;
  }

  /**
   * Return if the entries of this affix can be crossed
   * @return if the entries of this affix can be crossed
   */
  public boolean crossable()
  {
    return crossable;
  }
  
  public int specifiedSize()
  {
    return size;
  }

  /**
   * Add entry to <code>entries</code>
   * @param entry the rule to add
   */
  public void addEntry(AffixEntry entry)
  {
    entryList.add(entry);
  }
  
  public int size()
  {
    return entries.length;
  }

  /**
   * Should be called when all entries have been added.
   * Converts {@link #entryList} to {@link #entries} and clear
   * {@link #entryList}.
   */
  public void done()
  {
    entries = (AffixEntry[])entryList.toArray(new AffixEntry[entryList.size()]);
    entryList.clear();
  }

  /**
   * Create a new AffixEntry with the given parameters.
   * @param rules the rules this affix belongs to
   * @param index the index of the rule
   * @param acondition the conditions of the rule
   * @param strip the characters to strip from stems
   * @param append the characters that can be added to stripped stems
   */
  public abstract AffixEntry createEntry(Rules rules, int index,
      Condition[] acondition, String strip, String append);

  public abstract AffixEntry createEntry(Rules rules, int index,
      Condition[] acondition, String strip, String append, String morph);

  public String longContentString()
  {
    return name + "(" + (int)name + "), " + crossable + ", " + entries.length;
  }

  public String contentString()
  {
    return "" + name;
  }

  public String toLongString()
  {
    String s = getClass().getName();
    s = s.substring(s.lastIndexOf('.') + 1);
    return s + "[" + longContentString() + "]";
  }

  public String toString()
  {
    String s = getClass().getName();
    s = s.substring(s.lastIndexOf('.') + 1);
    return s + "[" + contentString() + "]";
  }
}