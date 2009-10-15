package net.sf.jhunlang.jmorph.analysis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import net.sf.jhunlang.jmorph.AffixFlags;
import net.sf.jhunlang.jmorph.CompoundDictEntry;
import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.Rules;
import net.sf.jhunlang.jmorph.util.Pair;

public class CompoundPOSController extends CompoundFlagController
{
  protected Set innerSet = new HashSet();
  protected Set lastSet = new HashSet();
  protected Map compoundMap = new HashMap();

  /**
   * Create a compound rule for joining words of part-of-speech
   * <code>left</code> and <code>right</code>. The affix flags <code>add</code>
   * will be added to, while the affix flags <code>remove</code> will be removed
   * from the affix flags of the resulted compound word.
   * @param left the part-of-speech of the left word
   * @param right the part-of-speech of the right word 
   * @param add the affix flags to add to the flags of the compound word
   * @param remove the affix flags to remove from the flags of the compound word
   */
  public void addCompound(
    String left, String right, AffixFlags add, AffixFlags remove)
  {
    compoundMap.put(new Pair(left, right), new Pair(add, remove));
    innerSet.add(left);
    lastSet.add(right);
  }

  /**
   * Return if <code>inner</code> can be an inner component of a compound
   * word.
   * @param inner the component word candidate
   * @return if <code>right</code>  can be an inner component of a compound
   * word
   */
  public boolean compoundInner(DictEntry inner)
  {
    return super.compoundInner(inner) || compoundInner(inner.getPOS());
  }

  /**
   * Return if <code>last</code> can be the last component of a compound
   * word.
   * @param last the component word
   * @return if <code>last</code>  can be the last component of a compound
   * word
   */
  public boolean compoundLast(DictEntry last)
  {
    return super.compoundLast(last) || compoundLast(last.getPOS());
  }

  /**
   * Return if <code>left</code> and <code>right</code> together form a legal
   * compound word. This method is called only after <code>compoundInner</code>
   * or <code>compoundLast</code> returned <code>true</code> for
   * <code>right</code>. The present implementation return if <code>left</code>
   * is flagged either with <code>compound</code> or <code>compoundFirst</code>.
   * @param left the left component word
   * @param right the right component word
   * @return if <code>left</code> and <code>right</code> together form a legal
   * compound word.
   */
  public boolean compound(DictEntry left, DictEntry right)
  {
    return
      super.compound(left, right) || compound(left.getPOS(), right.getPOS());
  }

  public boolean compound(String left, String right)
  {
    return compoundMap.containsKey(new Pair(left, right));
  }

  public boolean compoundInner(String inner)
  {
    return innerSet.contains(inner);
  }

  public boolean compoundLast(String last)
  {
    return lastSet.contains(last);
  }

  /**
   * Adjust flags of the composition of <code>left</code> and
   * <code>right</code>. 
   * @see CompoundController#createCompound(Rules, DictEntry, DictEntry)
   */
  public CompoundDictEntry createCompound(
    Rules rules, DictEntry left, DictEntry right)
  {
    Pair p = (Pair)compoundMap.get(new Pair(left.getPOS(), right.getPOS()));
    return p == null ?
      new CompoundDictEntry(rules, left, right) :
      new CompoundDictEntry(rules, left, right,
        (AffixFlags)p.getA(), (AffixFlags)p.getB());  
  }
  
  public String toString()
  {
    StringBuffer sb = new StringBuffer("POSController[");
    Iterator it = compoundMap.keySet().iterator();
    while (it.hasNext())
    {
      sb.append(it.next());
      if (it.hasNext())
      {
        sb.append(", ");
      }
    }
    return sb.toString();
  }
}