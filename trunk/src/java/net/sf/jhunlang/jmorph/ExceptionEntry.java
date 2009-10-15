package net.sf.jhunlang.jmorph;

import net.sf.jhunlang.jmorph.parser.DictConstants;

/**
 * ExceptionEntry instances represent the dictionary definitions of
 * <i>exceptional solution</i>s resolving <i>exceptional</i> words
 * stored in {@link XWordEntry} instances..
 */
public class ExceptionEntry extends DictEntry implements DictConstants
{
  /**
   * The solution given by this exception
   */
  protected String stem;
  /**
   * The entry for flags
   */
  protected DictEntry flagEntry;

  /**
   * Create an ExceptionEntry with the <code>solution</code> of an
   * exceptional <code>word</code>.
   * Strips off the {@link DictConstants#EXCEPTIONAL_STRIP} from the
   * beginning of <code>solution</code> as specified in huspell.
   * @param word the word
   * @param solution the solution
   */
  public ExceptionEntry(String word, String solution)
  {
    super(word);
    if (solution.startsWith(EXCEPTIONAL_STRIP))
    {
      solution = solution.substring(EXCEPTIONAL_STRIP.length());
    }
    this.stem = solution;
  }

  public void setFlagEntry(DictEntry entry)
  {
    flagEntry = entry;
  }

  /**
   * Return the solution of {@link #word}
   * @return the solution of <code>word</code> of this entry
   */
  public String getAbsoluteRootWord()
  {
    return stem;
  }

  public boolean hasFlag(int flag)
  {
    return flagEntry == null ? super.hasFlag(flag) : flagEntry.hasFlag(flag);
  }
  /**
   * Return the internal <code>String</code> representation of the content
   * of this instance
   * @return the internal <code>String</code> representation 
   */
  public String contentString()
  {
    return super.contentString() + " / " + stem;
  }
}
