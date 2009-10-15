package net.sf.jhunlang.jmorph.name;

import java.util.StringTokenizer;
import java.util.NoSuchElementException;

import java.io.LineNumberReader;

import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.WordList;
import net.sf.jhunlang.jmorph.parser.ListReader;
import net.sf.jhunlang.jmorph.parser.ParseException;

/**
 * NameReader stands for reading namelist files. The lines specify
 * names as follows:<p>
 *   ORIGINAL-NAME LANGUAGE-VARIATION where<p>
 *   ORIGINAL-NAME = name [/affix-flags] and<p>
 *   LANGUAGE-VARIATION = language/transcription/affix-flags.
 */
public class NameReader extends ListReader
{
  public final String SEPARATOR = "/";

  protected DictEntry parseLine(
    WordList wl, LineNumberReader reader, String line)
    throws ParseException
  {
    StringTokenizer st = new StringTokenizer(line, "\t");

    try
    {
      // original name and optionally the flags
      String word = st.nextToken();
      StringTokenizer wst = new StringTokenizer(word, SEPARATOR);
      word = wst.nextToken();

      StringTokenizer wordst = new StringTokenizer(word);

      // accomoding Tokenise take only last subword 
      while (wordst.hasMoreTokens())
      {
        word = wordst.nextToken();
      }

      // if no more tokens then the original name alone
      if (!st.hasMoreTokens())
      {
        // if there are no more tokens then original with no flags
        if (!wst.hasMoreTokens())
        {
          if (dicts.get(word) == null)
          {
            // create and return NameEntry without flags -
            //   it will admit all flags
            return new NameEntry(word);
          }
          else
          {
            return null;
          }
        }
        else
        {
          char[] flags = wst.nextToken().toCharArray();
          return new NameEntry(word, flags);
        }
      }

      MultipleEntry entry = wst.hasMoreTokens() ?
        new MultipleEntry(word, wst.nextToken().toCharArray()) :
        new MultipleEntry(word); 

      while(st.hasMoreTokens())
      {
        word = st.nextToken();
        wst = new StringTokenizer(word, SEPARATOR);
        String language = wst.nextToken();
        String transcription = wst.nextToken();
        char[] flags = wst.nextToken().toCharArray();
        new LanguageEntry(transcription, flags, entry, language);
      }
      return entry;
    }
    catch (NoSuchElementException nex)
    {
      throw new ParseException(
        nex + " at line " + reader.getLineNumber() + " of " + definition);
    }
  }
}
