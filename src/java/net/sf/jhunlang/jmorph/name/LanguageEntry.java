package net.sf.jhunlang.jmorph.name;

import net.sf.jhunlang.jmorph.DictEntry;

public class LanguageEntry extends DictEntry
{
  protected MultipleEntry original;
  protected String language;

  public LanguageEntry(String word, char[] flagCharacters,
    MultipleEntry original, String language)
  {
    super(word, flagCharacters);
    this.language = language;
    this.original = original;
    original.addEntry(this);
  }

  public String getLanguage()
  {
    return language;
  }

  public MultipleEntry getOriginal()
  {
    return original;
  }

  public String contentString()
  {
    return original.getWord() +
      "(" + language + ": " + super.contentString() + ")";
  }
}