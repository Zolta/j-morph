package net.sf.jhunlang.jmorph.morph;

public abstract class Morph
{
  protected String name;

  public Morph(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return name;
  }

  public String toString()
  {
    return name;
  }
}