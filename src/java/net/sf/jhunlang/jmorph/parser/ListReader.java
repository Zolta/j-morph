package net.sf.jhunlang.jmorph.parser;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.LineNumberReader;

import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.Dictionaries;
import net.sf.jhunlang.jmorph.WordList;


/**
 * DictionaryReader stands for reading the dictionary file and
 * building a Dict instance storing words.
 */
public abstract class ListReader extends AbstractReader
{
  protected Dictionaries dicts;

  protected Parser parser;

  public void setDictionaries(Dictionaries dicts)
  {
    this.dicts = dicts;
  }

  public Dictionaries getDictionaries()
  {
    return dicts;
  }

  /**
   * Read word definitions from the given reader and store them into the
   * dictionary. Call {@link #createWordList(LineNumberReader)} to create the
   * {@link WordList} storing the words read via <code>reader</code>.
   * Call {@link #parseLine(WordList, LineNumberReader, String)} to create
   * a {@link DictEntry} for each not empty line.
   * @param reader the reader providing the definition stream
   * @exception IOException if an IO error occurs while reading
   * @exception ParseException if a syntax error occurs while parsing
   */
  public Object read(BufferedReader reader)
    throws IOException, ParseException
  {
    LineNumberReader lineReader = new LineNumberReader(reader);
    WordList wl = createWordList(lineReader);

    if (dicts != null)
    {
      dicts.add(wl);
    }

    parser = new Parser();
    String line;
    try
    {
      while ((line = lineReader.readLine()) != null)
      {
        parser.setLine(line);
        String word = line.trim();
        if (word.length() == 0)
        {
          continue;
        }
        DictEntry entry = parseLine(wl, lineReader, line);
        if (entry != null && entry.getWord().trim().length() != 0)
        {
          wl.add(entry);
        }
      }
    }
    catch (ParseException pex)
    {
      throw pex;
    }
    catch (Throwable t)
    {
      throw new ParseException(
        "Internal error in line " + lineReader.getLineNumber(), t);
    }
    done(wl);
    return wl;
  }

  protected abstract DictEntry parseLine(
    WordList wl, LineNumberReader reader, String line)
    throws IOException, ParseException;

  protected WordList createWordList(LineNumberReader reader)
    throws IOException, ParseException
  {
    return new WordList();
  }

  /**
   * Calls {@link WordList#sync} of <code>wl</code>.
   * @param wl the WordList
   */
  protected void done(WordList wl)
  {
    wl.sync();
  }
}
