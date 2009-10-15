package net.sf.jhunlang.jmorph.analysis.consumer;

import java.util.Collection;

import net.sf.jhunlang.jmorph.DictEntry;

public class BasicConsumer implements AnalysisConsumer
{
  /**
   * The parent consumer.
   */
  protected AnalysisConsumer parent;
  /**
   * The current stemming level
   */
  protected int level;
  /**
   * The configured depth of analysis
   */
  protected int depth;
  
  protected boolean frozen;

  protected Collection analyses;

  public void setDepth(int depth)
  {
    this.depth = depth;
  }

  public void setLevel(int level)
  {
    if (!frozen)
    {
      this.level = level;
    }
  }

  public void freezeLevel(int level)
  {
    setLevel(level);
    frozen = true;
  }

  public void thawLevel()
  {
    frozen = false;
  }
  
  public AnalysisConsumer setParentConsumer(AnalysisConsumer parent)
  {
    AnalysisConsumer old = this.parent;
    this.parent = parent;
    return old;
  }

  public void setStems(Collection stems)
  {
    this.analyses = stems;
  }

  public Collection getStems()
  {
    return analyses;
  }

  public boolean done()
  {
    return parent == null ?
      (depth <= level && analyses.size() > 0) : parent.done();
  }

  /**
   * Return if stemming should be continued.
   */
  public boolean continueStemming(String word, DictEntry entry)
  {
    return depth >= level;
  }

  public boolean ignoreCase()
  {
    return false;
  }
  
  public boolean hasSuffixFlag(int flag)
  {
    return false;
  }

  public AnalysisConsumer getParent()
  {
    return parent;
  }
}