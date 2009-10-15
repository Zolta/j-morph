package net.sf.jhunlang.jmorph.morph;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class Morphs
{
  protected Map poss = new TreeMap();
  protected Map derivatives = new TreeMap();
  protected Map cases = new TreeMap();

  public Collection getPOSes()
  {
    return poss.values();
  }

  public Collection getDerivatives()
  {
    return derivatives.values();
  }

  public Collection getCases()
  {
    return cases.values();
  }

  public POS getPOS(String name)
  {
    return (POS)poss.get(name);
  }

  public Derivative getDerivative(String name)
  {
    return (Derivative)derivatives.get(name);
  }

  public Case getCase(String name)
  {
    return (Case)cases.get(name);
  }
}