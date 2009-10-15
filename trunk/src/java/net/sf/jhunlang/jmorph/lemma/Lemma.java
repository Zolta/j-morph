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

/**
 * Wrapper bean for the output of lemmatiyer.
 */
public class Lemma
{
  protected String word;
  protected String POS;
  
  public Lemma(String word, String POS)
  {
    this.word = word;
    this.POS = POS;
  }
    
  /**
   * @return Returns the pOS.
   */
  public String getPOS()
  {
    return POS;
  }
  
  /**
   * @param pos The pOS to set.
   */
  public void setPOS(String pos)
  {
    POS = pos;
  }
  
  /**
   * @return Returns the stem.
   */
  public String getWord()
  {
    return word;
  }
  
  /**
   * @param word The word to set.
   */
  public void setWord(String word)
  {
    this.word = word;
  }
    
  /**
   * Returns true if the other object is a Lemma with the
   * same word and POS.
   */
  public boolean equals(Object other)
  {
    if (!(other instanceof Lemma)) 
    {
        return false;
    }
    Lemma otherLemma = (Lemma) other;
    if (otherLemma.POS.equals(POS) && otherLemma.word.equals(word)) 
    {
      return true;
    }    
    return false;      
  }

  public int hashCode()
  { 
    return word.hashCode() * 31 + POS.hashCode();
  }
}
