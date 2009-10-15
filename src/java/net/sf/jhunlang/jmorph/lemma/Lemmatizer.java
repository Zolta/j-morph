/*
 * Created by Peter Halacsy <peter at halacsy.com>
 * 
 * This work is licensed under the Creative Commons 
 * Attribution License. To view a copy of this license, 
 * visit http://creativecommons.org/licenses/by/2.0/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 * 
 * Created on May 21, 2005
 *
 */

package net.sf.jhunlang.jmorph.lemma;
import java.util.List;

/**
 * Simplified jmorph component working as a lemmatiser.
 */
public interface Lemmatizer
{
  /**
   * Return the List of {@link Lemma}'s for word <code>w</code>.
   * <code>stripDerivatives</code> tells if derivatives should be stripped off.
   * @param w the word
   * @param stripDerivates if derivatives should be stripped off
   * @return the List of {@link Lemma}'s for word <code>w</code>
   */
  public List lemmatize(String w, boolean stripDerivates);    
}
