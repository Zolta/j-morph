package net.sf.jhunlang.jmorph.sword.parser;

import net.sf.jhunlang.jmorph.sword.SwordExtension;

public class EnglishExtensionParser extends ExtensionParser
{
  public final static char START = '<';
  public final static char END = '>';

  public EnglishExtensionParser(SwordExtension ext)
  {
    super(ext);
  }
}