package net.sf.jhunlang.jmorph.analysis;

import net.sf.jhunlang.jmorph.CompoundDictEntry;
import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.Rules;

public class CompoundFlagController implements CompoundController
{
  /**
   * Compound first flag if any.
   * Words having this flag can be the first tag of compound words.
   */
  protected char compoundFirst;
  /**
   * Compound last flag if any.
   * Words having this flag can be the last tag of compound words.
   */
  protected char compoundLast;
  /**
   * Compound flag if any.
   * Words having this flag can be tags of compound words.
   */
  protected char compound;

  protected CompoundFlagController()
  {}

  public CompoundFlagController(Rules rules)
  {
    setFlags(rules);
  }

  public void setFlags(Rules rules)
  {
    this.compoundFirst = rules.getCompoundFirst();
    this.compound = rules.getCompound();
    this.compoundLast = rules.getCompoundLast();
  }

  /**
   * Return if <code>left</code> and <code>right</code> together form a legal
   * compound word. This method is called only after <code>compoundInner</code>
   * or <code>compoundLast</code> returned true for <code>right</code>.
   * The present implementation return if <code>left</code> is flagged either
   * with <code>compound</code> or
   * <code>compoundFirst</code>.
   * @param left the left component word
   * @param right the right component word
   * @return if <code>left</code> and <code>right</code> together form a legal
   * compound word.
   */
  public boolean compound(DictEntry left, DictEntry right)
  {
    return left.hasFlag(compound) || left.hasFlag(compoundFirst);
  }

  /**
   * Return if <code>right</code> can be an inner component of a compound
   * word.
   * @param right the component word
   * @return if <code>right</code>  can be an inner component of a compound
   * word
   */
  public boolean compoundInner(DictEntry right)
  {
    return right.hasFlag(compound);
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
    return last.hasFlag(compound) || last.hasFlag(compoundLast);
  }

  /* 
   * @see CompoundController#createCompound(DictEntry, DictEntry)
   */
  public CompoundDictEntry createCompound(
    Rules rules, DictEntry left, DictEntry right)
  {
    return new CompoundDictEntry(rules, left, right);
  }
}