package net.sf.jhunlang.jmorph;

import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedList;

public class Dictionaries extends LinkedList
{
  public final static String GENERATE_NUMBERS = "dict.number";
  
  protected boolean generateNumber = Boolean.getBoolean(GENERATE_NUMBERS);
  
  /**
   * Create a new Dictionaries.
   */
  public Dictionaries()
  {}

  /**
   * Create a new Dictionaries and add code>wl</code>.
   * @param wl the word list to add
   */
  public Dictionaries(WordList wl)
  {
    add(wl);
  }

  /**
   * Add <code>o</code> to this Dictionaries. <code>o</code> must be instance
   * of {@link WordList}; otherwise a <code>ClassCastException</code is thrown.
   * @param o the wordlist to add
   */
  public boolean add(Object o)
  {
    if (o instanceof WordList)
    {
      WordList wl = (WordList)o;
      if (wl.getDictionaries() == this)
      {
        return false;
      }
      if (super.add(wl))
      {
        wl.setDictionaries(this);
        return true;
      }
    }
    return false;
  }

  /**
   * Return the collection of dictionary entries having <code>root</code> as
   * their root entry. 
   * @param root
   * @return the collection of dictionary entries having <code>root</code> as
   * their root entry
   */
  public Collection getReverseRoots(DictEntry root)
  {
    Collection c = null;
    Iterator it = iterator();
    while (it.hasNext())
    {
      if ((c = ((WordList)it.next()).getReverseRoots(root)) != null)
      {
        break;
      }
    }
    return c;
  }

  public Object rawGet(String lower)
  {
    if (generateNumber &&
        Character.getType(lower.charAt(0)) == Character.DECIMAL_DIGIT_NUMBER)
    {
      try
      {
        Integer.parseInt(lower);
        return new NumberEntry(lower);
      }
      catch (NumberFormatException nfx)
      {
        return null; // some dictionaries may contain words like '1-ben'
      }
    }
    
    Object o;
    Iterator it = iterator();
    while (it.hasNext())
    {
      if ((o = ((WordList)it.next()).rawGet(lower)) != null)
      {
        return o;
      }
    }
    return null;
  }

  /**
   * Return the {@link DictEntry} associated with given root word or null.
   * @param word the root word
   * @return the {@link DictEntry} associated with given root word or null.
   */
  public DictEntry get(String word)
  {
    DictEntry entry = null;
    Iterator it = iterator();
    while (it.hasNext())
    {
      if ((entry = ((WordList)it.next()).get(word)) != null)
      {
        break;
      }
    }
    return entry;
  }
}