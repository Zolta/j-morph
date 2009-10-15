package net.sf.jhunlang.jmorph.name;

import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedList;

public class MultipleEntry extends NameEntry
{
  // LanguageEntry instances
  protected Collection entries = new LinkedList();

  public MultipleEntry(String word, char[] flagCharacters)
  {
    super(word, flagCharacters);
  }

  public MultipleEntry(String word)
  {
    super(word);
  }

  public void addEntry(LanguageEntry entry)
  {
    entries.add(entry);
  }

  public Collection getEntries()
  {
    return entries;
  }

  public String contentString()
  {
    StringBuffer sb = new StringBuffer(super.contentString());
    Iterator it = entries.iterator();
    while (it.hasNext())
    {
      sb.append(", " + it.next());
    }
    return new String(sb);
  }
}