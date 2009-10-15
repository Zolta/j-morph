package net.sf.jhunlang.jmorph.analysis;

import net.sf.jhunlang.jmorph.CompoundDictEntry;
import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.Rules;

public class CompoundAnyController implements CompoundController
{
  public boolean compound(DictEntry left, DictEntry right)
  {
    return true;
  }

  public boolean compoundInner(DictEntry inner)
  {
    return true;
  }

  public boolean compoundLast(DictEntry last)
  {
    return true;
  }
  
  public CompoundDictEntry createCompound(
    Rules rules, DictEntry left, DictEntry right)
  {
    return new CompoundDictEntry(rules, left, right);    
  }
  
  public void setFlags(Rules rules)
  {}
}
