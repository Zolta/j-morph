package net.sf.jhunlang.jmorph.sword;

import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.WordList;

/**
 * SwordDict represents szoszablya dictionaries.
 */
public class SwordDict extends WordList
{
  /**
   * DictEntry's having root words.
   */
  protected Map rooted = new HashMap();

  /**
   * Create a new SwordDict instance
   */
  public SwordDict()
  {
    super();
  }

  /**
   * Record <code>entry</code> as having root <code>root</code>.
   * @param entry the 'rooted' entry
   * @param root the root word
   */
  public void setRoot(SwordEntry entry, String root)
  {
    rooted.put(entry, root);
  }

  /**
   * Set root of entries collected in <code>rooted</code>. <code>rooted</code>
   * associates entries with their root words. The root word of each rooted entry
   * is resolved to a dictionary entry and this entry is set as the root of the
   * rooted entry. For each root entry associate the entry with the collection of entries
   * with the entry as root and put these associations to <code>reverseRoots</code>.
   */
  public void sync()
  {
    Iterator it = rooted.entrySet().iterator();
    while (it.hasNext())
    {
      Map.Entry entry = (Map.Entry)it.next();
      SwordEntry s = (SwordEntry)entry.getKey();
      String root = (String)entry.getValue();
      DictEntry rootEntry = get(root);

      s.setRoot(rootEntry);

      Collection c = (Collection)reverseRoots.get(rootEntry);
      if (c == null)
      {
        c = new ArrayList(1);
        reverseRoots.put(rootEntry, c);
      }
      c.add(s);
    }
  }
}
