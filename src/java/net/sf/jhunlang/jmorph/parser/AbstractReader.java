package net.sf.jhunlang.jmorph.parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;

import net.sf.jhunlang.jmorph.factory.Definition;

/**
 * AbstractReader implements the common part of
 * {@link AffixReader} and {@link DictionaryReader}
 */
public abstract class AbstractReader
{
  /**
   * The default encoding for the test.
   */
  public final static String DEFAULT_ENCODING = "ISO-8859-2";

  /**
   * The definition of this reader.
   */
  protected Definition definition;

  public void setDefinition(Definition definition)
  {
    this.definition = definition;
  }

  public Definition getDefinition()
  {
    return definition;
  }

  /**
   * Read from the given inputstream using the given character encoding.
   * @param sourceUrl the source url
   * @param encoding the character encoding of the source
   * @exception IOException if an IO error occurs while opening or
   * reading the file
   * @exception IOException if an io error occurs while parsing the file
   * @exception ParseException if a syntax error occurs while parsing the file
   */
  public Object read(URL sourceUrl, String encoding)
    throws IOException, ParseException
  {
    InputStream in = sourceUrl.openStream();
    try
    {
      return read(in, encoding);
    }
    finally
    {
      try
      {
        in.close();
      }
      catch (Throwable t)
      {}
    }    
  }

  /**
   * Read from the given file using the given character encoding.
   * @param filename the name of the file
   * @param encoding the character encoding of the file
   * @exception IOException if an IO error occurs while opening or
   * reading the file
   * @exception ParseException if a syntax error occurs while parsing the file
   */
  public Object read(String filename, String encoding)
    throws IOException, ParseException
  {
    FileInputStream in = new FileInputStream(filename);
    try
    {
      return read(in, encoding);
    }
    finally
    {
      try
      {
        in.close();
      }
      catch (Throwable t)
      {}
    }
  }

  /**
   * Read from inputstream using the given character encoding.
   * @param in the inputstream to read from
   * @param encoding the character encoding of the input
   * @exception IOException if an IO error occurs while opening or
   * reading the file
   * @exception ParseException if a syntax error occurs while parsing
   * the file
   */
  public Object read(InputStream in, String encoding)
    throws IOException, ParseException
  {
      BufferedReader reader = new BufferedReader(
        new InputStreamReader(in, encoding));
      return read(reader);
  }

  /**
   * Read and parse content from <code>reader</>.
   * @param reader the reader providing the definition stream
   * @exception IOException if an IO error occurs while reading
   * @exception ParseException if a syntax error occurs while parsing
   */
  public abstract Object read(BufferedReader reader)
    throws IOException, ParseException;
}
