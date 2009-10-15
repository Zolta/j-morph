package net.sf.jhunlang.jmorph;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.TreeMap;

import java.util.regex.Pattern;

import java.io.Serializable;

/**
 * AdffixFlags instance encapsulate testable strings of affix flag characters
 */
public class AffixFlags implements Serializable
{
  protected static int capacity = 1024;
  /**
   * Stores different interned AffixFlag instances
   * @see AffixFlags#getAffixFlags(String)
   */
  protected final static Map flags = new TreeMap();
   /**
   * Convenience constant for the empty flags.
   */
  public static AffixFlags empty = getAffixFlags("");
 
  /**
   * The <code>String</code> representation of the affix flags
   */
  protected String flagstring;
  /**
   * Hack for factor.
   */
  protected String filteredFlagstring;
  /**
   * The ordered <code>Set</code> representation of the affix flags
   */  
  protected Set charset;
  /**
   * The ordered <code>String</code> representation of the affix flags
   */
  protected String intern;
  /**
   * Bitmap of the affix flags
   */
  protected int[] flagmap;
  
  public static void setFlagCapacity(int cap)
  {
    capacity = cap;
    if (empty.flagmap.length != capacity / 32)
    {
      empty.flagmap = new int[capacity / 32]; 
    }
  }

  protected AffixFlags(String flagstring)
  {
    this.flagstring = flagstring;
    flagmap = new int[capacity/32];
    
    char[] flagCharacters = flagstring.toCharArray();

    for(int i = flagCharacters.length - 1; i >= 0; i--)
    {
      try
      {
        int flag = flagCharacters[i];
        flagmap[flag/32] |= 1 << ((flag % 32));
      }
      catch (Throwable t)
      {
        throw new RuntimeException("FlagCharacters: flag too big: >>" +
          (int)flagCharacters[i] + ": " +
          i + "th >>" + (int)flagCharacters[i] + "<< " + t);
      }
    }
  }
  
  public boolean isEmpty()
  {
    return this == empty;
  }

  /**
   * Return if {@link #flagmap} contains <code>flag</code>
   * @param flag the flag to check
   * @return if <code>flagmap</code> containsc <code>flag</code>
   */
  public boolean hasFlag(int flag)
  {
    if (flag/32 >= flagmap.length)
    {
      System.out.println(flag + ": " + (flag/32) + " >= " + capacity +
        ", " + flagmap.length + ", " + isEmpty());
    }
    return (flagmap[flag/32] & (1 << (flag % 32))) != 0;
  }

  /**
   * Return the <code>String</code> representation of the affix flags
   * @return the affix flags
   */
  public String getFlagString()
  {
    return flagstring;
  }

  public String getFilteredFlagString(String filter)
  {
    return filteredFlagstring == null ?
      (filteredFlagstring = flagstring.replaceAll(filter, "")) :
      filteredFlagstring;
  }

  public AffixFlags add(AffixFlags flags)
  {
    return getAffixFlags(flagstring + flags.getFlagString());
  }

  public AffixFlags remove(AffixFlags flags)
  {
    Set remove = flags.order();
    StringBuffer sb = new StringBuffer();
    for(int i = 0; i < flagstring.length(); i++)
    {
      if (!remove.contains(new Character(flagstring.charAt(i))))
      {
        sb.append(flagstring.charAt(i));
      }
    }
    return getAffixFlags(new String(sb));
  }

  public AffixFlags remove(char flag)
  {
    int index = flagstring.indexOf(flag);
    if (index != -1)
    {
      return getAffixFlags(flagstring.substring(0, index) +
        flagstring.substring(index + 1));
    }
    else
    {
      return this;
    }
  }

  public AffixFlags add(char flag)
  {
    int index = flagstring.indexOf(flag);
    if (index != -1)
    {
      return this; // already contains flag
    }
    else
    {
      return getAffixFlags(flagstring + flag);
    }
  }

  public AffixFlags remove(Pattern remove)
  {
    return remove == null ? this :
      getAffixFlags(remove.matcher(flagstring).replaceAll(""));
  }

  /**
   * Arrange the flag characters in alphabetical order and return the
   * resulting <code>Set</code> (using sort of Arrays maybe better).
   * @return the set containing the flag characters in alphabetical order
   */
  public Set order()
  {
    if (charset == null)
    {
      charset = order(flagstring);
    }
    return charset;
  }
  
  /**
   * Arrange the flag characters in alphabetical order and return the
   * resulting String (using sort of Arrays maybe better).
   * @return the string containing the flag characters in alphabetical order
   */
  public String intern()
  {
    if (intern == null)
    {
      intern = intern(order());
    }
    return intern;
  }
  
  public boolean contains(AffixFlags flags)
  {
    String s0 = getFlagString(); 
    String s1 = flags.getFlagString(); 
    if (s0.length() < s1.length())
    {
      return false;
    }
    for(int i = 0; i < s1.length(); i++)
    {
      //for(char c : s1.toCharArray())
      if (s0.indexOf(s1.charAt(i)) == -1)
      {
        return false;
      }
    }
    return true;
  }

  /**
   * Return the ordered set with no duplicates of characters of <code>s</code>.
   * @param s
   * @return the ordered set of characters of <code>s</code>
   */
  private static Set order(String s)
  {
    TreeSet charset = new TreeSet();
    for(int i= 0; i < s.length(); i++)
    {
      charset.add(new Character(s.charAt(i)));
    }
    return charset;
  }
  
  /**
   * Return the String of characters of the set <code>s</code>.
   * @param s
   * @return the ordered set of characters of <code>s</code>
   */
  private static String intern(Set s)
  {
    StringBuffer sb = new StringBuffer();
    Iterator it = s.iterator();
    while (it.hasNext())
    {
      sb.append(((Character)it.next()).charValue());
    }
    return new String(sb);
  }
  
  public static AffixFlags getAffixFlags(char[] flagCharacters)
  {
    return getAffixFlags(new String(flagCharacters));
  }

  public static AffixFlags getAffixFlags(String flagstring)
  {
    flagstring = intern(order(flagstring));
    AffixFlags flag = (AffixFlags)flags.get(flagstring);
    if (flag == null)
    {
      flag = new AffixFlags(flagstring);
      flags.put(flagstring, flag); 
    }
    return flag;
  }
}