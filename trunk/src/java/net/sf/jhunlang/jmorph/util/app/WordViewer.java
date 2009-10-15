package net.sf.jhunlang.jmorph.util.app;

import java.util.Iterator;
import java.util.Collection;
import java.util.Map;

import javax.swing.JFrame;

import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.Dictionaries;
import net.sf.jhunlang.jmorph.WordList;
import net.sf.jhunlang.jmorph.factory.Loader;

public class WordViewer extends TextViewer
{
  protected int count = 0;
  protected int hcount = 0;

  public WordViewer(Dictionaries dictionaries)
  {
    // create TextViewer without 'go to line' and with search
    super(false, true);

    Iterator it = dictionaries.iterator();

    while (it.hasNext())
    {
      WordList wl = (WordList)it.next();
      Map map = wl.getWords();
      Iterator wit = map.values().iterator();
      while(wit.hasNext())
      {
        Object o = wit.next();

        if (o instanceof DictEntry)
        {
          DictEntry we = (DictEntry)o;
          count++;
          addEntry("", we);
        }
        else
        {
          Iterator mit = ((Map)o).values().iterator();
          while (mit.hasNext())
          {
            addEntry("", (DictEntry)mit.next());
          }
        }
      }
    }
    setTitle(count + " words");
    pack();
  }

  protected void addEntry(String head, DictEntry we)
  {
    add(head + toString(we));
    Collection homonyms = we.getHomonyms();
    Iterator hit = homonyms.iterator();
    while (hit.hasNext())
    {
      hcount++;
      DictEntry he = (DictEntry )hit.next();
      add("    " + toString(he));
    }
  }

  protected String toString(DictEntry we)
  {
    StringBuffer sb = new StringBuffer(we.contentString());

    if (we.getRootEntry() != null)
    {
      sb.append(" <== " + we.getRootEntry().contentString());
    }

    if (we.getHomonyms().size() > 0)
    {
      sb.append(" " + we.getHomonyms().size() + " homonyms:");
    }
    return sb.toString();
  }

  /**
   * Starts the application.<p>
   * The command line parameter specifies the definition file.
   * Read and parse the definition file, build Rules and Dictionaries and
   * create a AnalyserImpl. Create and show a Analysis.<p>
   * Errors are printed to the standard error.
   */
  public static void main(String[] args)
    throws Exception
  {
    Loader loader = new Loader();
    loader.loadDictionaries(args);
    WordViewer wv = new WordViewer(loader.getDic());
    wv.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    wv.setVisible(true);
  }
}
