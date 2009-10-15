package net.sf.jhunlang.jmorph;

import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

/**
 * Dict represents hunspell dictionaries.
 *
 * CHANGES:
 * <ul>
 *   <li>01/Jun/2004:<br>
 *     exceptional handling of words ending with y added (@link #ys} and
 *     {@link #ijs}
 *   </li>
 * </ul>
 */
public class Dict extends WordList
{
  /**
   * Stores entries for words flagged with 'f'.
   */
  protected Collection ys = new LinkedList();
  /**
   * Stores forbidden words ending with i or j - they are
   * the candidates for resolving words in ys.
   */
  protected Map ijs = new HashMap();
  /**
   * Number of exceptional words in {@link WordList#words}
   */
  protected int exceptionals;
  /**
   * Stores {@link ExceptionEntry} instances solving exceptional words.
   */
  protected Map exceptions;

  /**
   * Create a new Dict instance
   */
  public Dict()
  {
    super();
    exceptions = new HashMap();
  }

  /**
   * Add the given {@link DictEntry} instance. If entry is an {@link ExceptionEntry}
   * then add it to the exceptional words, put it into the root words otherwise. 
   * @param entry the new DictEntry
   */
  public void add(DictEntry entry)
  {
    if (entry instanceof ExceptionEntry)
    {
      add((ExceptionEntry)entry);
    }
    else
    {
      addEntry(words, entry);
      if (entry instanceof XWordEntry)
      {
        exceptionals++;
      }
    }
  }

  /**
   * Add the given {@link ExceptionEntry} instance the exceptional words.
   * @param entry the new ExceptionEntry
   */
  public void add(ExceptionEntry entry)
  {
    addEntry(exceptions, entry);
  }

  /**
   * Synchronize exceptional solutions.
   * Set flags of each ExceptionEntry to that of its solution and
   */
  public void sync()
  {
    Iterator it = exceptions.values().iterator();
    while (it.hasNext())
    {
      ExceptionEntry entry = (ExceptionEntry)it.next();
      DictEntry flagEntry = get(entry.getWord());
      entry.setFlagEntry(flagEntry);
    }

    it = ys.iterator();
    while (it.hasNext())
    {
      DictEntry entry = (DictEntry)it.next();
      String word = entry.getWord();

      String w = word.substring(0, word.length() - 1);
      String s = w + "i";
    
      DictEntry solution = (DictEntry)ijs.get(s);
      if (solution == null)
      {
        s = w + "j";
        solution = (DictEntry)ijs.get(s);
      }

      if (solution != null)
      {
        remove(s);
        remove(entry.getWord());
        add(new DictEntry(word, solution.getFlagString().toCharArray()));
      }
    }
  }

  /**
   * Return the number of exceptional root words
   * @return the number of exceptional root words stored so far
   */
  public int exceptionalSize()
  {
    return exceptionals;
  }

  /**
   * Return the number of exceptional solution words.
   * @return the number of exceptional solutions stroed so far
   */
  public int exceptionSize()
  {
    return exceptions.size();
  }

  /**
   * Return the {@link ExceptionEntry} solving the word of the given entry if
   * it is an exceptional word that is if the given entry is an
   * {@link XWordEntry} instance. If entry is not an {@link XWordEntry} or
   * it has no solution then return null.
   * @param entry the entry the word of which is to be solved
   * @return the {@link ExceptionEntry} solving word if it is exceptional or
   * null
   */
  public DictEntry exception(DictEntry entry)
  {
    if (entry instanceof XWordEntry)
    {
      XWordEntry xentry = (XWordEntry)entry;
      if (xentry.getChunk() == 0)
      {
        return (ExceptionEntry)exceptions.get(entry.getWord());
      }
      else
      {
        return xentry;
      }       
    }
    return null;
  }

  public void addY(DictEntry entry)
  {
    ys.add(entry);
  }
  
  public void putIJ(String word, DictEntry entry)
  {
    ijs.put(word, entry);
  }

  public String contentString()
  {
    return super.contentString() + ", " +
      exceptionals + " exceptionals, " + exceptions.size() + " exceptions";
  }
}
