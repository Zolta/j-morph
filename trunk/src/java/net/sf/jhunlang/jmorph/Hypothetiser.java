package net.sf.jhunlang.jmorph;

public class Hypothetiser extends Dictionaries
{
  public Hypothetiser()
  {}

  public boolean add(Object o)
  {
    return false;
  }

  public DictEntry get(String word)
  {
    return new DummyEntry(word);
  }
  
  public Object rawGet(String lowerCaseWord)
  {  
    return new DummyEntry(lowerCaseWord);
  }
}