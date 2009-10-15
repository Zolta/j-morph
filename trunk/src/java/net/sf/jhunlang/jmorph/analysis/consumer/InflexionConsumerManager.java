package net.sf.jhunlang.jmorph.analysis.consumer;

import net.sf.jhunlang.jmorph.analysis.AnalyserControl;

public class InflexionConsumerManager extends ConsumerManager
{  
  protected DictConsumer dictConsumer;
  protected CrossInflexionConsumer crossConsumer;

  public InflexionConsumerManager(AnalyserControl control)
  {
    super();
    dictConsumer = new DictConsumer();
    affixConsumer = new InflexionConsumer(control.getX());
    crossConsumer = new CrossInflexionConsumer(control.getX());

    int depth = control.getDepth();
    dictConsumer.setDepth(depth);
    affixConsumer.setDepth(depth);
    crossConsumer.setDepth(depth);
  }

  public DictConsumer getDictConsumer()
  {
    setParent(dictConsumer);
    return dictConsumer;
  }

  public CrossInflexionConsumer getCrossConsumer()
  {
    setParent(crossConsumer);
    return crossConsumer;
  }

  public void setLevel(int level)
  {
    super.setLevel(level);
    dictConsumer.setLevel(level);
    crossConsumer.setLevel(level);
  }

  public void setParentConsumer(AnalysisConsumer consumer)
  {
    super.setParentConsumer(consumer);
    dictConsumer.setParentConsumer(consumer);
    crossConsumer.setParentConsumer(consumer);
  }
}