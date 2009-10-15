package net.sf.jhunlang.jmorph.name;

import net.sf.jhunlang.jmorph.AffixFlags;
import net.sf.jhunlang.jmorph.DictEntry;

/**
 * NameEntry instances represent names 
 */
public class NameEntry extends DictEntry
{
  /**
   * The part-of-speech of <code>NameEntry</code> instances
   */
  public final static String NAME_POS = "noun_prs";
  
  public NameEntry(String word, char[] flagCharacters)
  {
    super(word, flagCharacters);
  }

  public NameEntry(String word)
  {
    super(word, new char[0]);
  }

  public String getPOS()
  {
    return NAME_POS;
  }
  
  public boolean hasFlag(int flag)
  {
    return flags == AffixFlags.empty ? true : super.hasFlag(flag);
  }
}