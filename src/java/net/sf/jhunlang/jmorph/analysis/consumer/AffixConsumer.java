package net.sf.jhunlang.jmorph.analysis.consumer;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;

import net.sf.jhunlang.jmorph.AffixEntry;
import net.sf.jhunlang.jmorph.DictEntry;

/**
 * AnalysisConsumer implementation for root context
 */
public abstract class AffixConsumer extends BasicConsumer
{
  protected boolean lazy;

  protected AffixEntry affix;

  protected List entries;
  protected String word;

  protected Collection cases = new HashSet();
  
  protected X x;
  
  public AffixConsumer(X x)
  {
    this.x = x;
  }

  public void clearEntries(String word)
  {
    this.word = word;
    if (entries == null)
    {
      entries = new ArrayList(1);
    }
    else
    {
      entries.clear();
    }
  }

  public void clearCases()
  {
    if (cases == null)
    {
      cases = new HashSet(1);
    }
    else
    {
      cases.clear();
    }
  }

  public void addcase(AffixEntry affix)
  {
    cases.add(x.getAffixIdentifier(affix));
  }
  
  public boolean affixDone(AffixEntry affix)
  {
    return getCases().contains(x.getAffixIdentifier(affix));
  }
  
  public void setAffix(AffixEntry affix, boolean lazy)
  {
    this.affix = affix;
    this.lazy = lazy;
  }

  public AffixEntry getAffix()
  {
    return affix;
  }

  public Collection getCases()
  {
    return cases;
  }

  /**
   * Collect entries
   */
  public boolean continueStemming(String word, DictEntry entry)
  {
    entries.add(entry);
    return true;
  }

  public abstract boolean continueStemming(String key);

  public String toString()
  {
    StringBuffer sb = new StringBuffer("AC[" + affix);
    if (parent != null)
    {
      sb.append(" of " + parent); 
    }
    sb.append("]");
    return sb.toString();
  }

  public boolean done()
  {
    return super.done();
  }
}