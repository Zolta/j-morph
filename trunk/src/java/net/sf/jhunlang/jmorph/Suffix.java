package net.sf.jhunlang.jmorph;

/**
 * Suffix instances store groups of suffix inflexion rules.
 */
public class Suffix extends Affix
{
  /**
   * Create a new Suffix instance with the given parameters.
   * Initialize <code>entries</code> to the given size.
   * @param name the name or flag
   * @param crossable if the rules can be crossed
   * @param size the number of prefix rules belonging to this group
   */
  public Suffix(char name, boolean crossable, int size)
  {
    super(name, crossable, size);
    entries = new SuffixEntry[size];
  }

  /**
   * Create and return a new SuffixEntry with the given parameters
   * @param rules the rules this affix belongs to
   * @param index the index of the rule
   * @param acondition the conditions of the rule
   * @param strip the characters to strip from stems
   * @param append the characters that can be added to stripped stems
   */
  public AffixEntry createEntry(Rules rules, int index,
    Condition[] acondition, String strip, String append)
  {
    return new SuffixEntry(rules, this, index, acondition, strip, append);
  }
  
  public AffixEntry createEntry(Rules rules, int index,
    Condition[] acondition, String strip, String append, String morph)
  {
    return new SuffixEntry(rules, this, index, acondition, strip, append, morph);
  }
}
