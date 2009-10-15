package net.sf.jhunlang.jmorph;

public interface AffixEntryExtension extends DictEntryExtension
{
  DictEntry derive(AffixEntry entry, String word, DictEntry root);  
}