package net.sf.jhunlang.jmorph.analysis.consumer;

import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.analysis.AnalysisEntry;

/**
 * AnalysisConsumer implementation for root context
 */
public class DictConsumer extends BasicConsumer
{
  /**
   * Return if stemming should be continued.
   */
  public boolean continueStemming(String word, DictEntry entry)
  {
    //stems.add(parent == null ? (Object)new AnalysisEntry(entry) : (Object)entry);
    if (parent != null)
    {
      return parent.continueStemming(word, entry);
    }
    else
    {
      analyses.add(new AnalysisEntry(entry));
      return depth > level;
    }
  }

  public boolean done()
  {
    return super.done();
  }
}