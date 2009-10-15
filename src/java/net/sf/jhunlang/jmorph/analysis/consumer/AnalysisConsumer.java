package net.sf.jhunlang.jmorph.analysis.consumer;

import java.util.Collection;

import net.sf.jhunlang.jmorph.DictEntry;

public interface AnalysisConsumer
{
  boolean continueStemming(String word, DictEntry entry);
  boolean done();

  void setStems(Collection stems);
  Collection getStems();
  boolean hasSuffixFlag(int flag);
  boolean ignoreCase();
  AnalysisConsumer getParent();
  AnalysisConsumer setParentConsumer(AnalysisConsumer consumer);
  void freezeLevel(int level);
  void thawLevel();
}