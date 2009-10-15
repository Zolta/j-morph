package net.sf.jhunlang.jmorph.util.app;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import net.sf.jhunlang.jmorph.Dict;
import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.parser.DictionaryReader;
import net.sf.jhunlang.jmorph.parser.ParseException;

/**
 * Given a dictionary file and an input file, AffixSample replaces the
 * sample words of the input with the affix flags found in the dictionary.
 */
public class AffixSample
{
  final static String COMMENT = "#";
  final static String SAMPLE = "@";

  /**
   * Replaces sample words in the sample file and writes the result to the
   * standard output. Sample words are those starting with '@'.
   * Command line arguments are the dictionary, its encoding,
   * the file with sample words and its encoding.
   * Prints unmathced sample words to the standard error
   */
  public static void main(String[] args)
  {
    if (args.length < 4)
    {
      System.out.println(
        "Usage: AffixSample dictionary, encoding sample file, encoding");
      System.exit(2);
    }
    try
    {
      String dictFile = args[0];
      String dictEncoding = args[1];
      String sampleFile = args[2];
      String encoding = args[3];

      List l = replaceSamples(dictFile, dictEncoding, sampleFile, encoding);

      Iterator it = l.iterator();
      while (it.hasNext())
      {
        System.err.println(it.next());
      }
    }
    catch (Throwable t)
    {
      t.printStackTrace();
    }
  }

  /**
   * Build dictionary from <code>dfile</code> and replace sample words of
   * <code>sfile</code> with the affix flags associated with it in the
   * dictionary. Write both the modified and the unchanged lines to the
   * standard output.  Sample words are those starting with '@' in non
   * comment lines.
   * Return the list of unmatched sample words.
   * @param dfile the dictionary file
   * @param dencoding the character encoding of the dictionary file
   * @param sfile the sample file
   * @param sencoding the character encoding of the sample file
   * @return the list of sample words not found in the dictionary
   * @throws IOException if an IO error occurs while reading the dictionary
   * or the sample file
   * @throws ParseException if the dictionary contains erroneous lines
   */
  public static List replaceSamples(
    String dfile, String dencoding, String sfile, String sencoding)
    throws IOException, ParseException
  {
    List l = new LinkedList();

    Dict dict = (Dict)new DictionaryReader().read(dfile, dencoding);

    FileInputStream fin = new FileInputStream(sfile);
    BufferedReader reader =
      new BufferedReader(new InputStreamReader(fin, sencoding));

    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
      System.out, sencoding));

    String line;

    while ((line = reader.readLine()) != null)
    {
      line = line.trim();
      // write empty and comment line as is
      if (line.length() == 0 || line.startsWith(COMMENT))
      {
        out.write(line + "\n");
        continue;
      }

      StringTokenizer st = new StringTokenizer(line);
      boolean changed = false;

      StringBuffer sb = new StringBuffer();

      while (st.hasMoreTokens())
      {
        String sample = st.nextToken();
        if (!sample.startsWith(SAMPLE))
        {
          sb.append(sample);
          if (st.hasMoreTokens())
          {
            sb.append(" ");
          }
          continue;
        }

        DictEntry entry = dict.get(sample.substring(1));

        if (entry != null)
        {
          String flags = entry.getFlagString();
          sb.append(flags);
          if (st.hasMoreTokens())
          {
            sb.append(" ");
          }
          changed = true;
        }
        else
        {
          l.add(sample);
        }
      }

      if (changed)
      {
        out.write(sb + "\n");
      }
      else
      {
        out.write(line + "\n");
      }
    }
    out.close();
    reader.close();
    return l;
  }
}
