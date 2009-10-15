package net.sf.jhunlang.jmorph.parser;

import net.sf.jhunlang.jmorph.AffixEntry;
import net.sf.jhunlang.jmorph.AffixEntryExtension;
import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.DictEntryExtension;

/**
 * Common 'fake' implementation of {@link AffixEntryExtension} and
 * {@link DictEntryExtension}.
 */
public class FakeExtension implements DictEntryExtension, AffixEntryExtension
{
  protected String morphString;
  
  public FakeExtension(String morphString)
  {
    this.morphString = morphString;
  }
  
  public String getCaseEnding()
  {
    return morphString;
  }
  
  public String getCase()
  {
    return "";
  }
  
  public String getPOS()
  {
    return morphString;
  }
  
  public String morphString()
  {
    return morphString;
  }

  public String inflexionString()
  {
    return "";
  }

  public String derivatorString()
  {
    return "";
  }

  public boolean isDerivator()  
  {
    return false;
  }
  
  public boolean isInflexion()  
  {
    return true;
  }
  
  public DictEntry derive(AffixEntry entry, String word, DictEntry root)
  {
    return null;
  }
  
  public String toString()
  {
    return morphString();
  }
}
