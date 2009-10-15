package net.sf.jhunlang.jmorph.parser;

public interface AffixConstants
{
  /**
   * Convenience constant for empty String
   */
  public final static String EMPTY = "";
  /**
   * Convenience constant for the character-string starting a suffix rule
   */
  public final static String SUFFIX = "SFX";
  /**
   * Convenience constant for the character-string starting a prefix rule
   */
  public final static String PREFIX = "PFX";
  /**
   * Convenience constant for the comment character
   */
  public final static String COMMENT = "#";
  /**
   * Convenience constant for the character defining 'crossable'
   */
  public final static String YES = "Y";
  /**
   * Convenience constant for the character defining 'not crossable'
   */
  public final static String NO = "N";
  /**
   * Convenience constant for the character defining 'no strip' and 'no append'
   */
  public final static char NULL = '0';
  /**
   * Convenience constant for the character defining 'no condition'
   */
  public final static char NOCOND = '.';

  /**
   * Convenience constant for the character starting a range condition
   */
  public final static char RANGE_START = '[';
  /**
   * Convenience constant for the character ending a range condition
   */
  public final static char RANGE_END = ']';
  /**
   * Convenience constant for the character negating a condition
   */
  public final static char NEG_COND = '^';
  /**
   * Convenience constant for the character-strings starting a rule
   */
  public static final String[] FLAGS = { SUFFIX, PREFIX };
  /**
   * Convenience constant for the characters defining crossable
   */
  public static final String[] CROSS = { YES, NO };
  /**
   * The COMPOUND keyword prefix
   */
  public final static String COMPOUND = "COMPOUND";
  /**
   * The COMPOUNDFLAG keyword
   */
  public final static String COMPOUNDFLAG = COMPOUND + "FLAG";
  /**
   * The COMPOUNDFIRST flag keyword
   */
  public final static String COMPOUNDFIRST = COMPOUND + "FIRST";
  /**
   * The COMPOUNDFORBIDDEN flag keyword
   */
  public final static String COMPOUNDFORBIDDEN = COMPOUND + "FORBIDFLAG";
  /**
   * The COMPOUNDLAST flag keyword
   */
  public final static String COMPOUNDLAST = COMPOUND + "LAST";
  /**
   * The COMPOUNDWORD keyword
   */
  public final static String COMPOUNDWORD = COMPOUND + "WORD"; // number + flag?
  /**
   * The COMPOUNDMIN keyword
   */
  public final static String COMPOUNDMIN = COMPOUND + "MIN";
  /**
   * The COMPOUNDSYLLABLE keyword
   */
  public final static String COMPOUNDSYLLABLE = COMPOUND + "SYLLABLE";
}