package net.sf.jhunlang.jmorph;

/**
 * XWordEntry instances represent <i>exceptional words</i>, those that are
 * resolved by {@link ExceptionEntry} instances.
 */
public class XWordEntry extends DictEntry
{
  /**
   * The number of characters to cut off from end of the word
   */
  protected byte chunk;

  protected String stem;

  /**
   * Create an XWordEntry instance for the given with the specified flags.
   * @param word the word
   * @param flagCharacters the affix flags
   * @exception IllegalArgumentException if the word is null
   */
  public XWordEntry(String word, char[] flagCharacters)
  {
    this(word, flagCharacters, (byte)0);
  }

  /**
   * Create an XWordEntry instance for the given with the specified flags and
   * with the given number of characters to cut off when stemmed..
   * @param word the word
   * @param flagCharacters the affix flags
   * @param chunk the number of characters to cut off
   * @exception IllegalArgumentException if the word is null
   */
  public XWordEntry(String word, char[] flagCharacters, byte chunk)
  {
    super(word, flagCharacters);
    if ((this.chunk = chunk) != 0)
    {
      try
      {
        this.stem = word.substring(0, word.length() - chunk);
      }
      catch (Throwable t)
      {
        this.chunk = 0;
        // ignore dictionary error (what else to do?)
      }
    }
    else
    {
      this.stem = word;
    }
  }

  public String getAbsoluteRootWord()
  {
    return word;// return stem;
  }

  /**
   * Return {@link #chunk}
   * @return the number of characters to cut off from end of the word
   */
  public byte getChunk()
  {
    return chunk;
  }

  /**
   * Return if {@link #chunk} is not 0.
   * @return if {@link #chunk} is not 0
   */
  public boolean chunked()
  {
    return chunk != 0;
  }

  /**
   * Return the internal <code>String</code> representation of the content
   * of this instance
   * @return the internal <code>String</code> representation
   */
  public String contentString()
  {
    return super.contentString() + ", " + stem;
  }
}
