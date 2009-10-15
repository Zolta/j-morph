package net.sf.jhunlang.jmorph.sword.parser;

import java.util.StringTokenizer;
import java.util.Collection;
import java.util.LinkedList;

import java.io.LineNumberReader;

import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.WordList;
import net.sf.jhunlang.jmorph.parser.DictionaryReader;
import net.sf.jhunlang.jmorph.parser.ParseException;
import net.sf.jhunlang.jmorph.sword.SwordDict;
import net.sf.jhunlang.jmorph.sword.SwordEntry;
import net.sf.jhunlang.jmorph.sword.SwordEntryExtension;

/**
 * SwordReader stands for reading the szoszablya dictionary file and building a
 * SwordDict instance storing words.
 * Ignore COMPOUNDMIN flag.
 */
public class SwordReader extends DictionaryReader
{
  /**
   * The collection of string s marking derivatives; an affix rule with
   * morphological description of x_Y_z is a derivative if Y is in this
   * collection.
   */
  public static Collection derivatives = new LinkedList();

  /**
   * Return if <code>morph</code> is a derivative i.e. if {@link #derivatives}
   * contains it. The present implementation returns true if <code>morph</code>
   * is 'PREF', the szoszablya convention for preverbs.
   * @param morph
   * @return if <code>morph</code> marks a derivative
   */
  public static boolean derivative(String morph)
  {
    return
      morph.equals(SwordExtensionParser.PREF) || derivatives.contains(morph);
  }

  /**
   * Create and return a {@link SwordDict} instance for the dictionary words.
   * @return a new {@link SwordDict} instance
   */
  protected WordList createWordList()
  {
    return new SwordDict();
  }

  /**
   * Return {@link SwordEntry} built from <code>line</code>. The line specifies
   * a dictionary word by one of the following:
   * <ul>
   * <li>word/flags [pos]</li>
   * <li>word/flags stem[pos]</li>
   * <li>word/flags [pos]{+[affixtype]...}</li>
   * <li>word/flags stem[pos]{+[affixtype]...}</li>
   * <li>word stem[pos]{+[affixtype]...}</li>
   * </ul>
   * @param wl the WordList
   * @param lr the reader
   * @param line the line to parse
   */
  protected DictEntry parseLine(WordList wl, LineNumberReader lr, String line)
    throws ParseException
  {
    SwordDict dict = (SwordDict)wl;

    // get word part until first space. Word part is word or word/flags
    // the remainder starts with either stem or [, stem lasting to [
    StringTokenizer st = new StringTokenizer(line);
    String wordPart = st.nextToken();

    char[] flagCharacters;
    String word;

    int index = wordPart.indexOf(SEPARATOR);

    // no flags if no SEPARATOR
    if (index == -1)
    {
      flagCharacters = NO_FLAGS;
      word = wordPart;
    }
    else
    {
      flagCharacters = wordPart.substring(index + 1).toCharArray();
      word = wordPart.substring(0, index);
    }
    if (st.hasMoreTokens())
    {
      String descriptionPart = st.nextToken();

      SwordEntryExtension ext = new SwordEntryExtension();
      SwordEntryExtensionParser extParser = new SwordEntryExtensionParser(ext);
      
      parser.setTokenizer(new StringTokenizer(descriptionPart));

      String root = extParser.parseDescription(parser, derivatives);

      SwordEntry entry = new SwordEntry(word, flagCharacters, ext);

      if (root != null)
      {
        dict.setRoot(entry, root);
      }
      return entry;
    }
    else
    {
      return new SwordEntry(word, flagCharacters);
    }
  }

  protected void done(WordList wl)
  {
    super.done(wl);
  }
}