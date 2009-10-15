package net.sf.jhunlang.jmorph.parser;

import java.util.StringTokenizer;

/**
 * Parser defines and implements utility methods for AffixReader.
 */
public class Parser
{
  /**
   * The line currently being parsed
   */
  protected String line;
  /**
   * The current line number.
   */
  protected int lineNumber;
  /**
   * The tokenizer for the current line..
   */
  protected StringTokenizer tokenizer;

  /**
   * Set current line, increment line number and create tokenizer
   * for the line.
   * @param line the current line
   */
  public void setLine(String line)
    throws ParseException
  {
    setLine(line, lineNumber + 1);
  }

  /**
   * Set current line and line number and create tokenizer
   * for the line.
   * @param line the current line
   * @param lineNumber the line number
   */
  public void setLine(String line, int lineNumber)
    throws ParseException
  {
    if (line == null)
    {
      throw error("Unexpected end of file");
    }
    this.line = line;
    this.lineNumber = lineNumber;
    tokenizer = new StringTokenizer(line);
  }

  public void setTokenizer(StringTokenizer tokenizer)
  {
    this.tokenizer = tokenizer;
  }

  /**
   * Return current line.
   */
  public String getLine()
  {
    return line;
  }

  /**
   * Return current line number.
   */
  public int getLineNumber()
  {
    return lineNumber;
  }

  /**
   * Test if line has been parsed totally.
   * @exception ParseException if there remained unparsed tokens in
   * the current line.
   */
  public void lineDone()
    throws ParseException
  {
    if (tokenizer.hasMoreTokens())
    {
      //throw error("Unexpected token " + tokenizer.nextToken());
    }    
  }

  /**
   * Return remainder of line.
   */
  public String remainder()
  {
    return remainder(tokenizer);
  }
  
  /**
   * Return remainder of <code>st</code>.
   * @param st the StringTokenizer
   */
  public static String remainder(StringTokenizer st)
  {
    StringBuffer sb = new StringBuffer();
    while (st.hasMoreTokens())
    {
      sb.append(st.nextToken());
      if (st.hasMoreTokens())
      {
        sb.append(" ");
      }
    }
    return new String(sb);
  }
  
  /**
   * Return next token of the current line.
   * @return next token of the current line
   * @exception ParseException if there is no more token in the current line.
   */
  public String string()
    throws ParseException
  {
    return string(false);
  }

  /**
   * Return next token of the current line if any.
   * Otherwise return null or throw ParseException depending on
   * whether or not <code>b</code> is true.
   * @return next token of the current line or null
   * @exception ParseException if there is no more token in the current line
   * and <code>b</code> is false.
   */
  public String string(boolean b)
    throws ParseException
  {
    if (!tokenizer.hasMoreTokens())
    {
      if (b)
      {
        return null;
      }
      else
      {
        throw error("Unexpected end of line");
      }
    }
    else
    {
      return tokenizer.nextToken();
    }
  }

  /**
   * Return next token of the current line if it is the given value.
   * Throw ParseException otherwise.
   * @param value the requested token value
   * @exception ParseException if there is no more token in the current line or
   * the next token is not the given value.
   */
  public String string(String value)
    throws ParseException
  {
    String token = string();
    if (token.equals(value))
    {
      return value;
    }
    throw error("Illegal value: " + token + " [" + value + "]");
  }

  /**
   * Return the single character of the next token of the current line
   * if it is the given value. Throw ParseException otherwise.
   * @param value the requested character value
   * @exception ParseException if there is no more token in the current line or
   * the next token is not the given value.
   */
  public char character(char value)
    throws ParseException
  {
    char c = character();
    if (c == value)
    {
      return value;
    }
    throw error("Illegal value: " + c + " [" + value + "]");
  }

  /**
   * Return the index of the next token in the given array of values.
   * @param values the requested values
   * @exception ParseException if there is no more token in the current line or
   * the next token is not in the given values.
   */
  public int string(String[] values)
    throws ParseException
  {
    String token = string();
    for(int i = 0; i < values.length; i++)
    {
      if (token.equals(values[i]))
      {
        return i;
      }
    }
    throw error("Illegal value: " + token, values);
  }

  /**
   * Return the single character of the next token of the current line.
   * @exception ParseException if there is no more token in the current line or
   * the next token is not a single character.
   */
  public char character()
    throws ParseException
  {
    String token = string();
    if (token.length() != 1)
    {
      throw error("Not a character: " + token);
    }
    return token.charAt(0);
  }

  /**
   * Return the integer value of the next token of the current line.
   * @exception ParseException if there is no more token in the current line or
   * the next token is not a number.
   */
  public int integer()
    throws ParseException
  {
    String token = string();
    try
    {
      return Integer.parseInt(token);
    }
    catch (NumberFormatException nfx)
    {
      throw error("Not a number: " + token);
    }
  }

  /**
   * Create and return a ParseException with the given message extended to
   * reflect the current line number.
   * @param msg the error message
   */
  public ParseException error(String msg)
  {
    return new ParseException(msg + " at line " + lineNumber);
  }

  /**
   * Create and return a ParseException with the given cause and
   * with given message extended to reflect the current line number.
   * @param msg the error message
   * @param cause the cause of the error
   */
  public ParseException error(String msg, Throwable cause)
  {
    return new ParseException(
      msg + " at line " + lineNumber + ": " + cause, cause);
  }

  /**
   * Create and return a ParseException with the given message extended to
   * reflect the given values and the current line number.
   * @param msg the error message
   */
  public ParseException error(String msg, String[] values)
  {
    StringBuffer sb = new StringBuffer(msg + " [");
    for(int i = 0; i < values.length; i++)
    {
      sb.append(values[i]);
      if (i == values.length - 1)
      {
        sb.append("]");
      }
      else
      {
        sb.append("|");
      }
    }
    return new ParseException(new String(sb) + " at line " + lineNumber);
  }
}