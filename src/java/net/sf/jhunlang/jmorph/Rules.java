package net.sf.jhunlang.jmorph;

import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import java.io.Serializable;

import java.util.regex.Pattern;

import net.sf.jhunlang.jmorph.analysis.CompoundController;
import net.sf.jhunlang.jmorph.analysis.CompoundFlagController;

/**
 * Rules stores AffixEntry instances as it is appropriate for the different
 * Analyser implementations. (The different store implementations should be
 * given in different classes. The only cause of not doing this is lazyness.
 * Sorry.)
 *
 * The present implementation stores suffix and prefix entries in AffixEntries.
 * The inner map of AffixEntriesis is hashes affixc entries by their strip.
 * If the same strip occurs in more than one entries with then the collection
 * of these entries, otherwise the entry itself is stored in the map.<p>
 *
 * Usage example:
 *
 * <pre>
 *  AffixEntries entries = rules.getSuffixEntries();
 *  String append = something;
 *  for(int i = 0; i < append.length() && entries != null; i++)
 *  {
 *     entries = entries.getMap(new Character(append.charAt(i)));
 *  }
 *  if (entries != null)
 *  { 
 *    // get suffix entries hashed by their strip
 *    Map byStrip = (Map)entries.getByStrip();
 *    // iterate on byStrip
 *    Iterator it = byStrip.values().iterator();
 *    while (it.hasNext())
 *    {
 *      // o is either the collection of multiple suffix entries with
 *      // the same strip or the single suffix entry
 *      Object o = it.next();
 *      if (o instanceof Collection)
 *      {
 *        Collection sameStrip = (Collection)o;
 *        Iterator sit = sameStrip.iterator();
 *        while(sit.hasNext())
 *        {
 *          // HERE WE ARE
 *          SuffixEntry suffixEntry = (SuffixEntry)sit.next());
 *        }
 *      }
 *      else // o itself is SuffixEntry instance
 *      {
 *        // HERE WE ARE
 *        SuffixEntry suffixEntry = (SuffixEntry)o;
 *      }
 *    }
 *  }
 * </pre>
 * 
 */
public class Rules implements Serializable
{
  public static interface Controller extends Serializable
  {
    boolean add2sub(Rules rules, AffixEntry entry);
    boolean add(Rules rules, AffixEntry entry);
  }

  /**
   * The controller of where to add affix entries
   */
  protected Controller controller = new Controller()
  {
    public boolean add2sub(Rules rules, AffixEntry entry)
    {
      return false;
    }
    public boolean add(Rules rules, AffixEntry entry)
    {
      return true;
    }
  };

  /**
   * Collection of Prefix instances
   */
  protected Map prefixes = new TreeMap();
  /**
   * Collection of Suffix instances
   */
  protected Map suffixes = new TreeMap();
  /**
   * Map for PrefixEntry instances
   */
  protected AffixEntries prefixEntries =  new AffixEntries();
  /**
   * Map for SuffixEntry instances
   */
  protected AffixEntries suffixEntries =  new AffixEntries();
  /**
   * Number of PrefixEntry's
   */
  protected int prefixCount;
  /**
   * Number of SuffixEntry's
   */
  protected int suffixCount;
  /**
   * Compound first flag if any.
   * Words having this flag can be the first tag of compound words.
   */
  protected char compoundFirst;
  /**
   * Compound last flag if any.
   * Words having this flag can be the last tag of compound words.
   */
  protected char compoundLast;
  /**
   * Compound forbidden flag if any.
   * Words having this flag can not be tag of compound words.
   */
  protected char compoundForbidden;
  /**
   * Compound flag if any.
   * Words having this flag can be tags of compound words.
   */
  protected char compound;
  /**
   * The controller of compound decomposition. <code>done</code> creates
   * a {@link CompoundFlagController} if not set via
   * <code>setCompoundController</code>.
   */
  protected CompoundController compoundController;
  /**
   * Compound word flag if any
   * Words having this flag are compound words.
   */
  protected char compoundWord;
  /**
   * The minimum length of compund word tags.
   */
  protected int minWord;
  /**
   * The sub rules if any
   */ 
  protected Rules subRules;
  /**
   * The parent rules if any
   */ 
  protected Rules parentRules;
  /**
   * Pattern for prefix flags to remove from flags of a new compound word
   * @see CompoundDictEntry#CompoundDictEntry(Rules, DictEntry, DictEntry)
   */
  protected Pattern prefixPattern;
  /**
   * Pattern for suffix flags to remove from flags of a new compound word
   * @see CompoundDictEntry#CompoundDictEntry(Rules, DictEntry, DictEntry)
   */
  protected Pattern suffixPattern;
  
  protected Conditionsmap conditionsMap;  

  /**
   * Create a new Rules instance
   */
  public Rules()
  {
    conditionsMap = new Conditionsmap();
  }

  /**
   * Create a new Rules instance passing affixes to <code>subRules</code> as
   * <code>add</code> and <code>add2sub</code> of <code>controller</code>
   * tell to.
   * @param subRules the Rules instance to add affixes for which
   * <code>add2sub</code> of <code>controller</code> returns <code>true</code>
   * @param controller the controller of where to add affixes
   */
  public Rules(Rules subRules, Controller controller)
  {
    this.subRules = subRules;
    this.controller = controller;
    subRules.parentRules = this;
    conditionsMap = subRules.getConditionsMap();
  }
  
  public boolean isCompoundFlag(char c)
  {
    return c == compound || c == compoundFirst ||
           c == compoundLast || c == compoundForbidden;
  }
  
  public String compoundFlagString()
  {
    return "" + compound + compoundFirst + compoundLast + compoundForbidden;
  }

  /**
   * Set compound controller.
   * @param compoundController the compound controller to set
   */
  public void setCompoundController(CompoundController compoundController)
  {
    this.compoundController = compoundController;
  }

  /**
   * Set compound flag.
   * @param flag the compound flag
   */
  public void setCompound(char flag)
  {
    compound = flag;
  }

  /**
   * Set compound first flag.
   * @param flag the compound first flag
   */
  public void setCompoundFirst(char flag)
  {
    compoundFirst = flag;
  }

  /**
   * Set compound last flag.
   * @param flag the compound last flag
   */
  public void setCompoundLast(char flag)
  {
    compoundLast = flag;
  }

  /**
   * Set compound forbidden flag.
   * @param flag the compound forbidden flag
   */
  public void setCompoundForbidden(char flag)
  {
    compoundForbidden = flag;
  }

  public CompoundController getCompoundController()
  {
    return compoundController;
  }

  /**
   * Return Prefix with flag <code>c</code> or <code>null</code>.
   * @param c the prefix flag
   * @return Prefix with flag <code>c</code> or <code>null</code>
   */
  public Prefix getPrefix(Character c)
  {
    Prefix prefix = (Prefix)prefixes.get(c);
    return (prefix == null && subRules != null) ?
      subRules.getPrefix(c) : prefix;
  }

  public Suffix getSuffix(Character c)
  {
    Suffix suffix = (Suffix)suffixes.get(c);
    return (suffix == null && subRules != null) ?
      subRules.getSuffix(c) : suffix;
  }

  public Affix getAffix(Character c)
  {
    Affix affix = getSuffix(c);
    if (affix == null)
    {
      affix = getPrefix(c);
    }
    return affix;
  }

  /**
   * Set compound word flag and minimum length of compund word tag.
   * @param minWord the minimum length of compund word tag
   * @param flag the compound word flag
   */
  public void setCompoundWord(int minWord, char flag)
  {
    compoundWord = flag;
    this.minWord = minWord;
  }

  /**
   * Return if {@link #subRules} is not <code>null</code>.
   * @return if {@link #subRules} is not <code>null</code>
   */
  public boolean hasSubRules()
  {
    return subRules != null;
  }

  /**
   * Return {@link #subRules}.
   * @return {@link #subRules}
   */
  public Rules getSubRules()
  {
    return subRules;
  }

  /**
   * Return if {@link #parentRules} is not <code>null</code>.
   * @return if {@link #parentRules} is not <code>null</code>
   */
  public boolean hasParentRules()
  {
    return parentRules != null;
  }

  /**
   * Return {@link #parentRules}.
   * @return {@link #parentRules}
   */
  public Rules getParentRules()
  {
    return parentRules;
  }

  /**
   * Return the compound flag.
   * @return the compound flag
   */
  public char getCompound()
  {
    return compound;
  }

  /**
   * Return compound first flag.
   * @return the compound first flag
   */
  public char getCompoundFirst()
  {
    return compoundFirst;
  }

  /**
   * Return compound last flag.
   * @return the compound last flag
   */
  public char getCompoundLast()
  {
    return compoundLast;
  }

  /**
   * Return compound forbidden flag.
   * @return the compound forbidden flag
   */
  public char getCompoundForbidden()
  {
    return compoundForbidden;
  }

  /**
   * Return compound word flag.
   * @return the compound word flag
   */
  public char getCompoundWord()
  {
    return compoundWord;
  }

  /**
   * Return minimum length of compund word tags.
   * @return the minimum length of compund word tags.
   */
  public int getMinWord()
  {
    return minWord;
  }

  /**
   * Return number of entries
   * @return number of entries stored so far
   */
  public int numEntries()
  {
    return suffixCount + prefixCount;
  }

  /**
   * Return number of suffix entries
   * @return number of suffix entries stored so far
   */
  public int numSuffixEntries()
  {
    return suffixCount;
  }

  /**
   * Return number of prefix entries
   * @return number of prefix entries stored so far
   */
  public int numPrefixEntries()
  {
    return prefixCount;
  }

  /**
   * Return the collection of suffixes.
   * @return the collection of suffixes.
   */
  public Collection getSuffixes()
  {
    return suffixes.values();
  }

  /**
   * Return the collection of prefixes.
   * @return the collection of prefixes.
   */
  public Collection getPrefixes()
  {
    return prefixes.values();
  }

  /**
   * Return the map of suffix entries.
   * @return the map of suffix entries.
   */
  public AffixEntries getSuffixEntries()
  {
    return suffixEntries;
  }

  /**
   * Return the map of prefix entries.
   * @return the map of prefix entries.
   */
  public AffixEntries getPrefixEntries()
  {
    return prefixEntries;
  }

  /**
   * @return return the conditionsMap.
   */
  public Conditionsmap getConditionsMap()
  {
    return conditionsMap;
  }
  
  /**
   * Add <code>affix</code> to this instance.
   * @param affix the affix entries of which are to be added
   */
  public void add(Affix affix)
  {
    if (affix instanceof Suffix)
    {
      addSuffix((Suffix)affix);
    }
    else
    {
      addPrefix((Prefix)affix);
    }
  }

  /**
   * Add the entries of the given suffix.
   * @param suffix the suffix entries of which are to be added
   */
  protected void addSuffix(Suffix suffix)
  {
    suffixCount += add(suffix, suffixes, suffixEntries);
  }

  /**
   * Add the entries of the given prefix.
   * @param prefix the prefix entries of which are to be added
   */
  protected void addPrefix(Prefix prefix)
  {
    prefixCount += add(prefix, prefixes, prefixEntries);
  }

  protected void add(Affix affix, AffixEntry entry)
  {
    if (affix instanceof Prefix)
    {
      prefixCount += add(prefixes, prefixEntries, affix, entry);
    }
    else
    {
      suffixCount += add(suffixes, suffixEntries, affix, entry);
    }
  }

  /**
   * Add affix to <code>affixMap</code> and add its entries to
   * <code>entryMap</code>.
   * @param affix the affix to store
   * @param affixMap the collection <code>affix<code> is added to
   * @param entryMap the map to add entries to
   * @return the number entries added
   */
  protected int add(Affix affix, Map affixMap, AffixEntries entryMap)
  {
    AffixEntry[] entries = affix.getEntries();

    int length = entries.length;
    int count = 0;

    for(int i = 0; i < length; i++)
    {
      AffixEntry entry = entries[i];
      if (entry != null)
      {
        count += add(affixMap, entryMap, affix, entry);
      }
    }
    return count;
  }

  protected int add(
    Map affixMap, AffixEntries entryMap, Affix affix, AffixEntry entry)
  {
    if (subRules != null && controller.add2sub(this, entry))
    {
      subRules.add(affix, entry);
    }

    if (controller.add(this, entry))
    {
      affixMap.put(new Character(affix.getName()), affix);
      entryMap.add(entry);
      return 1;
    }
    return 0;
  }

  protected void collectPrefixFlags(Collection affixFlags)
  {
    affixFlags.addAll(prefixes.keySet());
    if (subRules != null)
    {
      subRules.collectPrefixFlags(affixFlags);
    }
  }

  protected void collectSuffixFlags(Collection affixFlags)
  {
    affixFlags.addAll(suffixes.keySet());
    if (subRules != null)
    {
      subRules.collectSuffixFlags(affixFlags);
    }
  }

  protected String toString(Collection flags)
  {
    StringBuffer sb = new StringBuffer();
    Iterator it = flags.iterator();
    while (it.hasNext())
    {
      String s = Integer.toHexString(((Character)it.next()).charValue());
      while (s.length() < 4)
      {
        s = "0" + s;
      }
      sb.append("\\u" + s);
    }
    return sb.toString();
  }

  public void done()
  {
    HashSet prefixFlags = new HashSet();
    HashSet suffixFlags = new HashSet();

    collectPrefixFlags(prefixFlags);
    collectSuffixFlags(suffixFlags);

    // cannot inherit compound from right
    String prefix = toString(prefixFlags) + compound;
    String suffix = toString(suffixFlags);

    prefixPattern = Pattern.compile("[" + prefix + "]");
    suffixPattern = Pattern.compile("[" + suffix + "]");

    if (compoundController == null)
    {
      compoundController = new CompoundFlagController(this);
    }
  }

  public String toString()
  {
    StringBuffer sb = new StringBuffer("Rules[");
    if (subRules != null)
    {
      sb.append("[");
    }

    sb.append("prefixes: " + prefixCount + " in " + prefixes.size());
    sb.append(", suffixes: " + suffixCount + " in " + suffixes.size());

    if (subRules != null)
    {
      sb.append("]->\n  " + subRules);
    }
    sb.append("]");
    return sb.toString();
  }
  
  /**
   * @return return the suffixPattern.
   */
  public Pattern getSuffixPattern()
  {
    return suffixPattern;
  }
  
  /**
   * @return return the prefixPattern.
   */
  public Pattern getPrefixPattern()
  {
    return prefixPattern;
  }
}