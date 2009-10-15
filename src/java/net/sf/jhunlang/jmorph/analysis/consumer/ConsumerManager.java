package net.sf.jhunlang.jmorph.analysis.consumer;

import net.sf.jhunlang.jmorph.analysis.AffixCache;
import net.sf.jhunlang.jmorph.analysis.AnalyserContext;
import net.sf.jhunlang.jmorph.analysis.AnalyserControl;

public class ConsumerManager
{  
  protected AnalysisConsumer parentConsumer;

  protected DictConsumer dictConsumer;
  protected AffixConsumer affixConsumer;
  protected CrossInflexionConsumer crossConsumer;

  protected ConsumerManager subManager;

  /**
   * The affix cache of this context
   */
  protected AffixCache affixCache;

  public ConsumerManager(AnalyserControl control)
  {
    this();
    affixConsumer = new DerivationConsumer(control.getX());
    affixConsumer.setDepth(control.getDepth());
  }

  protected ConsumerManager()
  {
    affixCache = new AffixCache(AnalyserContext.MAX_WORD_LENGTH);
  }

  public void setSuffix(boolean b)
  {
    affixCache.setSuffix(b);
  }

  public void setSubManager(ConsumerManager subManager)
  {
    this.subManager = subManager;
  }

  public void setLevel(int level)
  {
    affixConsumer.setLevel(level);
  }

  /**
   * Return the current affix cache as set by {@link #setSuffix(boolean)}.
   * @return the current affix cache
   */
  public AffixCache getAffixCache()
  {
    return affixCache;
  }

  public ConsumerManager getSubManager()
  {
    return subManager;
  }
   
  public DictConsumer getDictConsumer()
  {
    throw new IllegalArgumentException();
  }

  public CrossInflexionConsumer getCrossConsumer()
  {
    throw new IllegalArgumentException();
  }

  //
  public AffixConsumer getAffixConsumer()
  {
    setParent(affixConsumer);
    return affixConsumer;
  }

  protected void setParent(AnalysisConsumer consumer)
  {    
    if (subManager != null)
    {
      subManager.setParentConsumer(consumer);
    }
  }

  public AnalysisConsumer getParentConsumer()
  {
    return parentConsumer;
  }

  /**
   * Set parent consumer. As this method only on subcontext and
   * subcontext has only affix consumer, set parent consumerof
   * affix consumer only.
   * @param consumer the parent consumer to set
   */
  public void setParentConsumer(AnalysisConsumer consumer)
  {
    parentConsumer = consumer;
    affixConsumer.setParentConsumer(consumer);
  }
}