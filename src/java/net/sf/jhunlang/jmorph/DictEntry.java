package net.sf.jhunlang.jmorph;

import java.util.Collection;
import java.util.LinkedList;

import java.io.Serializable;

/**
 * DictEntry instances represent the dictionary definitions.
 * The extensions {@link XWordEntry} and {@link ExceptionEntry} stand for
 * <i>exceptional words</i> and the <i>exceptional solutions</i> resolving
 * exceptional words respectively.
 */
public class DictEntry implements Serializable
{
  /**
   * Capitalization constant for all letters lower case.
   */
  public final static byte ALL_LOWER = 0;
  /**
   * Capitalization constant for first letter capital.
   */
  public final static byte CAPITALIZED = 1;
  /**
   * Capitalization constant for all letters capital.
   */
  public final static byte ALL_CAPITALIZED = 2;
  /**
   * Capitalization constant for funny capitalization.
   */
  public final static byte FUNNY_CAPITALIZED = 3;
  /**
   * Printable names for capitalization types.
   */
  public final static String[] CAPS = { "none", "cap", "all", "funny" };
  /**
   * Constant for the empty homonyms collection. 
   */
  protected final static Collection NO_HOMONYM = new LinkedList();

  /**
   * The affix flags admitted by this entry.
   */
  protected AffixFlags flags = AffixFlags.empty;
  /**
   * The word of this entry.
   */
  protected String word;
  /**
   * The capitalization of the word.
   */
  protected byte capitalization;
  /**
   * The root of this entry.
   * Root is the immediate 'predecessor' of this entry.
   */
  protected DictEntry root;
  /**
   * The homonyms of this word.
   */
  protected Collection homonyms = NO_HOMONYM;
  /**
   * The inflexion of this word.
   */
  protected DictEntryExtension inflexion;
  /**
   * The morph string of this word.
   */
  protected String morph; 
  
  /**
   * Create a DictEntry for the <code>word</code>.
   * @param word the word
   * @throws IllegalArgumentException if the word is <code>null</code>
   */
  public DictEntry(String word)
  {
    setWord(word);
  }

  /**
   * Create a DictEntry for the <code>word</code> with <code>root</code>.
   * @param word the word
   * @param root the root of the created entry
   * @throws IllegalArgumentException if the word is <code>null</code>
   */
  public DictEntry(String word, DictEntry root)
  {
    this(word);
    this.root = root;
  }

  /**
   * Create a DictEntry for <code>word</code> with flags specified by
   * <code>flagCharacters</code>.
   * @param word the word
   * @param flagCharacters the affix flags
   * @throws IllegalArgumentException if the word is null
   */
  public DictEntry(String word, char[] flagCharacters)
  {
    this(word, AffixFlags.getAffixFlags(flagCharacters));
  }
  
  /**
   * Create a DictEntry for <code>word</code> with flags specified by
   * <code>flagCharacters</code> and with morphological description
   * <code>morph</code>.
   * @param word the word
   * @param flagCharacters the affix flags
   * @param morph the morphological description
   * @throws IllegalArgumentException if the word is null
   */
  public DictEntry(String word, char[] flagCharacters, String morph)
  {
    this(word, AffixFlags.getAffixFlags(flagCharacters));
    this.morph = morph;
  }


  /**
   * Create a DictEntry for <code>word</code> with <code>flags<code>.
   * @param word the word
   * @param flags the affix flags
   * @throws IllegalArgumentException if the word is null
   */
  public DictEntry(String word, AffixFlags flags)
  {
    this(word);
    setFlags(flags);
  }

  /**
   * Create a DictEntry for <code>word</code> with <code>flags<code>
   * and with <code>root</code>.
   * @param word the word
   * @param flags the affix flags
   * @param root the root of the created entry
   * @throws IllegalArgumentException if the word is null
   */
  public DictEntry(String word, DictEntry root, AffixFlags flags)
  {
    this(word, root);
    setFlags(flags);
  }

  /**
   * Return {@link #word} of this entry.
   * @return the word of this entry
   */
  public String getWord()
  {
    return word;
  }

  /**
   * Return {@link #capitalization} of the word of this entry.
   * @return the {@link #capitalization}
   */
  public byte getCapitalization()
  {
    return capitalization;
  }
  
  /**
   * Return collection of homonyms.
   * @return the homonyms of this word.
   */
  public Collection getHomonyms()
  {
    return homonyms;
  }

  /**
   * Return {@link #flags}
   * @return the flags of this entry
   */
  public AffixFlags getFlags()
  {
    return flags;
  }

  /**
   * Return the <code>String</code> representation of the affix flags
   * @return the String representation of the affix flags
   */
  public String getFlagString()
  {
    return flags.getFlagString();
  }

  /**
   * Accumulated flags stands for synthetizing multiple derivations using
   * the affix rules of hunmorph RC1-2. Accumulation goes along derivations
   * until pos is unchanged.
   * @return the flags accumulated along the 'pos-preserving' derivation path
   * to this entry
   */
  public AffixFlags getAccumulatedFlags()
  {
    return flags;
  }

  /**
   * Return {@link #inflexion}.
   * @return return {@link #inflexion}
   */
  public DictEntryExtension getInflexion()
  {
    return inflexion;
  }
  
  public void setMorph(String morph)
  {
    this.morph = morph;
  }

  /**
   * Return the number of components of this entry. This implementation
   * returns 1. Extensions representing compound words should override this
   * method to return the true number of components.
   * @return the number of component entries
   */
  public int length()
  {
    return 1;
  }

  /**
   * Tells if this entry represents a compound word. This implementation
   * returns <code>false</code>. Extensions representing compound words should
   * override this method to return <code>true</code>.
   * @return if this entry represents a compound word.
   */
  public boolean compound()
  {
    return false;
  }

  /**
   * Tells if this entry has been read from the dictionary. Derivative
   * extensions should override this method to return <code>false</code>.
   * @return if this entry has been read from the dictionary file
   */
  public boolean dictionaryWord()
  {
    return true;
  }

  /**
   * Tells if this entry represents a derived word. The implementation here
   * returns false. Extensions should override this method to return the truth.
   * @return if this entry represents a derived word.
   */
  public boolean derived()
  {
    return false;
  }

  /**
   * Return if this entry is an inflexed form of <code>root</code>.
   * @return return if this entry is inflexed
   */
  public boolean inflexed()
  {
    return root != null && !derived();
  }

  /**
   * Return the part-of-speech string of this entry as told by
   * {@link #inflexion}.
   * @return part-of-speech of this entry
   */
  public String getPOS()
  {
    return inflexion == null ? "" : inflexion.getPOS();
  }

  /**
   * Return the case ending string of this entry. This implemenatation
   * returns <code>null</code>. Extensions should override this method.
   * @return case ending of this entry
   */
  public String getCaseEnding()
  {
    return null;
  }

  /**
   * Return the {@link #root} of this entry or <code>null</code>.
   * @return the root of this entry if any
   */
  public DictEntry getRootEntry()
  {
    return root;
  }

  /**
   * Return the root word of this entry regardless if the root is inflexed.
   * @return the root word of this entry
   */
  public String getRootWord()
  {
    return word;
  }
  
  /**
   * Return the absolute root of this entry, the one at the bottom of
   * the derivational chain.
   * @return the absolute root entry
   */
  public DictEntry getAbsoluteRootEntry()
  {
    return root == null ? this : root.getAbsoluteRootEntry();
  }
  
  /**
   * Return the derivation depth of this entry.
   * @return the derivation depth of this entry
   */
  public int depth()
  {
    return root == null ? 0 : root.depth() + 1;
  }

  /**
   * Return the absolute root word of this entry, the one at the bottom of
   * the derivational chain.
   * @return the absolute root word
   */
  public String getAbsoluteRootWord()
  {
    return root == null ? word : root.getAbsoluteRootWord();
  }

  /**
   * Return the relative root, the root at the end of derivational chain
   * of this entry.
   * @return the relative root of this entry
   */
  public DictEntry getRelativeRootEntry()
  {
    return inflexed() ? root.getRelativeRootEntry() : this;
  }

  /**
   * Return the {@link #word} of the absolute root returned by
   * {@link #getRelativeRootEntry()}.
   * @return the word of the relative root of this entry
   */
  public String getRelativeRootWord()
  {
    return inflexed() ? root.getRelativeRootWord() : word;
  }

  /**
   * Return the entry representing the dictionary root of {@link #word}.
   * Return {@link #getRelativeRootEntry()} if this entry has been read from a
   * dictionary or {@link #root} is <code>null</code>. Otherwise return the
   * dictionary root of {@link #root}.
   * @return the dictionary root entry of this entry
   */
  public DictEntry getDictionaryRootEntry()
  {
    return (root == null || dictionaryWord()) ?
      getRelativeRootEntry() : root.getDictionaryRootEntry();
  }

  /**
   * Return the dictionary root of {@link #word}.
   * Return {@link #getRelativeRootWord()} if this entry has been read from a
   * dictionary or {@link #root} is <code>null</code>. Otherwise return the
   * dictionary root of {@link #root}.
   * Return dictionary stem of {@link #root} otherwise.
   * @return the dictionary root word of the word of this entry
   */
  public String getDictionaryRootWord()
  {
    return (root == null || dictionaryWord()) ?
      getRelativeRootWord() : root.getDictionaryRootWord();
  }

  /**
   * Set {@link #inflexion}.
   * @param inflexion the inflexion to set
   */
  public void setInflexion(DictEntryExtension inflexion)
  {
    this.inflexion = inflexion;
  }

  /**
   * Return the inflexed word resulted by applying <code>affix</code> to the
   * {@link #word} of this entry.
   * @param affix the affix
   * @return the inflexed word
   */
  public String inflex(AffixEntry affix)
  {
    return affix.inflex(word);
  }
  
  /**
   * Return the inflexed word resulted by applying <code>prefix</code> and
   * <code>suffix</code> to the {@link #word} of this entry.
   * @param prefix the prefix inflexion
   * @param suffix the suffix inflexion
   * @return the inflexed word
   */
  public String inflex(PrefixEntry prefix, SuffixEntry suffix)
  {
    return prefix.inflex(suffix.inflex(word));
  }
  
  /**
   * Return if the capitalization of this entry admits <code>word</code>.
   * Note that <code>admitCaptialization</code> does not check if
   * <code>word</code> equals to the word of this entry!
   * @param word the word to admit
   * @return if the capitalization admits <code>word</code>
   */
  public boolean admitCapitalization(String word)
  {
    switch (capitalization)
    {
      case ALL_LOWER:
        return true;
      case CAPITALIZED:
        return Character.isUpperCase(word.charAt(0));
      default:
        return this.word.equals(word);
    }
  }

  /**
   * Return if the capitalization of this entry admits <code>word</code> with
   * first character upper case.
   * Note that <code>admitCaptialization</code> does not check if
   * <code>word</code> equals to the word of this entry!
   * @param word the word to admit
   * @return if the capitalization admits <code>word</code>
   */
  public boolean admitLowerCapitalization(String word)
  {
    switch (capitalization)
    {
      case ALL_LOWER:
        return true;
      case CAPITALIZED:
        return true;
        //return Character.isUpperCase(word.charAt(0));
      default:
        return this.word.equals(word);
    }
  }

  /**
   * Return if {@link #flags} contains <code>flag</code>
   * @param flag the flag to check
   * @return if <code>flags</code> contains <code>flag</code>
   */
  public boolean hasFlag(int flag)
  {
    return flags.hasFlag(flag);
  }

  /**
   * Return if {@link #flags} contains the flag of <code>entry</code>.
   * The implementation of this method tells if {@link #flags} contains
   * the flag of <code>entry</code>.
   * @param entry the affix entry to check
   * @return if <code>flags</code> contains the flag of <code>entry</code>
   */
  public boolean hasFlag(AffixEntry entry)
  {
    return hasFlag(entry.getFlag());
  }

  /**
   * Set the root of this word if setting doesn't result a cyclic reference
   * via roots.
   * @param entry the root of this word.
   * @return if <code>root</code> has been set
   */
  public boolean setRoot(DictEntry entry)
  {
    if (this != entry)
    {
      root = entry;
      return true;
    }
    return false;
  }

  /**
   * Add homonym <code>h</code> if the addition doesn't result
   * a cyclic reference via homonyms.
   * @param h the homonym to add.
   * @return if the homonym <code>h</code> has been added
   */
  public final boolean addHomonym(DictEntry h)
  {
    if (h != this)
    {
      if (homonyms == NO_HOMONYM)
      {
        homonyms = new LinkedList();
      }
      homonyms.add(h);
      return true;
    }
    return false;
  }

  private void setWord(String word)
  {
    if (word == null)
    {
      throw new IllegalArgumentException("Word cannot be null");
    }
    this.word = word;
    capitalization = capitalization(word);
  }

  public void setFlags(AffixFlags flags)
  {
    if (flags == null)
    {
      throw new IllegalArgumentException();
    }
    this.flags = flags;
  }

  /**
   * Return the morphological description of {@link #inflexion}.
   * @return the morphological description 
   */
  public String morphString()
  {
    return inflexion == null ?
      (morph == null ? "" : morph) : inflexion.morphString();
  }
  
  /**
   * Return the derivational description of {@link #inflexion}.
   * @return the derivational description 
   */
  public String derivatorString()
  {
    return inflexion == null ? "" : inflexion.derivatorString();
  }
  
  /**
   * Return the inflexional description of {@link #inflexion}.
   * @return the inflexional description 
   */
  public String inflexionString()
  {
    return inflexion == null ? "" : inflexion.inflexionString();
  }
  
  /**
   * Return the derivator string showing how this entry is derived
   * from <code>stem</code>. if <code>stem</code> is <code>null</code> then
   * return the derivation from the dictionary root.
   * @param stem the stem entry 
   * @return the derivator string from the <code>stem</code>
   */
  public String derivatorString(DictEntry stem)
  {
    if (root == null || this == stem)// || dictionaryWord())
    {
      return "";
    }
    else
    {
      String d = derivatorString();
      String r = root.derivatorString(stem);
      if (r.length() == 0)
      {
        return d;
      }
      else if (d.length() == 0)
      {
        return r;
      }
      return r + ' ' + d;
    }    
  }

  /**
   * Return the internal <code>String</code> representation of the content
   * of this entry.
   * @return the <code>String</code> representation of the of content of this
   * entry
   */
  public String longContentString()
  {
    return word + "/" + flags.intern() + "<" + morphString() + ">";
  }

  /**
   * Return the internal <code>String</code> representation of the content
   * of this entry.
   * @return the <code>String</code> representation of the of content of this
   * entry
   */
  public String contentString()
  {
    return word;
  }

  public String toLongString()
  {
    return shortClassName() + "[" + longContentString() +
      (root == null ? "" : ("<=" + root.toLongString())) + "]";
  }

  public String toString()
  {
    return shortClassName() + "[" + contentString() +
      (root == null ? "" : ("<=" + root.getRelativeRootEntry())) + "]";
  }

  protected String shortClassName()
  {
    String s = getClass().getName();
    return s.substring(s.lastIndexOf('.') + 1).substring(0, 2);
  }
  
  /**
   * Return the capitalization type of <code>word</code>.
   * The returned value is one of {@link #ALL_LOWER}, {@link #CAPITALIZED},
   * {@link #ALL_CAPITALIZED} and {@link #FUNNY_CAPITALIZED}.
   * @param word the word the capitalization type of which is to return
   * @return the captialization type of <code>word</code>.
   */
  public static byte capitalization(String word)
  {
    String lower = word.toLowerCase();
    if (lower.equals(word))
    {
      return ALL_LOWER;
    }
    else if (lower.substring(1).equals(word.substring(1)))
    {
      return CAPITALIZED;
    }
    else
    {
      String upper = word.toUpperCase();
      if (upper.equals(word))
      {
        return ALL_CAPITALIZED;
      }
      else
      {
        return FUNNY_CAPITALIZED;
      }
    }
  }
}
