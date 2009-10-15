package net.sf.jhunlang.jmorph.analysis;

import java.util.List;
import java.util.ArrayList;

/**
 * AffixCache provides cache for splits and checked affixes of words.
 */
public class AffixCache
{
  /**
   * Maximum length of word this cache can handle.
   */
  protected int maxWordLength;
  /**
   * Cache for satisfied suffix rules
   */
  protected final List[] SL;
  /**
   * Cache for satisfied crossable suffix rules
   */
  protected final List[] SCL;
  /**
   * Cache for satisfied prefix rules
   */
  protected final List[] PL;
  /**
   * Cache for satisfied crossable prefix rules
   */
  protected final List[] PCL;
  /**
   * The splits of the word currently stemmed for suffix and prefix
   * check respecively
   */
  protected String[][] splits;
  /**
   * The splits of the lowercase word currently stemmed
   * for suffix and prefix check respecively
   */
  protected String[][] lowerCaseSplits;
  /**
   * The current word. Splits above store splits of this word.
   */
  protected String word;
  /**
   * The current word all lowercase.
   */
  protected String lowerCaseWord;
  /**
   * If prefix is sought for in the current stemming
   */
  protected boolean suffix;

  /**
   * Create a new AffixCache with <code>maxWordLength</code>.
   * @param maxWordLength max. length of words this cache can handle
   */
  public AffixCache(int maxWordLength)
  {
    this.maxWordLength = maxWordLength;

    SL = new ArrayList[maxWordLength];
    SCL = new ArrayList[maxWordLength];
    PL = new ArrayList[maxWordLength];
    PCL = new ArrayList[maxWordLength];

    for(int i = 0; i < maxWordLength; i++)
    {
      SL[i] = new ArrayList(10);
      SCL[i] = new ArrayList(10);
      PL[i] = new ArrayList(10);
      PCL[i] = new ArrayList(10);
    }
  }

  /**
   * Set caches to those for prefix or suffix depending on
   * <code>b</code>.
   * @param b tells if prefix or suffix caches are the current ones
   */
  public boolean setSuffix(boolean b)
  {
    boolean old = suffix;
    suffix = b;
    return old;
  }

  /**
   * Return if the current caches are those for suffix or prefix.
   * @return if suffix or prefix caches are the current ones
   */
  public boolean suffix()
  {
    return suffix;
  }

  /**
   * Return the cache for satisfied suffix rules.
   * @return the suffix cache
   */
  public List[] getSuffixCache()
  {
    return SL;
  }

  /**
   * Return the cache for crossable satisfied suffix rules.
   * @return the crossable suffix cache
   */
  public List[] getCrossableSuffixCache()
  {
    return SCL;
  }

  /**
   * Return the cache for satisfied prefix rules.
   * @return the prefix cache
   */
  public List[] getPrefixCache()
  {
    return PL;
  }

  /**
   * Return the cache for crossable satisfied prefix rules.
   * @return the crossable prefix cache
   */
  public List[] getCrossablePrefixCache()
  {
    return PCL;
  }

  /**
   * Return the cache for satisfied prefix or suffix rules
   * @return the suffix or the prefix cache depending on {@link #suffix}
   */
  public List[] getCache()
  {
    return suffix ? SL : PL;
  }

  /**
   * Return the cache for satisfied crossable prefix or suffix rules
   * @return the crossable suffix or the prefix cache
   * depending on {@link #suffix}
   */
  public List[] getCrossableCache()
  {
    return suffix ? SCL : PCL;
  }

  /**
   * Return the prefix or suffix splits
   * @return the prefix or suffix splits depending on {@link #suffix}
   */
  public String[] getSplit()
  {
    return suffix ? splits[0] : splits[1];
  }

  /**
   * Return the lower case prefix or suffix splits
   * @return the lower case prefix or suffix splits depending on {@link #suffix}
   */
  public String[] getLowerCaseSplit()
  {
    return suffix ? lowerCaseSplits[0] : lowerCaseSplits[1];
  }

  public String[] getLowerCaseSuffixSplit()
  {
    return lowerCaseSplits[0];
  }

  public String[] getLowerCasePrefixSplit()
  {
    return lowerCaseSplits[1];
  }

  public String getWord()
  {
    return word;
  }

  public String getLowerCaseWord()
  {
    return lowerCaseWord;
  }

  /**
   * Return the suffix splits
   * @return the suffix splits
   */
  public String[] getSuffixSplit()
  {
    return splits[0];
  }

  /**
   * Return the prefix splits
   * @return the prefix splits
   */
  public String[] getPrefixSplit()
  {
    return splits[1];
  }

  /**
   * Clear the first <code>num</code> elements of suffix cache {@link #SL}.
   * @param num the number of cache entries to clear
   */
  public void clearSuffixCache(int num)
  {
    for(int i = 0; i < num; i++)
    {
      SL[i].clear();
    }
  }

  /**
   * Clear the first <code>num</code> elements of both suffix and
   * crossable suffix cache, {@link #SL} and {@link #SCL} respectively.
   * @param num the number of cache entries to clear
   */
  public void clearSuffixCaches(int num)
  {
    for(int i = 0; i < num; i++)
    {
      SL[i].clear();
      SCL[i].clear();
    }
  }

  /**
   * Clear the first <code>num</code> elements of both prefix and
   * crossable prefix cache, {@link #PL} and {@link #PCL} respectively.
   * @param num the number of cache entries to clear
   */
  public void clearPrefixCaches(int num)
  {
    for(int i = 0; i < num; i++)
    {
      PL[i].clear();
      PCL[i].clear();
    }
  }

  /**
   * Return splits of the given word as it is appropriate for checking
   * suffix and prefix rules. The first element of the returned 2-element
   * array is the list of splits for suffixes while the second is the one
   * for prefixes. If the word contains hyphens and <code>hyphenize</code>
   * is true then throw HyphenizedException.
   * @param word the word to split
   * @param lowerCaseWord the all lowercase form of word
   * @return the 2-element array of lists of splits
   */
  public String[][] split(String word, String lowerCaseWord)
  {
    this.word = word;
    this.lowerCaseWord = lowerCaseWord;

    int length = word.length();
    int size = length + length;

    String[] l0 = new String[size];
    String[] l1 = new String[size];

    String[] l2 = new String[size];
    String[] l3 = new String[size];

    for(int i = 0; i < length; i++)
    {
      String append = word.substring(i);
      String remainder = word.substring(0, i);

      String lowerCaseAppend = lowerCaseWord.substring(i);
      String lowerCaseRemainder = lowerCaseWord.substring(0, i);

      int j = i + i;

      l0[j] = append;
      l1[j] = remainder;

      l2[j] = lowerCaseAppend;
      l3[j] = lowerCaseRemainder;

      j++;
      l0[j] = remainder;
      l1[j] = append;

      l2[j] = lowerCaseRemainder;
      l3[j] = lowerCaseAppend;
    }

    splits = new String[][] { l0, l1 };
    lowerCaseSplits = new String[][] { l2, l3 };
    return splits;
  }

  /**
   * Return splits of the given word as it is appropriate for checking
   * suffix rules.
   * @param word the word to split
   * @return the list of splits
   */
  public String[] suffixSplit(String word)
  {
    int length = word.length();
    String[] l = new String[length + length];

    for(int i = 0; i < length; i++)
    {
      int j = i + i;
      l[j] = word.substring(i);
      l[++j] = word.substring(0, i);
    }
    return l;
  }
}