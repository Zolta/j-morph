package net.sf.jhunlang.jmorph.morph;

public class Derivative extends Morph
{
  protected POS source;
  protected POS target;

  public Derivative(String name, POS source, POS target)
  {
    super(name);
    this.source = source;
    this.target = target;
  }

  public POS getSourcePOS()
  {
    return source;
  }

  public POS getTargetPOS()
  {
    return target;
  }

  public String toString()
  {
    return source.getName() + "[" + name + "]" + target.getName();
  }
}