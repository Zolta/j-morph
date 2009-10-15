package net.sf.jhunlang.jmorph.analysis;

import net.sf.jhunlang.jmorph.analysis.consumer.AnalysisConsumer;

/**
 * Interface of the compound stemmer algorithm
 */
public interface CompoundAnalyser
{
  /**
   * System property telling if check legality of decompositions.
   */
  public final static String DONT_CHECK = "compound.check.not";
  /**
   * Constant for no compound on substem
   */
  public final static int COMPOUND_LEVEL_TOP = 0;
  /**
   * Constant for add components from left only
   */
  public final static int COMPOUND_LEVEL_LEFT = 1;
  /**
   * Constant for decompose on all possible way
   */
  public final static int COMPOUND_LEVEL_ALL = 2;
  
  /**
   * Decompose <code>word</code> until <code>consumer</code> tells enoguh.
   * Return if <code>consumer</code> told enough. 
   * @param word the word to decompose
   * @param lowerCaseWord the all lower case form of the word
   * @param context the stemming context
   * @param consumer the consumer
   * @return if <code>consumer</code> told enough
   */
  boolean compound(String word, String lowerCaseWord,
    AnalyserContext context, AnalysisConsumer consumer);

  void setLevel(int level);
  int getLevel();
}

