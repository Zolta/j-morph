package net.sf.jhunlang.jmorph.sword;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import java.io.Serializable;

/**
 * POSName instances represent part of speech categories.
 * They are mapped by their names.
 */ 
public class POSName implements Serializable
{
  protected final static Map posNames = new TreeMap();

  public final static POSName empty = getPOSName("");

  protected String name;

  protected POSName(String name)
  {
    this.name = name;
  }

  public static POSName getPOSName(String name)
  {
    POSName posName = (POSName)posNames.get(name);
    if (posName == null)
    {
      posName = new POSName(name);
      posNames.put(name, posName);
    }
    return posName;
  }

  public String getName()
  {
    return name;
  }

  public static Iterator getPOSNameIterator()
  {
    return posNames.values().iterator();
  }

  public String contentString()
  {
    return name;
  }

  public String toString()
  {
    return "<" + contentString() + ">";
  }
}

