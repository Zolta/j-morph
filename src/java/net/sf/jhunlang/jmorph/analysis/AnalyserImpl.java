package net.sf.jhunlang.jmorph.analysis;

import java.util.ListIterator;
import java.util.LinkedList;

import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import java.util.ArrayList;

import net.sf.jhunlang.jmorph.AffixEntries;
import net.sf.jhunlang.jmorph.AffixEntry;
import net.sf.jhunlang.jmorph.Dictionaries;
import net.sf.jhunlang.jmorph.PrefixEntry;
import net.sf.jhunlang.jmorph.Rules;
import net.sf.jhunlang.jmorph.SuffixEntry;
import net.sf.jhunlang.jmorph.analysis.consumer.AffixConsumer;
import net.sf.jhunlang.jmorph.analysis.consumer.ConsumerManager;
import net.sf.jhunlang.jmorph.analysis.consumer.CrossInflexionConsumer;
import net.sf.jhunlang.jmorph.analysis.consumer.DerivationConsumer;
import net.sf.jhunlang.jmorph.analysis.consumer.AnalysisConsumer;
import net.sf.jhunlang.jmorph.util.BooleanResults;

/**
 * AnalyserImpl implements <code>stem</code> of {@link Analyser} as follows:
 * <ul> 
 *   <li>check if the dictionary resolves the word</li>
 *   <li>find analyses by suffix rules</li>
 *   <li>find analyses by prefix rules</li>
 *   <li>find analyses by the cross product of crossable rules</li>
 *   <li>find compound and derivation analyses</li>
 * </ul>
 */
public class AnalyserImpl extends Homonyms implements Analyser
{
  /**
   * Convenience constant for where to start cross prefix
   */
  public final static int CROSS_PREFIX_START = 1;
  /**
   * Convenience constant for where to start cross suffix
   */
  public final static int CROSS_SUFFIX_START = 1;

  /**
   * Convenience constant for the default analysis control
   */
  protected AnalyserContext defaultContext;
  /**
   * Convenience constant for the ispell-like analysis control
   */
  protected AnalyserContext istemContext;
  /**
   * Affix and prefix rules
   */
  protected Rules rules;

  /**
   * Create a AnalyserImpl instance using the given rules and dictionaries.
   * @param rules the affix and prefix rules
   * @param dictionary the dictionary
   */
  public AnalyserImpl(Rules rules, Dictionaries dictionary)
  {
    this.rules = rules;
    this.dictionary = dictionary;
  }

  /**
   * Return the dictionary used by this instance
   * @return the dictionary of this analyser
   */
  public Dictionaries getDictionaries()
  {
    return dictionary;
  }

  /**
   * Return the rules used by this instance
   * @return the rules of this analyser
   */
  public Rules getRules()
  {
    return rules;
  }

  /**
   * Call {@link #istem(java.lang.String, AnalyserContext)} with
   * the default context.
   * @param word the word to analyse
   * @return the istem-like result
   */
  public IStem istem(String word)
  {
    return istem(word, ensureIstemContext());
  }

  /**
   * Call {@link #istem(java.lang.String, int, AnalyserContext)}
   * with the default context {@link #istemContext}.
   * @param word the word to analyse
   * @param offset the offset of the word in its line
   * @return the istem-like result
   */
  public IStem istem(String word, int offset)
  {
    return istem(word, offset, ensureIstemContext());
  }

  /**
   * Call {@link #istem(java.lang.String, int, AnalyserContext)}
   * with <code>offset</code> 0.
   * @param word the word to stem
   * @param context the context of analysis
   * @return the istem-like result
   */
  public IStem istem(String word, AnalyserContext context)
  {
    return istem(word, 0, context);
  }

  /**
   * Analyse <code>word</code> and return an {@link IStem} instance
   * representing the result as <i>ispell</i> does in its '-a' mode.
   * <code>offset</code> is the offset of the word in its line. The value
   * of offset is not used at all, only the returned <code>IStem</code>
   * instance echoes it - offset is present for being compatible with istem.
   * @param word the word to stem
   * @param offset the offset of the word in its line if applicable
   * @param context the context of analysis
   * @return the istem-like result
   */
  public IStem istem(String word, int offset, AnalyserContext context)
  {
    Iterator it = analyse(word, context).iterator();
    if (!it.hasNext())    // if no stem found
    {
      return new IStem(word, offset);
    }
    else
    {
      return new IStem(word, (Analysis)it.next());
    }
  }

  /**
   * Return the list of analyses of <code>word</code>.
   * The returned collection contains {@link AnalysisEntry} instances.
   * @param word the to stem
   * @return the collection of analyses found
   * @exception IllegalArgumentException if the word is longer than 
   * {@link AnalyserContext#MAX_WORD_LENGTH}
   * @see #analyse(String, AnalyserContext)
   */
  public List analyse(String word)
  {
    return analyse(word, ensureDefaultContext());
  }

  /**
   * Return the list of analyses of the given word. The returned list
   * contains {@link AnalysisEntry} instances. If <code>word</code>
   * is longer than {@link AnalyserContext#MAX_WORD_LENGTH} then
   * return no analysis.
   * @param word the to analyse
   * @param context the context of stemming
   * @return the list of analyses
   */
  public List analyse(String word, AnalyserContext context)
  {
    synchronized (context)
    {
      context.follow(rules, this);

      boolean compound = context.getControl().getCompoundControl().enabled();

      // collect not compound stems first
      context.compoundEnabled = false;

      AnalysisConsumer consumer =
        context.getRoot().getConsumerManager().getDictConsumer();
      consumer.thawLevel();
      
      consumer.setStems(new ArrayList(1));
      context.setHomonyms(true);

      doAnalysis(word, context, true, consumer);
      // done without compound, check if we are done at this level
      context.setLevel(AnalyserControl.ALL_CROSSES);
      
      // collect compound stems
      if (!consumer.done() && compound)
      {
        CompoundAnalyser compoundStemmer =
          context.getBase().getCompoundAnalyser();
        context.compoundEnabled = true;
        // no homonyms on base context
        context.setHomonyms(false);
        consumer.freezeLevel(AnalyserControl.FIRST_COMPOUND);
        // run compound on max level if all compounds
        if (context.getControl().getDepth() == AnalyserControl.ALL_COMPOUNDS)
        {
          compoundStemmer.setLevel(CompoundAnalyser.COMPOUND_LEVEL_ALL);
          doAnalysis(word, context, true, consumer);
        }
        else
        {
          // run compound until a compound stem found
          for(int i = 0; i <= CompoundAnalyser.COMPOUND_LEVEL_ALL; i++)
          {
            compoundStemmer.setLevel(i);
            doAnalysis(word, context, true, consumer);
            if (consumer.done())
            {
              break;
            }
          }
        }
      }
      return (List)consumer.getStems();
    }
  }

  protected boolean doAnalysis(String word, AnalyserContext context,
    boolean dict, AnalysisConsumer consumer)
  {
    return doAnalysis(word, null, context, dict, consumer);
  }

  /**
   * Perform analysis and return the list of analyses found.
   * The returned list contains {@link AnalysisEntry} instances.
   * <code>context</code> defines the depth of the analysis along
   * with the parameters for compound decomposition. If <code>word</code> is
   * longer than {@link AnalyserContext#MAX_WORD_LENGTH} then return no stem.
   * @param word the to analyse
   * @param lowerCaseWord the word in all lowercase
   * @param context the context of the analysis
   * @param dict tells if the dictionary should sought for <code>word</code>
   * @param consumer the consumer collecting analyses and controlling the
   * analyser process
   * @return if at least one analysis has been found; the analyses are
   * collected by <code>consumer</code> 
   */
  protected boolean doAnalysis(String word, String lowerCaseWord,
    AnalyserContext context, boolean dict, AnalysisConsumer consumer)
  {
    if (word.length() == 0 || word.length() >= AnalyserContext.MAX_WORD_LENGTH)
    {
      return false;
    }

    AffixCache affixCache = context.getConsumerManager().getAffixCache();

    if (lowerCaseWord == null)
    {
      lowerCaseWord = word.toLowerCase();
    }

    context.setLevel(AnalyserControl.FIRST_STEM);

    if (dict)
    {
      // if compound would be switched off here then inflexed forms might be
      // analysed differently like:
      //   honvédelmi to honvédelmi, honvédelem
      // while
      //   honvédelmiEK to honvédelmi, honvédelmi
      // no compound on top for suffix first
      if (context.affixFirst() && context.top() && context.compoundEnabled)
      {
        context.compoundEnabled = false;
        homonyms(word, lowerCaseWord, context, consumer);
        context.compoundEnabled = true;
      }
      else
      {
        homonyms(word, lowerCaseWord, context, consumer);
      }
      if (consumer.done())
      {
        return true;
      }
    }

    affixCache.split(word, lowerCaseWord);

    if (consumer.done())
    {
      return true;
    }

    context.setLevel(AnalyserControl.FIRST_SUFFIX);
    context.setSuffix(true);
    affixCache.clearSuffixCaches(affixCache.getSplit().length/2);  
    affixStem(word, consumer.getStems(), context);
    if (consumer.done())
    {
      return true;
    }

    context.setLevel(AnalyserControl.FIRST_PREFIX);
    context.setSuffix(false);
    affixCache.clearPrefixCaches(affixCache.getSplit().length/2);
    affixStem(word, consumer.getStems(), context);

    if (consumer.done())
    {
      return true;
    }

    // do not cross on derivatives - it would produce duplicate analyses
    // like legpirosabb: leg + pirosabb (pirosabb being suffix derived)
    // and leg + piros + abb (cross-stemming). The difference between top
    // and sub levels is that the level admits only derivative analysis
    // which is the analysis on sublevels only!
    if (context.top())
    {
      context.setLevel(AnalyserControl.FIRST_CROSS);
      crossStem(consumer.getStems(), context);
      if (consumer.done())
      {
        return true;
      }
      // 'substem' the word itself (as substem was called only from affixStem
      // for real parts of the word, uninflexed derivatives and compounds were
      // not found)
      AnalyserContext subContext = context.getSubContext();
      if (subContext != null)
      {
        consumer = context.getConsumerManager().getDictConsumer();
        context.setLevel(AnalyserControl.FIRST_STEM);
        doAnalysis(word, lowerCaseWord, subContext, false, consumer);
      }

      if (consumer.done())
      {
        return true;
      }
      // if homonyms was called with compoundEnabled false, call compound here
      if (context.compoundEnabled && context.affixFirst())
      {
        context.getBase().getCompoundAnalyser().
          compound(word, lowerCaseWord, context, consumer);
      }
    }
    return consumer.done();
  }

  /** 
   * Find analyses of the given word. Use the rules and splits of 
   * <code>context</code>. The even elements of splits are the stem
   * candidates while the odd ones are the append candidates.
   * A stem-candidate is a stem of the word if <ul>
   *   <li>there is a rule in <code>rulemap</code> defining
   *       the append/prepend candidate as its append and</li>
   *   <li>the stem-candidate satisfies the rule and</li>
   *   <li>the dictionary stems the stem-candidate.</li>
   * </ul><p>
   * Analyses found this way are added to <code>analyses</code>.
   * The statisfied rules are added to cache of <code>context</code>,
   * the crossable ones are added to its crossCache.
   * Stop analysing when affixConsumer of <code>context</code> tells to.
   * @param word the word to analyse
   * @param analyses the collection of analyses
   * @param context the analyser context
   */
  protected void affixStem(
    String word, Collection analyses, AnalyserContext context)
  {
    ConsumerManager cm = context.getConsumerManager();

    AffixCache affixCache = cm.getAffixCache();
    String[] splits = affixCache.getSplit();

    int start, until, inc;
    boolean suffix = context.suffix();

    if (suffix)
    {
      start = (splits.length - 1) / 2;
      until = 0;
      inc = -1;
      if (start < until)
      {
        return;
      }
    }
    else
    {
      start = 1;
      until = (splits.length - 1) / 2;
      inc = 1;

      if (start > until)
      {
        return;
      }
    }

    String[] lowerCaseSplits = affixCache.getLowerCaseSplit();

    List[] cache = affixCache.getCache();
    List[] crossCache = affixCache.getCrossableCache();

    BooleanResults conditionResults = context.getConditionResults();

    AffixConsumer consumer = cm.getAffixConsumer();
    consumer.setStems(analyses);

    AffixEntries rulemap = context.getAffixEntries();
    AffixEntries previous = null;
    
    // list of applicable affix maps
    List rulesMap = new LinkedList();

    int ll = 0;
    int i;

    boolean shortestFirst = context.shortestAffixFirst();
    // index of dash; on dash affix do not check affix 
    int dash = -1;
    
    for(i = start; i != until; i += inc)
    {
      char c = lowerCaseSplits[i + i].charAt(suffix ? 0 : ll++);
      if ((rulemap = rulemap.getMap(c)) == null)
      {
        if (previous != null &&
            Character.getType(c) == Character.DASH_PUNCTUATION)
        {
          dash = i;
          if (shortestFirst)
          {
            rulesMap.add(0, previous);
          }
          else
          {
            rulesMap.add(previous);
          }
          i += inc;
        }
        break;
      }
      previous = rulemap;  
      if (shortestFirst)
      {
        rulesMap.add(0, rulemap);
      }
      else
      {
        rulesMap.add(rulemap);
      }
    }

    if (shortestFirst)
    {
      i = start;
      inc = -inc;
    }
    else
    {
      i -= inc;
    }
    
    ListIterator rit = rulesMap.listIterator(rulesMap.size());

    outer: for(; rit.hasPrevious(); i -= inc)
    {
      Map rules = ((AffixEntries)rit.previous()).getByStrip();
      if (rules == null)
      {
        continue;
      }
      
      // check if remainder satisfies any of rules;
      // remainder is the word after append/prepend has been cut off
      int j = i + i + 1;
      String remainder = splits[j];
      String lower = lowerCaseSplits[j];
      
      // tell conditionResults this is a new check round
      conditionResults.nextRound();
      Iterator it = rules.values().iterator();
      while (it.hasNext())
      {
        Object o = it.next();
        if (o instanceof Collection) // multiple rules with the same strip
        {
          boolean sub = false;
          consumer.clearCases();
          // eit runs on rules by strip
          Iterator eit = ((Collection)o).iterator();
          while (eit.hasNext())
          {            
            AffixEntry affix = (AffixEntry)eit.next();
            // do not try an affix with flags'nd case occurring
            // in cases - the analysis wouldn't be new
            if (consumer.affixDone(affix))
            {
              continue;
            }

            if (dash == i || affix.admitStem(lower, conditionResults))
            {
              consumer.setAffix(affix, true);
              // as we're running on affixes with the same strip,
              // stem and entries don't change after strip added to
              // remainder first
              if (!sub)
              {
                sub = true;
                if (affix.same())
                {
                  if (affixSame(word, affixCache.getLowerCaseWord(),
                                context, consumer))
                  {
                    return;
                  }
                }
                else if (dash != i && affix.getStripLength() != 0)
                {
                  String stem = affix.addStrip(remainder);
                  if (affixStem(
                       stem, stem.toLowerCase(), context, consumer))
                  {
                    return;
                  }
                }
                else if (affixStem(remainder, lower, context, consumer))
                {
                   return;
                }
              }
              // affix has changed on consumer!
              else if (!consumer.continueStemming(word))
              {
                return;
              }

              cache[i].add(affix);
              if (context.top() && affix.crossable())
              {
                crossCache[i].add(affix);
              }
            } // end of if affix admits remainder
          }   // end of iterator on rule collection
        }     // end of if collection
        else  // single affix rule
        {
          AffixEntry affix = (AffixEntry)o;
          if (dash == i || affix.admitStem(lower, conditionResults))
          {
            consumer.setAffix(affix, false);
            if (affix.same())
            {
              if (affixSame(word, affixCache.getLowerCaseWord(),
                            context, consumer))
              {
                return;
              }
            }
            else if (dash != i && affix.getStripLength() != 0)
            {
              // add strip to remainder to get stem
              String stem = affix.addStrip(remainder);
              if (affixStem(stem, stem.toLowerCase(), context, consumer))
              {
                return;
              }
            }
            else if (affixStem(remainder, lower, context, consumer))
            {
              return;
            }

            cache[i].add(affix);
            // recall that crossStem is called only on top context
            if (context.top() && affix.crossable())
            {
              crossCache[i].add(affix);
            }
          }         // end of if affix admits remainder
        }           // end of single affix rule
      }             // end of iterator on rules
    }               // end of iterator on splits
  }

  // if the word and its inflexed/derived form by affix are the same
  protected boolean affixSame(String word, String low,
    AnalyserContext context, AffixConsumer affixConsumer)
  {
    affixConsumer.clearEntries(word);
    if (context.top())
    {
      return homonyms(word, low, context, affixConsumer);
    }
    DerivationConsumer consumer = (DerivationConsumer)affixConsumer;
    consumer.startCache();
    homonyms(word, low, context, affixConsumer);
    return !consumer.cache();    
  }

  protected boolean affixStem(String word, String low, AnalyserContext context,
    AffixConsumer affixConsumer)
  {
    affixConsumer.clearEntries(word);
    if (context.top())
    {
      return subanalyse(word, low, context, affixConsumer);
    }
    DerivationConsumer consumer = (DerivationConsumer)affixConsumer;
    consumer.startCache();
    subanalyse(word, low, context, consumer);
    return !consumer.cache();
  }

  /** 
   * Find 'cross' analyses using the partitions for prefix and suffix and
   * the rules in prefix and suffix cache of <code>context</code> respectively.
   * Break when the first analysis found unless the current stemming level is
   * {@link AnalyserControl#ALL_CROSSES} at least. Add cross analyses to
   * <code>analyses</code>.
   * @param analyses the collection of analyses
   * @param context the analyser context
   */
  protected void crossStem(Collection analyses, AnalyserContext context)
  {
    ConsumerManager cm = context.getConsumerManager();
    AffixCache affixCache = cm.getAffixCache();

    String[] pSplits = affixCache.getPrefixSplit();
    String[] sSplits = affixCache.getSuffixSplit();

    List[] pfxCache = affixCache.getCrossablePrefixCache();
    List[] sfxCache = affixCache.getCrossableSuffixCache();

    CrossInflexionConsumer consumer = cm.getCrossConsumer();
    consumer.setStems(analyses);

    // run sSplits only from where we are in pSplits:
    //  if [0, i] is the prefix then try suffixes [j, end] only for j >= i
    for(int i = CROSS_PREFIX_START; i + i < pSplits.length; i++)
    {
      List prefixes = pfxCache[i];

      if (prefixes != null) // satisfied crossable prefixes 
      {
        // code for finding longest suffix first would be
        //   for(int j = i + CROSS_SUFFIX_START; 2 * j < sSplits.length; j++)
        // now find shortest suffix first
        for(int j = (sSplits.length - 1) / 2; j >= i + CROSS_SUFFIX_START; j--)
        {
          List suffixes = sfxCache[j];
          if (suffixes != null) // statisfied crossable suffixentry
          {
            String sremainder = sSplits[j + j + 1].substring(i);
            String pstem = null;
            Iterator pit = prefixes.iterator();
            while (pit.hasNext())
            {
              PrefixEntry prefix = (PrefixEntry)pit.next();
              pstem = prefix.getStrip() + sremainder;

              Iterator sit = suffixes.iterator();
              while(sit.hasNext())
              {
                SuffixEntry suffix = (SuffixEntry)sit.next();
                String sstem = pstem + suffix.getStrip();

                consumer.setAffixes(prefix, suffix);

                if (subanalyse(sstem, sstem.toLowerCase(), context, consumer))
                {
                  return;
                }
              }   // end of iterator on suffixes
            }     // end of iterator on prefixes
          }       // end if (suffixes != null)
        }         // end of iterator on sSplits
      }           // end of if prefixes not null
    }             // end of iterator on pSplits
  }

  public boolean subanalyse(String word, String lowerCaseWord,
    AnalyserContext context, AnalysisConsumer consumer)
  {
    AnalyserContext subContext = context.getSubContext();
    return subContext == null ?
      homonyms(word, lowerCaseWord, context, consumer) :
      doAnalysis(word, lowerCaseWord, subContext, true, consumer);
  }

  protected synchronized AnalyserContext ensureIstemContext()
  {
    if (istemContext == null)
    {
      istemContext = createContext();
    }
    return istemContext;
  }

  protected synchronized AnalyserContext ensureDefaultContext()
  {
    if (defaultContext == null)
    {
      defaultContext = createContext();
    }
    return defaultContext;
  }

  protected AnalyserContext createContext()
  {
    AnalyserContext context = new AnalyserContext();
    CompoundControl cc = context.getControl().getCompoundControl();

    if (rules.getCompound() != (char)0)
    {
      cc.setEnabled(true);
      cc.setMin(rules.getMinWord());
    }
    else
    {
      cc.setEnabled(false);
    }
    return context;
  }
}