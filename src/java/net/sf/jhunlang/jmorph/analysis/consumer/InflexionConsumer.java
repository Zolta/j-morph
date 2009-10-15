package net.sf.jhunlang.jmorph.analysis.consumer;

import java.util.Iterator;

import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.analysis.AnalysisEntry;

public class InflexionConsumer extends AffixConsumer
{
  public InflexionConsumer(X x)
  {
    super(x);
  }
  
  /**
   * Parent is null. If lazy then continueStemming will be called.
   */
  public boolean continueStemming(String word, DictEntry entry)
  {
    if (entry.hasFlag(affix))
    {
      analyses.add(new AnalysisEntry(entry, affix));
      if (depth <= level)
      {
        return false;
      }
      else if (lazy)
      {
        addcase(affix);
        return super.continueStemming(word, entry);
      }
      else
      {
        return true;
      }
    }
    else if (lazy)
    {
      return super.continueStemming(word, entry);
    }
    else
    {
      return true;
    }
  }

  /**
   * Called if the same set of stem candidates
   */
  public boolean continueStemming(String key)
  {
    Iterator it = entries.iterator();
    while (it.hasNext())
    {
      DictEntry entry = (DictEntry)it.next();
      if (entry.hasFlag(affix))
      {
        analyses.add(new AnalysisEntry(entry, affix));
        if (depth <= level)
        {
          return false;
        }
        addcase(affix);
      }
    }
    return true;
  }
}