package net.sf.jhunlang.jmorph.sword;

import net.sf.jhunlang.jmorph.AffixEntry;
import net.sf.jhunlang.jmorph.AffixFlags;
import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.PrefixEntry;

/**
 * SwordEntry instances represent the dictionary definitions of
 * szoszablya root words.
 */
public class SwordEntry extends DictEntry
{
  /**
   * The deriver of this word if it is derived.
   */
  protected AffixEntry deriver;

  protected SwordEntry(String word)
  {
    super(word);
  }

  /**
   * Create a SwodEntry instance for <code>word</code> with flags
   * specified in <code>flagCharacters</code>.
   * @param word the word
   * @param flagCharacters the affix flag characters
   * @exception IllegalArgumentException if the word is null
   */
  public SwordEntry(String word, char[] flagCharacters)
  {
    super(word, flagCharacters);
  }

  /**
   * Create a SwodEntry instance for <code>word</code> with flags
   * specified in <code>flagCharacters</code> with <code>inflexion</code>.
   * @param word the word
   * @param flagCharacters the affix flag characters
   * @param inflexion the inflexion of the word
   * @exception IllegalArgumentException if the word is null
   */
  public SwordEntry(String word, char[] flagCharacters,
    SwordEntryExtension inflexion, DictEntry root)
  {
    this(word, flagCharacters, inflexion);
    this.root = root;
  }

  /**
   * Create a SwodEntry instance for <code>word</code> with flags
   * specified in <code>flagCharacters</code> with <code>inflexion</code>.
   * @param word the word
   * @param flagCharacters the affix flag characters
   * @param inflexion the inflexion of the word
   * @exception IllegalArgumentException if the word is null
   */
  public SwordEntry(
    String word, char[] flagCharacters, SwordEntryExtension inflexion)
  {
    this(word, flagCharacters);
    this.inflexion = inflexion;
  }

  /**
   * Create a SwodEntry representing the derivation of <code>root</code> by
   * <code>deriver</code.
   * @param root the derivation root
   * @param deriver the deriver
   * @param add if flags of <code>root</code> are to be added to the derivation 
   * @exception IllegalArgumentException if the word is null
   */
  public SwordEntry(DictEntry root, AffixEntry deriver, boolean add)
  {
    super(root.inflex(deriver), root,
          add ? deriver.getFlags().add(root.getFlags()) : deriver.getFlags());

    this.deriver = deriver;
    if (add)
    {
      flags = flags.remove((char)deriver.getFlag());
    }

    SwordExtension ext = (SwordExtension)deriver.getExtension();
    SwordEntryExtension swInflexion = new SwordEntryExtension(ext);
    
    inflexion = swInflexion;

    if (swInflexion.getPOSName() == POSName.empty)
    {
      if (root instanceof SwordEntry)
      {
        swInflexion.setPOSName(((SwordEntry)root).getPOSName());
      }
    }
  }

  public boolean hasFlag(AffixEntry entry)
  {
    if (hasFlag(entry.getFlag()))
    {
      return true;
    }
    else if (deriver == null)
    {
      return false;
    }
    // do not inherit if inflexed; both inflexed is impossible
    // so that cases are equal does it
    else if (!entry.getExtension().isDerivator() &&
             root.getPOS() == getPOS() && root.getCaseEnding() == getCaseEnding())
    {
      return root.hasFlag(entry);
    }
    // if entry is prefix and deriver is suffix or
    //    entry is suffix and deriver is prefix
    // then return if root has flag - 'cross derivation'
    // on deriver not null implies root is not null 
    else if ((entry instanceof PrefixEntry) ^ (deriver instanceof PrefixEntry))
    {
      return root.hasFlag(entry);
    }
    else
    {
      return false;
    }
  }

  public AffixFlags getAccumulatedFlags()
  {
    if (deriver == null)
    {
      return super.getAccumulatedFlags();
    }
    else if (root.getPOS() == getPOS() &&
             root.getCaseEnding() == getCaseEnding())
    {
      return flags.add(root.getAccumulatedFlags()).add(deriver.getFlags());
    }
    else
    {
      return super.getAccumulatedFlags();
    }
  }

  /**
   * Tells if this entry represents a derived word.
   * @return if this entry represents a derived word.
   */
  public boolean derived()
  {
    return deriver != null || (inflexion != null && inflexion.isDerivator());
  }

  /**
   * Tells if this entry represents a dictionary word.
   * @return if this entry represents a dictionary word.
   */
  public boolean dictionaryWord()
  {
    return deriver == null;
  }

  public POSName getPOSName()
  {
    return inflexion == null ?
      null : ((SwordEntryExtension)inflexion).getPOSName();
  }

  public String getCaseEnding()
  {
    if (deriver != null)
    {
      return (deriver instanceof PrefixEntry) ?
        root.getCaseEnding() : deriver.getExtension().getCase();
    }
    else
    {
      return inflexion != null ?  inflexion.getCase() : "";
    }
  }

  /**
   * Return the internal <code>String</code> representation of the content
   * of this instance
   * @return the internal <code>String</code> representation
   */
  public String longContentString()
  {    
    if (inflexion != null)
    {
      return super.longContentString() + inflexion;
    }
    else
    {
      return super.longContentString();
    }
  }

  /**
   * Return the internal <code>String</code> representation of the content
   * of this instance
   * @return the internal <code>String</code> representation
   */
  public String contentString()
  {
    if (inflexion != null)
    {
      return super.contentString() + inflexion;
    }
    else
    {
      return super.contentString();
    }
  }
  
  protected String shortClassName()
  {
    return deriver == null ? "Sw" : "Dw";
  }  
}
