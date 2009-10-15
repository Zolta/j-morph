package net.sf.jhunlang.jmorph.analysis;

import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.PrefixEntry;
import net.sf.jhunlang.jmorph.SuffixEntry;

public interface Analysis
{
  // return the relative root word
  String getRelativeRootWord();
  // return the absolute word (the one with no derivation)
  String getAbsoluteRootWord();
  // return the dictionary stem word (the one read from the dictionary)
  String getDictionaryRootWord();
  // return the prefix inflexion if any
  PrefixEntry getPrefixEntry();
  // return the suffix inflexion if any
  SuffixEntry getSuffixEntry();
  // return if inflexed; as dictionary
  // might be inflexed, inflexed true doesnt imply
  // that getPrefixEntry or getSuffixEntry is not <code>null</code>
  boolean inflexed();
  // return inflexed form
  String getInflexedWord();
  // return if the stem has been derived explicitly
  boolean derived();
  // return the part of speech of stem
  String getPOS();
  String getCaseEnding();
  boolean compound();
  DictEntry getDictEntry();
  String toLongString();
  
  String morphString();
  String relativeMorphString();
  String dictionaryMorphString();
  String absoluteMorphString();
}