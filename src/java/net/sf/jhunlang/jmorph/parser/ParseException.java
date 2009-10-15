package net.sf.jhunlang.jmorph.parser;

/**
 * Exception thrown while parsing the affix and dictionary files.
 */
public class ParseException extends Exception
{
  /**
   * Create a ParseException instance with no message.
   */
  public ParseException()
  {}

  /**
   * Create a ParseException instance with the given message.
   * @param msg the message
   */
  public ParseException(String msg)
  {
    super(msg);
  }

  /**
   * Create a ParseException instance with the given cause and
   * with the given message.
   * @param msg the message
   * @param cause the cause of the error
   */
  public ParseException(String msg, Throwable cause)
  {
    super(msg, cause);
  }
}
