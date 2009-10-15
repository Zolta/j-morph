package net.sf.jhunlang.jmorph.analysis;

import net.sf.jhunlang.jmorph.AffixEntry;
import net.sf.jhunlang.jmorph.AffixEntries;
import net.sf.jhunlang.jmorph.Rules;
import net.sf.jhunlang.jmorph.analysis.consumer.ConsumerManager;
import net.sf.jhunlang.jmorph.analysis.consumer.InflexionConsumerManager;
import net.sf.jhunlang.jmorph.util.BooleanResults;


/**
 * AnalyserContext is the context of analysis.
 * The toplevel context stands for inflexion while subContexts represent
 * derivations. The toplevel context provides two more top level contexts,
 * left and right, for analysing compound words.
 * The {@link #base} is the toplevel context where the analysis starts.
 * The {@link #root} is the toplevel inflexion context,
 * {@link #leftContext} and {@link #rightContext} are toplevel contexts.
 * All the three share the same base context.
 */
public class AnalyserContext
{
  /**
   * Convenience constant for the maximum allowed word length.
   */
  public final static int MAX_WORD_LENGTH = 64;

  protected boolean compoundEnabled;
  /**
   * Tells if analyser should do dictionary lookup.
   */
  protected boolean homonyms = true;
  /**
   * Cache for {@link AffixEntry#checkStem(java.lang.CharSequence)} results.
   */
  protected BooleanResults conditionResults;
  /**
   * The rules.
   */
  protected Rules rules;
  /**
   * The control of this context.
   */
  protected AnalyserControl control;
  /**
   * Manager of analysis consumers and affix cache.
   */
  protected ConsumerManager consumerManager;
  /**
   * The current level of analyse on this context.
   */
  protected int level;
  /**
   * The sub context level of this context
   */
  protected int recursionLevel;
  /**
   * The sub context.
   */
  protected AnalyserContext subContext;
  /**
   * If prefix is sought for in the current analysis.
   */
  protected boolean suffix;
  /**
   * The root context of this analyser context.
   */
  protected AnalyserContext root;
  /**
   * The base context. Root is the inflexion top context while
   * base is the starting toplevel context.
   */
  protected AnalyserContext base;
  /**
   * The context for compound right.
   */
  protected AnalyserContext rightContext;
  /**
   * The context for compound left.
   */
  protected AnalyserContext leftContext;
  /**
   * The compound analyser.
   */
  protected CompoundAnalyser compoundAnalyser;
  /**
   * Tells if analyser should try shortest suffix first.
   */
  protected boolean shortestSuffixFirst;
  /**
   * Tells if analyser should try shortest prefix first.
   */
  protected boolean shortestPrefixFirst;
  /**
   * Tells if analyser should try to cut off affix before
   * compound decomoposition.
   */
  protected boolean affixFirst;

  /**
   * Create a new AnalyserContext with a new {@link AnalyserControl} specifying
   * a default {@link AnalyserControl}.
   */
  public AnalyserContext()
  {
    this(new AnalyserControl());
  }

  /**
   * Create a new  AnalyserContext for <code>control</code>
   */
  public AnalyserContext(AnalyserControl control)
  {
    this(control, control.getCompoundControl().getMax(), null);
  }

  protected AnalyserContext(
    AnalyserControl control, int compound, AnalyserContext base)
  {
    this.control = control;
    this.base = base == null ? this : base;

    if (--compound > 0)
    {
      leftContext = new AnalyserContext(control, compound, this.base);
      rightContext = new AnalyserContext(control, compound, this.base);
    }
  }

  public CompoundAnalyser getCompoundAnalyser()
  {
    return compoundAnalyser;
  }

  public void setHomonyms(boolean b)
  {
    homonyms = b;
  }

  public boolean homonyms()
  {
    return homonyms;
  }

  public synchronized void follow(Rules rules, Analyser analyser)
  {
    if (root != null && root != this)
    {
      root.follow(rules, analyser);
    }
    else if (follow(rules, this, 0))
    {
      compoundAnalyser = new CompoundAnalyserImpl(analyser);
      if (leftContext != null)
      {
        leftContext.follow(rules);
        rightContext.follow(rules);
      }
    }
  }

  protected void follow(Rules rules)
  {
    if (follow(rules, this, 0))
    {
      if (leftContext != null)
      {
        leftContext.follow(rules);
        rightContext.follow(rules);
      }
    }
  }

  protected synchronized boolean follow(
    Rules rules, AnalyserContext root, int rl)
  {
    if (rules != this.rules)
    {
      this.rules = rules;
      this.root = root;
      
      conditionResults = new BooleanResults(rules.getConditionsMap().size());

      recursionLevel = rl;

      consumerManager = this == root ?
        new InflexionConsumerManager(control) : new ConsumerManager(control);

      if (rules.hasSubRules())
      {
        AnalyserControl subControl = new AnalyserControl(
          getControl().getCompoundControl(), AnalyserControl.ALL_COMPOUNDS);

        // pass 0 as compound for not creating left, right on subContext
        subContext = new AnalyserContext(subControl, 0, base);
        subContext.follow(rules.getSubRules(), root, recursionLevel + 1);
        consumerManager.setSubManager(subContext.getConsumerManager());
      }
      return true;
    }
    return false;
  }

  public BooleanResults getConditionResults()
  {
    return conditionResults;
  }

  public ConsumerManager getConsumerManager()
  {
    return consumerManager;
  }

  public int getRecursionLevel()
  {
    return recursionLevel;
  }

  public AnalyserContext getRoot()
  {
    return root;
  }

  public AnalyserContext getBase()
  {
    return base;
  }

  /**
   * Set and return current stemming level.
   * @param level the current stemming level
   * @return the new stemming level 
   */
  public int setLevel(int level)
  {
    consumerManager.setLevel(level);
    return this.level = level;
  }

  /**
   * Tell if this is a toplevel context.
   * @return if this is a toplevel context 
   */
  public boolean top()
  {
    return recursionLevel == 0;
  }

  /**
   * Return the subcontext if any.
   * @return the subcontext
   */
  public AnalyserContext getSubContext()
  {
    return subContext;
  }

  /**
   * Return the right context for compound stemming if any.
   * @return the right context
   */
  public AnalyserContext getRightContext()
  {
    return rightContext;
  }

  /**
   * Return the left context for compound stemming if any.
   * @return the right context
   */
  public AnalyserContext getLeftContext()
  {
    return leftContext;
  }

  /**
   * Return the current stemming level.
   * @return the current stemming level
   */
  public int getLevel()
  {
    return level;
  }

  /**
   * Set <code>suffix</code to <code>b</code> and set suffix to
   * <code>b</code> on <code>consumerManager</code> appropriately.
   * @return the old value of <code>suffix</code>
   */
  public boolean setSuffix(boolean b)
  {
    boolean old = suffix;
    suffix = b;
    consumerManager.setSuffix(suffix);
    return old;
  }

  /**
   * Return if analyser should try with shortest affix first.
   * Return {@link #shortestSuffixFirst} or  {@link #shortestPrefixFirst}
   * depending whether or not {@link #suffix} is <code>true</code>.
   * @return {@link #shortestSuffixFirst} or  {@link #shortestPrefixFirst}
   * depending whether or not {@link #suffix} is <code>true</code>.
   */
  public boolean shortestAffixFirst()
  {
    return suffix ? shortestSuffixFirst : shortestPrefixFirst;
  }

  /**
   * Return if analyser should try to cut off affix before compound
   * compound decomposition.
   * @return if analyser should try to cut off affix before compound
   * decomposition
   */
  public boolean affixFirst()
  {
    return affixFirst;
  }

  /**
   * Return if the current affix cache is the suffix cache.
   * @return if the current affix cache is the suffix cache
   */
  public boolean suffix()
  {
    return suffix;
  }

  public AffixEntries getAffixEntries()
  {
    return suffix ? rules.getSuffixEntries() : rules.getPrefixEntries();
  }

  public AffixEntries getSuffixEntries()
  {
    return rules.getSuffixEntries();
  }

  public AffixEntries getPrefixEntries()
  {
    return rules.getPrefixEntries();
  }

  /**
   * Return the control of this context.
   * @return the control of this context
   */
  public AnalyserControl getControl()
  {
    return control;
  }

  public String toString()
  {
    return "AnalyserContext[" + control + "]";
  }
  
  /**
   * @param shortestPrefixFirst The shortestPrefixFirst to set.
   */
  public void setShortestPrefixFirst(boolean shortestPrefixFirst)
  {
    this.shortestPrefixFirst = shortestPrefixFirst;
  }
  
  /**
   * @param shortestSuffixFirst The shortestSuffixFirst to set.
   */
  public void setShortestSuffixFirst(boolean shortestSuffixFirst)
  {
    this.shortestSuffixFirst = shortestSuffixFirst;
  }
}