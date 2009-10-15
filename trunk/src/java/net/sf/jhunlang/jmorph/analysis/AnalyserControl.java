package net.sf.jhunlang.jmorph.analysis;

import net.sf.jhunlang.jmorph.AffixEntry;
import net.sf.jhunlang.jmorph.analysis.consumer.X;

/**
 * AnalyserControl stands for controlling stemming algorithm and caching
 * words already stemmed.
 */
public class AnalyserControl
{
  public final static X flagndcase = new X()
  {
    public Object getAffixIdentifier(AffixEntry affix)
    {
      return affix.getFlagAndCaseString();
    }
    
    public String toString()
    {
      return "flag&case";
    }
  };
  
  public final static X flag = new X()
  {
    public Object getAffixIdentifier(AffixEntry affix)
    {
      return affix.getFlags().getFlagString();
    }
    
    public String toString()
    {
      return "flag";
    }
  };
  
  /**
   * The default x is 'flag and case'.
   */
  public final static X DEFAULT_X = flagndcase;
  /**
   * Convenience constant for 'analyse until the first analysis found'
   */
  public final static int FIRST_STEM = 0;
  /**
   * Convenience constant for 'analyse until all analysis without
   * inflexion is found'
   */
  public final static int ALL_STEMS = 1;
  /**
   * Convenience constant for 'analyse until the first suffix
   * inflexion even if analyses found earlier'
   */
  public final static int FIRST_SUFFIX = 2;
  /**
   * Convenience constant for 'analyse until all suffix
   * inflexions even if analyses found earlier'
   */
  public final static int ALL_SUFFIXES = 3;
  /**
   * Convenience constant for 'analyse until the first prefix
   * inflexion even if analyses found earlier'
   */
  public final static int FIRST_PREFIX = 4;
  /**
   * Convenience constant for 'analyse until all prefix
   * inflexions even if analyses found earlier'
   */
  public final static int ALL_PREFIXES = 5;
  /**
   * Convenience constant for 'analyse until the first crossed
   * inflexion even if analyses found earlier'
   */
  public final static int FIRST_CROSS = 6;
  /**
   * Convenience constant for 'analyse until all crossed
   * inflexions even if stems found earlier'
   */
  public final static int ALL_CROSSES = 7;
  /**
   * Convenience constant for 'analyse until the first compound
   * even if analyses found earlier'
   */
  public final static int FIRST_COMPOUND = 8;
  /**
   * Convenience constant for 'analyse until all compound analyses
   * even if analyses found earlier'
   */
  public final static int ALL_COMPOUNDS = 9;
  /**
   * The default depth.
   */
  public static int DEFAULT_DEPTH = FIRST_STEM;
  /**
   * String representation of legal depth values
   */
  public final static String[] DEPTHS = 
  {
    "first-stem", "all-stems",
    "first-suffix", "all-suffixes",
    "first-prefix", "all-prefixes",
    "first-cross", "all-crosses",
    "first-compound", "all-compounds"
  };
  
  public static final X[] XES =
  {
    flag, flagndcase
  };
  
  /**
   * The depth of analysis.
   * The legal values are those defined in static constants.
   */
  protected final int depth;
  /**
   * The control for decomposition to compound word.
   */
  protected final CompoundControl compoundControl;
  
  protected X x;

  /**
   * Create a AnalyserControl with a default CompoundControl and with the
   * default depth.
   */
  public AnalyserControl()
  {
    this(new CompoundControl());
  }

  /**
   * Create a AnalyserControl with the given CompoundControl and with the
   * default depth.
   * @param compoundControl the compound control
   */
  public AnalyserControl(CompoundControl compoundControl)
  {
    this(compoundControl, DEFAULT_DEPTH, DEFAULT_X);
  }

  /**
   * Create a AnalyserControl with a default CompoundControl and with the
   * given depth.
   * @param depth the depth of analysis
   * @exception IllegalArgumentException if depth is
   * not one of the legal values
   */
  public AnalyserControl(int depth)
  {
    this(depth, flagndcase);
  }

  /**
   * Create a AnalyserControl with a default CompoundControl, with the
   * deafult depth and with the given <code>x</code>.
   * @param x the {@link X} of the analysis
   */
  public AnalyserControl(X x)
  {
    this(DEFAULT_DEPTH, x);
  }

  /**
   * Create a AnalyserControl with <code>compoundControl</code> and
   * <code>depth</code>.
   * @param compoundControl the compound control
   * @param depth the depth of analaysis
   * @exception IllegalArgumentException if depth is
   * not one of the legal values
   */
  public AnalyserControl(CompoundControl compoundControl, int depth)
  {
    this(compoundControl, depth, DEFAULT_X);
  }
  
  /**
   * Create a AnalyserControl with a default CompoundControl, with the given
   * <code>x</code> and with the given depth
   * @param depth the depth of analysis
   * @param x the {@link X} of the analysis
   * @exception IllegalArgumentException if depth is
   * not one of the legal values
   */
  public AnalyserControl(int depth, X x)
  {
    this(new CompoundControl(), depth, x);
  }

  /**
   * Create a AnalyserControl with <code>compoundControl</code> and
   * <code>depth</code>.
   * @param compoundControl the compound control
   * @param x the {@link X} of the analysis
   * @exception IllegalArgumentException if depth is
   * not one of the legal values
   */
  public AnalyserControl(CompoundControl compoundControl, X x)
  {
    this(compoundControl, DEFAULT_DEPTH, x);
  }
  
  /**
   * Create a AnalyserControl with <code>compoundControl</code> and
   * <code>depth</code>
   * @param compoundControl the compound control
   * @param depth the depth of stemming
   * @param x the {@link X} of the analysis
   * @exception IllegalArgumentException if depth is
   * not one of the legal values
   */
  public AnalyserControl(CompoundControl compoundControl, int depth, X x)
  {
    if (depth < 0 || depth > ALL_COMPOUNDS)
    {
      throw new IllegalArgumentException("" + depth);
    }
    this.depth = depth;
    this.x = x;
    this.compoundControl = compoundControl;
  }
  
  public X getX()
  {
    return x;
  }

  /**
   * Return the depth of stemming.
   * @return the depth of stemming 
   */
  public int getDepth()
  {
    return depth;
  }

  /**
   * Return the compound control.
   * @return the compound control
   */
  public CompoundControl getCompoundControl()
  {
    return compoundControl;
  }

  public String toString()
  {
    return "AnalyserControl[" + DEPTHS[depth] + ", " + compoundControl + "]";
  }
}