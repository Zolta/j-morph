package net.sf.jhunlang.jmorph.analysis;

/**
 * Generate ispell-like stemming output.
 * @TODO the ispell symbols '&' and '?' are not yet implemented
 */
public class IStem
{
  /**
   * Convenience constant for the ispell-defined stem type '*'.
   */
  public final static char FOUND = '*';
  /**
   * Convenience constant for the ispell-defined stem type '-'.
   */
  public final static char COMPOUND = '-';
  /**
   * Convenience constant for the ispell-defined stem type '+'.
   */
  public final static char INFLEXED = '+';
  /**
   * Convenience constant for the ispell-defined stem type '#'.
   */
  public final static char NONE = '#';  
  /**
   * Convenience constant for the ispell-defined stem type array.
   */
  public final static char[] TYPES = { FOUND, COMPOUND, INFLEXED, NONE };

  /**
   * The type of this IStem instance.
   * Possible values of type are {@link #FOUND}, {@link #COMPOUND},
   * {@link #INFLEXED} and {@link #NONE}.
   */
  protected final char type;
  /**
   * The analysis of {@link #word} this istem comes from. {@link #type}
   * is NONE iff <code>analyis</code> is <code>null</code>.
   */
  protected Analysis analysis;
  /**
   * The original word this IStem instance is the istem of.
   */
  protected String word;
  /**
   * The ispell-offset of the word.
   */
  protected int offset;

  /**
   * Create a new IStem instance for <code>word</code> analysed as described
   * in <code>analysis</code>.
   * @param word the original word
   * @param analysis the analysis of the word
   */
  public IStem(String word, Analysis analysis)
  {
    this.word = word;
    this.analysis = analysis;
    if (analysis.compound())
    {
      this.type = COMPOUND;
    }
    else
    {
      type = analysis.inflexed() ? INFLEXED : FOUND;
    }
  }

  /**
   * Create a new IStem instance for unstemmed <code>word</code>.
   * <code>offset</code> is the ispell-defined offset of the
   * <code>word</code> in the input line.
   * @param word the word 
   * @param offset the offset of word in the input line
   */
  public IStem(String word, int offset)
  {
    this.type = NONE;
    this.word = word;
    this.offset = offset;
  }

  /**
   * Return the <code>Analysis</code> describing how {@link #word} has been
   * analysed. Return <code>null</code> if {@link #type} is {@link #NONE}.
   * @return {@link #analysis}
   */
  public Analysis getAnalysis()
  {
    return analysis;
  }

  /**
   * Return the stem type.
   * @return {@link #type}
   */
  public char getType()
  {
    return type;
  }

  /**
   * Return the original word.
   * @return {@link #word}
   */
  public String getWord()
  {
    return word;
  }

  /**
   * Return the offset of the original word in the input line.
   * @return {@link #offset}
   */
  public int getOffset()
  {
    return offset;
  }

  /**
   * Return the part of speech of word or <code>null</code> if
   * <code>type</code> if {@link #NONE}.
   * @return the part of speech of word
   */
  public String getPOS()
  {
    return analysis == null ? null : analysis.getPOS();
  }

  public String toString()
  {
    switch (type)
    {
      case FOUND:
        return type + "";
      case COMPOUND:
        return type + "";
      case INFLEXED:
        return type + " " + word;
      case NONE:
        return type + " " + word + " " + offset;
      default:
        return "????";
    }
  }
}