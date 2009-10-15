package net.sf.jhunlang.jmorph.analysis;

import java.util.Iterator;
import java.util.Map;

import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.Dictionaries;
import net.sf.jhunlang.jmorph.analysis.consumer.AnalysisConsumer;

/**
 * Homonyms is a utility class for Analyser implementations to
 * add DictEntry's along with their homonyms to stem collections.
 * The Homonyms name became deprecated: THERE IS NO NEED to handle homonyms
 * AT ALL - as the dictionary resolution gives back homomyms as well, all
 * homonyms appear as entries!
 */
public abstract class Homonyms
{
  /**
   * The dictionaries
   */
  protected Dictionaries dictionary;

  public boolean homonyms(String word, String lowerCaseWord,
      AnalyserContext context, AnalysisConsumer consumer)
  {
    return homonyms(word, lowerCaseWord, context, consumer, false);
  }
  
  public boolean homonyms(String word, String lowerCaseWord,
      AnalyserContext context, AnalysisConsumer consumer, boolean homonyms)
  {
    if (context.getRoot().homonyms())
    {
      Object o = dictionary.rawGet(lowerCaseWord);
      if (o != null)
      {
        if (o instanceof DictEntry)
        {
          if (dictEntry(word, (DictEntry)o, consumer, homonyms))
          {
            return true;
          }
        }
        else // o is map of entries
        {
          Iterator it = ((Map)o).values().iterator();
          while (it.hasNext())
          {
            if (dictEntry(word, (DictEntry)it.next(), consumer, homonyms))
            {
              return true;
            }
          }
        }
      }
    }
    return context.getRoot().compoundEnabled ?
      context.getBase().getCompoundAnalyser().
        compound(word, lowerCaseWord, context, consumer) : false;
  }

  protected boolean dictEntry(
    String word, DictEntry entry, AnalysisConsumer consumer, boolean homonyms)
  {
    if (!entry.admitCapitalization(word))
    {
      if (consumer.ignoreCase() && entry.admitLowerCapitalization(word))
      {
        return addHomonyms(word, entry, consumer, homonyms);
      }
      else
      {
        return false;
      }
    }
    else
    {
      return addHomonyms(word, entry, consumer, homonyms);
    }
  }

  protected boolean addHomonyms(
    String word, DictEntry entry, AnalysisConsumer consumer, boolean homonyms)
  {
    if (!consumer.continueStemming(word, entry))
    {
      return true;
    }
    Iterator it = entry.getHomonyms().iterator();
    while (it.hasNext())
    {
      DictEntry hom = (DictEntry)it.next();
      if (!consumer.continueStemming(word, hom))
      {
        return true;
      }
    }
    return false;
  }
}