package net.sf.jhunlang.jmorph;

public class DummyEntry extends DictEntry
{
  public DummyEntry(String word)
  {
    super(word, new char[0]);
  }

  public String getPOS()
  {
    return "dummy";
  }

  public boolean hasFlag(int flag)
  {
    return flags.getFlagString().length() == 0 ? true : super.hasFlag(flag);
  }
}