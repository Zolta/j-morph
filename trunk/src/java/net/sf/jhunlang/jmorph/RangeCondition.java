package net.sf.jhunlang.jmorph;

/**
 * RangeCondition instances store character range conditions.
 */
public class RangeCondition extends Condition
{
  /**
   * Counts maximum character occurring in RangeConditions
   */
  public static int maxMax;
  /**
   * Convenience constant for the bitsize of int
   */
  public final static int INT_SIZE = 32;

  /**
   * The character range
   */
  protected char[] range;
  /**
   * The bits for the range
   */
  int[] set;

  /**
   * Create a new RangeCondition with the given character range.
   * The created condition will be affirmative, only the characters
   * from the range will satisfy it.
   * @param range the condition character range
   */
  public RangeCondition(char[] range)
  {
    this(range, false);
  }

  /**
   * Create a new RangeCondition with the given character range.
   * The created condition will be negated if <code>not</code> is true.
   * @param range the condition character range
   * @param not if the condition is negated
   */
  public RangeCondition(char[] range, boolean not)
  {
    super(not);
    this.range = range;

    contentString = "[" + contentString + new String(range) + "]";

    int max = 0;
    for(int i = 0; i < range.length; i++)
    {
      int c = 1 + range[i] / INT_SIZE;
      if (c > max)
      {
        max = c;
      }
    }

    if (maxMax < max)
    {
      maxMax = max;
    }
    
    set = new int[max];

    for(int i = 0; i < range.length; i++)
    {
      int c = range[i];
      set[c / INT_SIZE] |= (1 << ((c % INT_SIZE)));
    }
  }

  /**
   * Return the condition character range
   */
  public char[] getRange()
  {
    return range;
  }

  /**
   * Return whether or not the given character satisfies this condition.
   */
  public boolean admit(char c)
  {
    int k = c / INT_SIZE;
    return not ^ (k >= set.length ? false :
           ((set[k] & (1 << (c % INT_SIZE))) != 0));
  }
}
