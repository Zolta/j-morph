package net.sf.jhunlang.jmorph.parser;

public interface DictConstants
{
  /**
   * Convenience constant for the huspell-specified start of exception words
   */
  public final static String EXCEPTION_START = "__";
  /**
   * Convenience constant for the huspell-specified flag of exceptional words
   */
  public final static char X_FLAG = '0';
  /**
   * Convenience constant for the huspell-specified flag heading the number of
   * bytes to cut off from the end of exceptional words
   */
  public final static char CHUNK_FLAG = 'g';
  /**
   * Convenience constant for the optional start of exceptional words.
   */
  public final static String EXCEPTIONAL_STRIP = "-";
  /**
   * hunspell handles words ending with y by marking them with f and adding
   * the word ending with i or j.
   */
  public final static char YIJ_FLAG = 'f';
  public final static char FORBIDDEN_FLAG = 'w';
  public final static char ONLYROOT_FLAG = 'u';
}