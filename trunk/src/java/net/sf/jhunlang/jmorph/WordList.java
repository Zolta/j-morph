package net.sf.jhunlang.jmorph;

import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

import java.io.Serializable;

public class WordList implements Serializable
{
  /**
   * Number of homonyms.
   */
  protected int homos;  
  /**
   * The {@link Dictionaries} instances this WordList belongs to.
   */
  protected Dictionaries dicts;
  /**
   * Stores {@link DictEntry} instances associated to root words.
   */
  protected Map words = new HashMap();
  /**
   * Map of <root entry, collection of entries with entry as root> pairs.
   */
  protected Map reverseRoots = new HashMap();

  /**
   * Create a new WordList instance.
   */
  public WordList()
  {}

  public int getHomos()
  {
    return homos;
  }
  
  public void setDictionaries(Dictionaries dicts)
  {
    this.dicts = dicts;
  }

  public Dictionaries getDictionaries()
  {
    return dicts;
  }

  /**
   * Add the given {@link DictEntry} instance.
   * @param entry the new DictEntry
   */
  public void add(DictEntry entry)
  {
    addEntry(words, entry);
  }

  /**
   * Remove and return the {@link DictEntry} instance belonging to
   * <code>word</code> if any.
   * @param word the word to remove
   */
  public DictEntry remove(String word)
  {
    String loword = word.toLowerCase();
    Object value = words.get(loword);
    if (value != null)
    {
      if (value instanceof DictEntry)
      {
        DictEntry entry = (DictEntry)value;
        if (entry.admitCapitalization(word))
        {
          words.remove(loword);
          return entry;
        }
      }
      else
      {
        DictEntry entry = (DictEntry)((Map)value).get(word);

        if (entry != null && entry.admitCapitalization(word))
        {
          ((Map)value).remove(word);
          return entry;
        }
      }
    }
    return null;
  }

  /**
   * Return the number of root words.
   * @return the number of root words stored so far
   */
  public int size()
  {
    return words.size();
  }

  /**
   * Return the {@link DictEntry} associated with <code>word</code> or
   * <code>null</code>.
   * @param word the root word
   * @return the {@link DictEntry} associated with <code>word</code> or
   * <code>null</code>.
   */
  public DictEntry get(String word)
  {
    return get(word, word.toLowerCase());
  }

  public DictEntry get(String word, String lowerCaseWord)
  {
    Object value = words.get(lowerCaseWord);
    if (value != null)
    {
      if (value instanceof DictEntry)
      {
        DictEntry entry = (DictEntry)value;
        if (entry.admitCapitalization(word))
        {
          return entry;
        }
        return null;
      }
      else
      {
        Iterator it = ((Map)value).values().iterator();

        DictEntry nearest = null;
        byte nearestCap = 0;
        while (it.hasNext())
        {
          DictEntry entry = (DictEntry)it.next();
          if (entry.admitCapitalization(word))
          {
            byte cap = entry.getCapitalization();
            if (nearest == null || nearestCap < cap)
            {
              nearest = entry;
              nearestCap = cap;
            }
          }
        }
        return nearest;
      }
    }
    else
    {
      return null;
    }
  }

  /**
   * Return the object associated with <code>lowerCaseWord</code>
   * @param lowerCaseWord the word in all lowercase
   * @return the object associated with <code>lowercaseWord</code>
   */
  public Object rawGet(String lowerCaseWord)
  {
    return words.get(lowerCaseWord);
  }

  /**
   * Return the map of words stored in this instance. 
   * @return the map of words 
   */  
  public Map getWords()
  {
    return words;
  }

  /**
   * Add the given {@link DictEntry} instance to the given map.
   * @param map the map entry is to be put into
   * @param entry the new DictEntry
   */
  protected void addEntry(Map map, DictEntry entry)
  {
    String key = entry.getWord().toLowerCase();
    Object old = map.get(key);

    DictEntry oldEntry;
    // if there is an entry with same lowercase word
    if (old != null)
    {
      if (old instanceof DictEntry)
      {
        oldEntry = (DictEntry)old;
        if (oldEntry.getWord().equals(entry.getWord()))
        {
          oldEntry.addHomonym(entry);
          homos++;       
        }
        else
        {
          Map subMap = new HashMap();
          map.put(key, subMap);
          subMap.put(oldEntry.getWord(), oldEntry);
          subMap.put(entry.getWord(), entry);
        }
      }
      else // subMap already
      {
        Map subMap = (Map)old;        
        if ((oldEntry = (DictEntry)subMap.get(entry.getWord())) != null)
        {
          oldEntry.addHomonym(entry);
          homos++;          
        }
        else
        {
          subMap.put(entry.getWord(), entry);
        }
      }
    }
    else
    {
      map.put(key, entry);
    }
  }

  public Collection getReverseRoots(DictEntry entry)
  {
    return (Collection)reverseRoots.get(entry);
  }

  /**
   * This method does nothing. It is called by readers after all entries
   * has been added to this list. 
   */
  public void sync()
  {}

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
    return words.size() + " words";
  }

  public String toString()
  {
    return shorten(getClass()) + "[" + contentString() + "]";
  }
}