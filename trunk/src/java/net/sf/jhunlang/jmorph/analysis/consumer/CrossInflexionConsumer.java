package net.sf.jhunlang.jmorph.analysis.consumer;

import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.PrefixEntry;
import net.sf.jhunlang.jmorph.SuffixEntry;
import net.sf.jhunlang.jmorph.analysis.AnalysisEntry;

public class CrossInflexionConsumer extends InflexionConsumer
{
  protected SuffixEntry suffix;

  public CrossInflexionConsumer(X x)
  {
    super(x);
  }

  public void setAffixes(PrefixEntry prefix, SuffixEntry suffix)
  {
    super.setAffix(prefix, false);
    this.suffix = suffix;
  }

  /**
   * Return if stemming should be continued.
   * parent MUST BE null.
   */
  public boolean continueStemming(String word, DictEntry entry)
  {
    if (entry.hasFlag(affix) && entry.hasFlag(suffix))
    {
      analyses.add(new AnalysisEntry(entry, (PrefixEntry)affix, suffix));
      return depth > level;
    }
    return true;
  }
}