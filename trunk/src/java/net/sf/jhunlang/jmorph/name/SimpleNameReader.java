package net.sf.jhunlang.jmorph.name;

import java.util.StringTokenizer;
import java.util.NoSuchElementException;

import java.io.LineNumberReader;

import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.WordList;
import net.sf.jhunlang.jmorph.parser.ListReader;
import net.sf.jhunlang.jmorph.parser.ParseException;

/**
 * SimpleNameReader stands for reading simple namelist files.
 * The lines might specify names of multiple words; SimpleNameReader picks
 * up the last of them.
 */
public class SimpleNameReader extends ListReader
{
  /**
   * Create and return a {@link NameEntry} for the last word of the
   * <code>line</code>.
   * @param wl the {@link WordList} instance the returned {@link DictEntry}
   * will be added to
   * @param reader the reader that has read <code>line</code>
   * @param line the line to parse
   * @return a new {@link NameEntry} for the last word of <code>line</code> 
   */
  protected DictEntry parseLine(
    WordList wl, LineNumberReader reader, String line)
    throws ParseException
  {
    StringTokenizer st = new StringTokenizer(line, " \t");
    try
    {
      String word = st.nextToken();
      while (st.hasMoreTokens())
      {
        word = st.nextToken();
      }
      return new NameEntry(word);
    }
    catch (NoSuchElementException nex)
    {
      throw new ParseException(
        nex + " at line " + reader.getLineNumber() + " of " + definition);
    }
  }
}
