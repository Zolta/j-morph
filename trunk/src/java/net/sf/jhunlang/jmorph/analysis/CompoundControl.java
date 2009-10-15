package net.sf.jhunlang.jmorph.analysis;

/**
 * CompoundControl stands for controlling the way of
 * decomposing words to compound words in stemming.
 */
public class CompoundControl
{
  /**
   * System property for the default disabled.
   */
  public final static String DISABLED = "compound.disabled"; 
  /**
   * System property for the default minimum length of component words.
   */
  public final static String MIN = "compound.min"; 
  /**
   * System property for the default maximum number of component words.
   */
  public final static String MAX = "compound.max"; 
  /**
   * System property for the default hint of compound stemming.
   */
  public final static String HINT = "compound.hint"; 

  /**
   * Convenience constant for the hint
   * 'decompose via the shortest component words first'
   */
  public final static int SHORTEST = 0;
  /**
   * Convenience constant for the hint
   * 'decompose via the longest component words first'
   */
  public final static int LONGEST = 1;
  /**
   * Convenience constant for the hint
   * 'decompose via both the longest and shortest component words'
   */
  public final static int BOTH = 2;
  /**
   * String representations for the legal hint values
   */
  public final static String[] HINTS = { "shortest", "longest", "both" };
  /**
   * Maximum value for 'minimum length of component words'
   */
  public final static int MAX_MIN = 6;
  /**
   * Maximum value for 'maximum number of component words in a compound word'
   */
  public final static int MAX_MAX = 32;
  /**
   * Maximum value for 'hint'
   */
  public final static int MAX_HINT = BOTH;
  /**
   * The default disabled value is false
   */
  public final static boolean DEFAULT_DISABLED;
  /**
   * The default hint value is SHORTEST
   */
  public final static int DEFAULT_HINT;
  /**
   * The default 'minimum length of component words' is 3
   */
  public static int DEFAULT_MIN = 2;
  /**
   * The default 'maximum number of component words' is 3
   */
  public static int DEFAULT_MAX = 3;

  /**
   * Initialize static DAFULT_DISABLED, DEFAULT_MIN, DEFAULT_MAX and
   * DEFAULT_HINT from the system properties DISABLED, MIN, MAX annd HINT
   * respectively.
   */
  static
  {
    DEFAULT_DISABLED = Boolean.getBoolean(DISABLED);
    int min = DEFAULT_MIN;
    try
    {
      min = Integer.parseInt(System.getProperty(MIN, DEFAULT_MIN + ""));
      if (min < 0 || min > MAX_MIN)
      {
        min = DEFAULT_MIN;
      }
    }
    catch (Throwable t)
    {}
    DEFAULT_MIN = min;

    int max = DEFAULT_MAX;
    try
    {
      max = Integer.parseInt(System.getProperty(MAX, DEFAULT_MAX + ""));
      if (max < 0 || max > MAX_MAX)
      {
        max = DEFAULT_MAX;
      }
    }
    catch (Throwable t)
    {}
    DEFAULT_MAX = max;

    int hint = SHORTEST;
    try
    {
      hint = Integer.parseInt(System.getProperty(HINT, "" + SHORTEST));
      if (hint < 0 || hint > MAX_HINT)
      {
        hint = SHORTEST;
      }
    }
    catch (Throwable t)
    {}
    DEFAULT_HINT = hint;
  }

  /**
   * Tells if compound decomposition is enabled at all.
   */
  protected boolean disabled = DEFAULT_DISABLED;
  /**
   * The minimum length of a component word in compound words
   */
  protected int min;
  /**
   * The maximum number of component words in compound words
   */
  protected int max;
  /**
   * If decomposition recurse via the shortest/longest/both splits.
   * Not implemented yet.
   */
  protected int hint;

  /**
   * Create a CompoundControl with default controlling parameters.
   */
  public CompoundControl()
  {
    this(DEFAULT_HINT);
  }

  /**
   * Create a CompoundControl with the given hint. All other parameters are
   * the defaults.
   * @param hint the hint
   * @exception IllegalArgumentException if hint is not one of
   * SHORTEST, LONGEST and BOTH
   */
  public CompoundControl(int hint)
  {
    this(DEFAULT_MIN, DEFAULT_MAX, hint);
  }

  /**
   * Create a CompoundControl with the given minimum component length and
   * maximum component number and with the default hint.
   * @param min the minimum length of component words
   * @param max the maximum number of component words
   * @exception IllegalArgumentException if either any of min and max is
   * negative or min is greater than MIN_MAX or max is greater than MAX_MAX
   * SHORTEST, LONGEST or BOTH
   */
  public CompoundControl(int min, int max)
  {
    this(min, max, DEFAULT_HINT);
  }

  /**
   * Create a CompoundControl with the given minimum component length, the
   * given maximum component number and with the given hint.
   * @param min the minimum length of component words
   * @param max the maximum number of component words
   * @param hint the hint
   * @exception IllegalArgumentException if either hint is not SHORTEST,
   * LONGEST or BOTH or any of min and max is negative or min is greater
   * than MIN_MAX or max is greater than MAX_MAX
   */
  public CompoundControl(int min, int max, int hint)
  {
    if (hint < 0 || hint > BOTH)
    {
      throw new IllegalArgumentException("" + hint);
    }
    if (min < 0 || min > MAX_MIN)
    {
      throw new IllegalArgumentException("" + min);
    }
    if (max < 0 || max > MAX_MAX)
    {
      throw new IllegalArgumentException("" + max);
    }

    this.min = min;
    this.max = max;
    this.hint = hint;
  }

  /**
   * Enable or disable compound decomposition
   * @param b if compound decomposition enabled
   */
  public void setEnabled(boolean b)
  {
    disabled = !b;
  }

  /**
   * Set minimum length of component words
   * @param min the minimum length of compoenent words
   */
  public void setMin(int min)
  {
    this.min = min;
  }

  /**
   * Tells if compound decomposition is enabled at all
   */
  public boolean enabled()
  {
    return !disabled;
  }

  /**
   * Return hint
   */
  public int getHint()
  {
    return hint;
  }

  /**
   * Return minimum length of component words
   */
  public int getMin()
  {
    return min;
  }

  /**
   * Return maximum number of component words
   */
  public int getMax()
  {
    return max;
  }

  public String toString()
  {
    return "CompoundControl[" +
      enabled() + ", " + max + " of " + min + ", " + HINTS[hint] + "]";
  }
}

