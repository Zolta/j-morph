package net.sf.jhunlang.jmorph.analysis;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;


import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.Rules;
import net.sf.jhunlang.jmorph.analysis.consumer.BasicConsumer;
import net.sf.jhunlang.jmorph.analysis.consumer.AnalysisConsumer;

/**
 * TODO do not split numbers (i.e. if charAt(at) and charAt and charAt(at + 1)
 * are both numbers than continue)
 */
public class CompoundAnalyserImpl implements CompoundAnalyser
{
  /**
   * Analyser used to generate component candidates
   */
  protected Analyser stemmer;
  /**
   * The rules of stemmer
   */
  protected Rules rules;
  /**
   * Tells check legality of decompositions
   */
  protected boolean dontCheckCompound = Boolean.getBoolean(DONT_CHECK);
  /**
   * The compound controller used for checking the legality of decomposition and
   * for creating compound entries
   */
  protected CompoundController controller;
  /**
   * Maximum number of components as configured in compound control
   */
  protected int maxWord;
  /**
   * Minimum length of components as configured in compound control
   */
  protected int minWord;
  /**
   * If find all possible decompositions as depth of control tells to
   */
  protected boolean all;
  /**
   * The current maximum number of components
   */
  protected int wordNum;
  /**
   * The current depth of compound stem recursion
   */
  protected int compoundDepth;
  /**
   * Caches for collections of compound decompositions to
   * at least 2 and at most wordNum components hashed by word and
   * wordNum being the cache index
   */
  protected Cache[][] compoundCache;
  /**
   * Caches for collections of stems either compound or single, 
   * hashed by word and wordNum being the cache index
   */
  protected Cache[][] stemCache;
  /**
   * The current level of compound stemming<ul>
   * <li>level 0 means no compound on substem</li>
   * <li>level 1 means compound on left substem (left added to right)</li>
   * <li>level 2 means all</li>
   * </ul>
   */
  protected int compoundLevel;

  /**
   * Create a new <code>CompoundAnalyserImpl</code> instance using
   * <code>stemmer</code>.
   * @param stemmer
   */
  public CompoundAnalyserImpl(Analyser stemmer)
  {
    this.stemmer = stemmer;
    rules = stemmer.getRules();
    controller = rules.getCompoundController();
  }

  /**
   * Set {@link #compoundLevel} to the given value.
   * @param compoundLevel the new value of {@link #compoundLevel} 
   */
  public void setLevel(int compoundLevel)
  {
    this.compoundLevel = compoundLevel;
  }

  /**
   * @return return {@link #compoundLevel}.
   */
  public int getLevel()
  {
    return compoundLevel;
  }
  
  /**
   * @see CompoundAnalyser#compound(String, String, AnalyserContext, AnalysisConsumer)
   */
  public boolean compound(String word, String lowerCaseWord,
    AnalyserContext context, AnalysisConsumer consumer)
  {
    context = context.getRoot();
    if (compoundDepth == 0)
    {
      init(context);
      wordNum = maxWord;
    }
    return doCompound(word, lowerCaseWord, context, consumer);
  }

  protected boolean doCompound(String word, String lowerCaseWord,
    AnalyserContext context, AnalysisConsumer consumer)
  {
    if (wordNum < 2)
    {
      return false;
    }

    int l = word.length();
    if (l < minWord * 2)
    {
      return false;
    }

    compoundDepth++;

    Collection c = compoundCache[compoundLevel][wordNum].getCollection(word);
    if (c != null)
    {
      compoundDepth--;
      Iterator it = c.iterator();
      while (it.hasNext())
      {
        DictEntry entry = (DictEntry)it.next();
        if (!consumer.continueStemming(entry.getWord(), entry))
        {
          return true;
        }
      }
      return false;
    }

    int oldWordNum = wordNum;

    RightConsumer rightConsumer =
      new RightConsumer(word, lowerCaseWord, context, consumer);

    boolean b = compound(rightConsumer, l);
    // restore depth and wordNum, cache decompoisitons
    compoundDepth--;
    wordNum = oldWordNum;
    compoundCache[compoundLevel][wordNum].
      putCollection(word, rightConsumer.collector);
    return b;
  }

  // try to decompose word to 2 components at every inner position
  protected boolean compound(RightConsumer rightConsumer, int l)
  {
    //return compound(l - minWord, minWord);
    return compound(rightConsumer, minWord, l - minWord);
  }
  
  // enumerate all possible split points and call <code>rightConsumer</code>
  // until <code>true</code> returned.
  protected boolean compound(RightConsumer rightConsumer, int from, int to)
  {
    //for(at = from; at >= to; at--)
    for(int at = from; at <= to; at++)    
    {
      if (rightConsumer.compound(at))
      {
        return true;
      }
    }
    return rightConsumer.done();
  }
  
  protected class RightConsumer extends BasicConsumer
  {
    /**
     * The parent consumer of this consumer.
     */
    protected AnalysisConsumer consumer;
    /**
     * The context used for stemming the right component
     */
    protected AnalyserContext rightContext;
    /**
     * The context used for stemming the left component
     */
    protected AnalyserContext leftContext;
    /**
     * The word to decompose
     */
    protected String word;
    /**
     * The all lowercase version of word
     */
    protected String lowerCaseWord;
    /**
     * Tells if right component is in the last position 
     */
    protected boolean last;
    /**
     * The consumer for decomposition to left + right
     */
    protected LeftConsumer leftConsumer;
   
    // endindex of left component in word
    protected int leftIndex;
    // start index of right component in word
    protected int rightIndex;
    // tells if this consumer found enough decompositions
    protected boolean rightDone;
    // collect <left, right> compound decompositions
    protected Collection collector = new ArrayList(1);
    // collect decompositions found for right word
    protected Collection rightCollector;

    // set this as parent consumer of rightcontext
    public RightConsumer(String word, String lowerCaseWord,
      AnalyserContext context, AnalysisConsumer consumer)
    {
      this.word = word;
      this.lowerCaseWord = lowerCaseWord;
      this.consumer = consumer;

      --wordNum;

      rightContext = context.getRightContext();
      leftContext = context.getLeftContext();
      
      leftConsumer = new LeftConsumer();
      
      AnalyserContext rightSub = rightContext.getSubContext();
      
      if (rightSub != null)
      {
        rightSub.getConsumerManager().setParentConsumer(this);
        leftContext.getSubContext().
          getConsumerManager().setParentConsumer(leftConsumer);
      }

      last = compoundDepth == 1;
    }

    // use stemCache for right word
    protected boolean compound(int at)
    {
      if (Character.getType(word.charAt(at)) == Character.DASH_PUNCTUATION)
      {
        rightIndex = at + 1;
      }
      else
      {
        rightIndex = at;
      }
      
      String w = word.substring(rightIndex);
      if (w.length() < minWord)
      {
        return false;
      }
      
      leftIndex = at;
      
      Collection c = stemCache[compoundLevel][wordNum].getCollection(w);

      if (c != null)
      {
        rightCollector = null;
        Iterator it = c.iterator();
        while (it.hasNext())
        {
          DictEntry entry = (DictEntry)it.next();
          // adjust wordNum to the length of entry!
          int oldWordNum = wordNum;
          wordNum = wordNum + 1 - entry.length();
          boolean b = !continueStemming(entry.getWord(), entry);
          // restore wordNum
          wordNum = oldWordNum;
          if (b)
          {
            return true;
          }
        }
      }
      else
      {
        String lw = lowerCaseWord.substring(rightIndex);
        // as right is not recursive on level 1, do not cache
        rightCollector =
          compoundLevel == COMPOUND_LEVEL_LEFT ? null : new ArrayList(1);

        rightContext.getRoot().compoundEnabled =
          compoundLevel == COMPOUND_LEVEL_ALL;
        
        boolean b = stemmer.subanalyse(w, lw, rightContext, this) && !all;

        if (rightCollector != null)
        {
          stemCache[compoundLevel][wordNum].putCollection(w, rightCollector);
        }

        if (b)
        {
          return b;
        }
      }
      return done();
    }

    // return if continue stemming;
    // call substem on left with a new LeftConsumer with right
    // leftconsumer will pass compound dict entries to <code>consumer</code>
    public boolean continueStemming(String rightWord, DictEntry right)
    {
      if (rightCollector != null)
      {
        rightCollector.add(right);
      }

      if (dontCheckCompound ||
          (last ? controller.compoundLast(right) :
                  controller.compoundInner(right)))
      {
        String w = word.substring(0, leftIndex);

        leftConsumer.right = right;
        leftConsumer.leftDone = false;

        Collection c = stemCache[compoundLevel][wordNum].getCollection(w);

        if (c != null)
        {
          leftConsumer.leftCollector = null;
          Iterator it = c.iterator();
          while (it.hasNext())
          {
            DictEntry entry = (DictEntry)it.next(); // left entry for w
            if (!rightDone)
            {
              leftConsumer.continueStemming(entry.getWord(), entry);
              if (leftConsumer.leftDone && !all)
              {
                rightDone = true;
                return false;
              }
            }
          }
        }
        else
        {
          String lw = lowerCaseWord.substring(0, leftIndex);
          leftConsumer.leftCollector = new ArrayList(1);
          // set compoundEnabled only if compoundLevel is not 0
          leftContext.getRoot().compoundEnabled =
            compoundLevel > COMPOUND_LEVEL_TOP;

          boolean b =
            stemmer.subanalyse(w, lw, leftContext, leftConsumer) && !all;

          stemCache[compoundLevel][wordNum].
            putCollection(w, leftConsumer.leftCollector);

          if (b)
          {
            rightDone = true;
            return false;
          }
        }
      }         
      return true;
    }

    public boolean done()
    {
      return !all && rightDone;
    }

    protected class LeftConsumer extends BasicConsumer
    {
      protected DictEntry right;
      protected boolean leftDone;
      // collect stems for left
      protected Collection leftCollector;

      public boolean continueStemming(String leftWord, DictEntry left)
      {
        if (leftCollector != null)
        {
          leftCollector.add(left);
        }
        if (dontCheckCompound || controller.compound(left, right))
        {
          DictEntry entry = controller.createCompound(rules, left, right);
          collector.add(entry);
          if (!leftDone &&
              !consumer.continueStemming(entry.getWord(), entry) &&
              !all)
          {
            leftDone = true;
          }
        }
        // return true for collecting all entries for cache
        return true;
      }

      public boolean done()
      {
        return !all && leftDone;
      }
    }
  }

  /**
   * Create and return the cache. The cache contains an array of caches for
   * each possible value of {@link #compoundLevel}. These array of caches
   * contain caches for each possible "number of component words".
   * @param num the max. number of component words + 1
   */
  private Cache[][] newCache(int num)
  {
    Cache[][] acache = new Cache[COMPOUND_LEVEL_ALL + 1][num];
    for(int i = 0; i <= COMPOUND_LEVEL_ALL; i++)
    {
      for(int j = 0; j < num; j++)
      {
        acache[i][j] = new Cache();
      }
    }
    return acache;
  }

  /**
   * Initialize this compound stemmer with <code>context</code>
   * @param context the stemming context to initialize with
   */
  private void init(AnalyserContext context)
  {
    if (stemCache == null)
    {
      AnalyserControl control = context.getControl();

      all = control.getDepth() >= AnalyserControl.ALL_COMPOUNDS;

      CompoundControl compoundControl = control.getCompoundControl();

      minWord = compoundControl.getMin();
      maxWord = compoundControl.getMax();

      stemCache = newCache(maxWord + 1);
      compoundCache = newCache(maxWord + 1);
    }
  }
}
