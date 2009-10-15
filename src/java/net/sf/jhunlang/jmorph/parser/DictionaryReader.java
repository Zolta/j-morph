package net.sf.jhunlang.jmorph.parser;


import java.util.StringTokenizer;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;

import net.sf.jhunlang.jmorph.Dict;
import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.ExceptionEntry;
import net.sf.jhunlang.jmorph.WordList;
import net.sf.jhunlang.jmorph.XWordEntry;

/**
 * DictionaryReader stands for reading the hunspell dictionary file and
 * building a Dict instance storing words.
 * Ignore COMPOUNDMIN flag.
 */
public class DictionaryReader extends ListReader implements DictConstants
{
  /**
   * The separator between the word and the flags in the definition
   */
  public final static char SEPARATOR = '/';
  /**
   * Empty flag characters
   */
  public final static char[] NO_FLAGS = new char[0];

  /**
   * Create and return a {@link WordList} for words read from
   * <code>reader</code>.
   * @param reader the reader of the dictionary file
   * @return a new {@link WordList} instance
   * @throws IOException if cannot read the first line
   * @throws ParseException if the dictionary file is empty or its first line
   * doesnt tell the number of dictionary words
   */
  protected WordList createWordList(LineNumberReader reader)
    throws IOException, ParseException
  {
    String line = reader.readLine();
    if (line == null)
    {
      throw new ParseException("Unexpected end of file at line 1");
    }
    line = line.trim();
    try
    {
      Integer.parseInt(line);
      return createWordList();
    }
    catch (Throwable t)
    {
      throw new ParseException("Expected number of words: " + line);
    }
  }

  /**
   * Create and return a {@link WordList}.
   * @return a new {@link WordList} instance
   */
  protected WordList createWordList()
  {
    return new Dict();
  }

  /**
   * Return {@link DictEntry} built from <code>line</code>.
   * @param wl the WordList the returned will be added to
   * @param lr the reader
   * @param line the line to parse
   */
  protected DictEntry parseLine(WordList wl, LineNumberReader lr, String line)
    throws ParseException
  {
    Dict dict = (Dict)wl;

    int index = line.indexOf(SEPARATOR);

    char[] flagCharacters;
    String word;
    StringTokenizer st;
    
    if (index == -1)
    {
      word = line;
      flagCharacters = NO_FLAGS;
      st = new StringTokenizer(line);
    }
    else
    {
      word = line.substring(0, index);
      String flags = line.substring(index + 1);
      st = new StringTokenizer(flags);
      if (st.hasMoreTokens())
      {
        flags = st.nextToken();
      }
      flagCharacters = flags.toCharArray();
    }
        
    if (word.startsWith(EXCEPTION_START))
    {
      return new ExceptionEntry(
        word.substring(EXCEPTION_START.length()), new String(flagCharacters));
    }

    DictEntry entry = null;

    for(int i = 0; entry == null && i < flagCharacters.length; i++)
    {
      switch (flagCharacters[i])
      {
        case X_FLAG:
          entry = new XWordEntry(word, flagCharacters);
          break;
        case CHUNK_FLAG:
          byte chunk = 0;
          if (i + 1 < flagCharacters.length)
          {
            char c = flagCharacters[i + 1];
            if (c > '0' && c <= '9')
            {
              chunk = (byte)(c - '0');
            }
          }
          entry = new XWordEntry(word, flagCharacters, chunk);
          if (((XWordEntry)entry).chunked())
          {
            DictEntry chunkEntry =
              new DictEntry(entry.getRootWord(), flagCharacters);
            chunkEntry.setRoot(entry);
            //add(chunkEntry);
          }
          break;
        case YIJ_FLAG:
          entry = new DictEntry(word, flagCharacters);
          dict.addY(entry);
          break;

        case FORBIDDEN_FLAG:
          entry = new DictEntry(word, flagCharacters);
          if (word.endsWith("i") || word.endsWith("j"))
          {
            dict.putIJ(word, entry);
          }
          break;
        default:
          entry = new DictEntry(word, flagCharacters);
          break;
      }
    }
    if (entry == null)
    {
      entry = new DictEntry(word, flagCharacters);      
    }
    String remainder = Parser.remainder(st);
    if (remainder.length() > 0)
    {
      entry.setInflexion(new FakeExtension(remainder));
    }
    return entry;
  }

  /**
   * Basic test of DictionaryReader.<p>
   * The command line parameters must specify the dictionary file and
   * optionally the encoding. The default encoding is ISO-8859.
   * @param args the command line parameters
   */
  public static void main(String[] args)
  {
    new DictionaryReader().test(args);
  }

  protected void test(String[] args)
  {
    if (args.length == 0)
    {
      System.out.println("Usage: " +
        getClass().getName() + " dict-file [encoding]");
      System.exit(2);
    }
    String encoding = args.length < 2 ? DEFAULT_ENCODING : args[1];
    try
    {
      BufferedReader br = new BufferedReader(new InputStreamReader(
        new FileInputStream(args[0]), encoding));
      read(br);
    }
    catch (Throwable t)
    {
      t.printStackTrace();
    }
  }
}
