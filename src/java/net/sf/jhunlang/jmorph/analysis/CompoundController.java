package net.sf.jhunlang.jmorph.analysis;

import java.io.Serializable;

import net.sf.jhunlang.jmorph.CompoundDictEntry;
import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.Rules;

/**
 * <code>CompoundController</code> controls how different words can be
 * joined to a compound word.
 */
public interface CompoundController extends Serializable
{
  /**
   * Return if <code>left</code> and <code>right</code> together form a legal
   * compound word. This method is called only after <code>compoundInner</code>
   * or <code>compoundLast</code> returned true for <code>right</code>.
   * @param left the left component word
   * @param right the right component word
   * @return if <code>left</code> and <code>right</code> together form a legal
   * compound word.
   */
  boolean compound(DictEntry left, DictEntry right);
  /**
   * Return if <code>inner</code> can be an inner component of a compound
   * word.
   * @param inner the component word
   * @return if <code>inner</code>  can be an inner component of a compound
   * word
   */
  boolean compoundInner(DictEntry inner);
  /**
   * Return if <code>last</code> can be the last component of a compound
   * word.
   * @param last the component word
   * @return if <code>last</code>  can be the last component of a compound
   * word
   */
  boolean compoundLast(DictEntry last);
  
  /**
   * Return the {@link CompoundDictEntry} instance representing the composition
   * of <code>left</code> and <code>right</code>.
   * @param rules the rules that configures decomposition
   * @param left the left component
   * @param right the right component
   * @return the composition of <code>left</code> and <code>right</code>
   */
  CompoundDictEntry createCompound(
    Rules rules, DictEntry left, DictEntry right);
  
  void setFlags(Rules rules);
}