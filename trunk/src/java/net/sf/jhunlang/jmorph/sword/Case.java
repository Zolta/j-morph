package net.sf.jhunlang.jmorph.sword;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import java.io.Serializable;

import net.sf.jhunlang.jmorph.util.Comparables;

/**
 * Case instances represent prefix and affix categories.
 * They are mapped by their {@link #name}s.
 */ 
public class Case implements Serializable
{
  public final static String[] ZERO_MORPHS =
  {
    "NOM",
    "PRES_INDIC_INDEF_SG_3"
  };
  
  /**
   * The map of Case instances by their {@link #name}s.
   */
  protected final static Map cases = new TreeMap();
  /**
   * Convenience constant for the empty Case.
   */
  public final static Case empty = getCase("");
  /**
   * The affix morpheme 
   */
  protected String affix;
  /**
   * The morpheme (allomorph) of this Case.
   */
  protected String name;
  
  protected boolean zero;

  /**
   * Return a Case instance for <code>name</code> with zero morpheme.
   * Return the Case instance from {@link #cases} for <code>name</code> if any.
   * Create and return a new one otherwise.
   * @param name the name of the case
   * @return the Case instance for <code>name</code> with zero morpheme 
   */
  public static Case getCase(String name)
  {
    return getCase("", name);
  }

  /**
   * Return a Case instance for <code>name</code> with allomorph
   * <code>affix</code>. Return the Case instance from {@link #cases}
   * for <code>affix</code> and <code>name</code> if any. Create and
   * return a new one otherwise.
   * @param affix the allomorph of the case
   * @param name the name of the case
   * @return the Case instance for <code>name</code> with allomorph
   * <code>affix</code> 
   */
  public static Case getCase(String affix, String name)
  {
    Comparables c = new Comparables(affix, name, false);
    Case cas = (Case)cases.get(c);
    if (cas == null)
    {
      for(int i = 0; i < ZERO_MORPHS.length; i++)
      {
        if (name.equalsIgnoreCase(ZERO_MORPHS[i]))
        {
          cas = new Case(affix, name, true);                   
        }
      }
      if (cas == null)
      {
        cas = new Case(affix, name, false);                           
      }
      cases.put(c, cas);
    }
    return cas;
  }

  /**
   * Return an iterator for all the Case instances created so far.
   * @return an iterator for all the Case instances created so far
   */
  public static Iterator getCaseIterator()
  {
    return cases.values().iterator();
  }

  /**
   * Create a Case instance for <code>name</code> with allomorph
   * <code>affix</code>.
   * @param affix the allomorph of the case
   * @param name the name of the case
   */
  protected Case(String affix, String name, boolean zero)
  {
    this.affix = affix.intern();
    this.name = name.intern();
    this.zero = zero;
  }
  
  public boolean zero()
  {
    return zero;
  }

  /**
   * Return the name of this Case instance.
   * @return the name of this Case instance
   */
  public String getName()
  {
    return name;
  }

  /**
   * Return the allomorph of this Case instance.
   * @return the allomorph of this Case instance
   */
  public String getAffix()
  {
    return affix;
  }

  public String morphString()
  {
    return affix.length() == 0 ? name : (affix + ", " + name);
  }

  public String contentString()
  {
    return morphString();
  }

  public String toString()
  {
    return "{" + contentString() + "}";
  }
}

