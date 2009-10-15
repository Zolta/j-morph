package net.sf.jhunlang.jmorph.analysis;

import java.util.List;

import net.sf.jhunlang.jmorph.Dictionaries;
import net.sf.jhunlang.jmorph.Rules;
import net.sf.jhunlang.jmorph.analysis.consumer.AnalysisConsumer;

public interface Analyser
{
  /**
   * Return the list of analysises of word found by default context.
   * The returned list enlists {@link AnalysisEntry} instances in
   * the order of their appearance as described in {@link AnalyserControl}.
   * @param word the word to analyse
   * @return the list of {@link AnalysisEntry} instances describing the
   * analyses of the word
   */
  List analyse(String word);
  /**
   * Return the list of analyses of word found by <code>context</code>.
   * The returned list enlists {@link AnalysisEntry} instances in
   * the order of their appearance as described in {@link AnalyserControl}.
   * @param word the word to analyse
   * @param context the context configuring the analyser algorithm
   * @return the collection of {@link AnalysisEntry} instances describing
   * the analyses of the word
   */
  List analyse(String word, AnalyserContext context);
  /**
   * Return ispell-like stemming output found by the default context.
   * @param word the word to stem
   * @param offset the offset to echo as specified in ispell 
   */
  IStem istem(String word, int offset);
  /**
   * Return ispell-like stemming output found by <code>context</code>.
   * @param word the word to stem
   * @param offset the offset to echo as specified in ispell 
   * @param context the context configuring the analyser algorithm
   */
  IStem istem(String word, int offset, AnalyserContext context);
  /**
   * Return the dictionary used by this analyser.
   * @return the dictionary of this analyser
   */
  Dictionaries getDictionaries();
  /**
   * Return the rules used by this analyser.
   * @return the rules used of this analyser
   */
  Rules getRules();

  boolean subanalyse(String word, String lowerCaseWord,
    AnalyserContext context, AnalysisConsumer consumer);
}
