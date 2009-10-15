package net.sf.jhunlang.jmorph.sword;

import java.util.StringTokenizer;

import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.InputStreamReader;
import java.io.Serializable;

import java.net.URL;

import net.sf.jhunlang.jmorph.AffixEntry;
import net.sf.jhunlang.jmorph.parser.ParseException;

public class SwordAffixFlagFilter implements Serializable
{
  public final static String NOT = "NOT";
  public final static String FLAGS = "FLAGS";
  public final static String ENDINGS = "ENDINGS";

  public final static String START = "STARTS";
  public final static String CONTAIN = "CONTAINS";
  public final static String EQU = "EQU";

  public final static String[] TYPES = {START, CONTAIN, EQU};

  protected Collection allowedFlags;
  protected Collection forbiddenFlags;

  protected Collection allowedEndings;
  protected Collection forbiddenEndings;

  public SwordAffixFlagFilter()
  {}

  public void read(URL url, String encoding)
    throws IOException, ParseException
  {
    LineNumberReader reader =
      new LineNumberReader(new InputStreamReader(url.openStream(), encoding));
  
    String line;
    while ((line = reader.readLine()) != null)
    {
      if ((line = line.trim()).length() == 0 || line.startsWith("#"))
      {
        continue;
      }

      StringTokenizer st = new StringTokenizer(line);
      if (st.countTokens() < 2)
      {
        throw error(url, reader, line);
      }

      boolean not = false;
      String next = st.nextToken();
      if (next.equals(NOT))
      {
        not = true;
        if (!st.hasMoreTokens())
        {
          throw error(url, reader, line);
        }
        next = st.nextToken();
      }

      if (next.equals(FLAGS))
      {
        if (!st.hasMoreTokens())
        {
          throw error(url, reader, line);
        }
        next = st.nextToken();

        Collection flags;
        if (not)
        {
          if (forbiddenFlags == null)
          {
            forbiddenFlags = new HashSet();
          }
          flags = forbiddenFlags;
        }
        else
        {
          if (allowedFlags == null)
          {
            allowedFlags = new HashSet();
          }
          flags = allowedFlags;
        }

        for(int i = 0; i < next.length(); i++)
        {
          flags.add(new Character(next.charAt(i)));
        }
      }
      else if (next.equals(ENDINGS))
      {
        Collection endings;
        if (not)
        {
          if (forbiddenEndings == null)
          {
            forbiddenEndings = new HashSet();
          }
          endings = forbiddenEndings;
        }
        else
        {
          if (allowedEndings == null)
          {
            allowedEndings = new HashSet();
          }
          endings = allowedEndings;
        }
        endings.add(new Endings(url, reader, line, st));
      }
      else
      {
        throw error(url, reader, line);
      }
    }
  }

  /**
   * Return if flag <code>entry</code> is configured.
   * Return <code>true</code> if <ul>
   * <li>
   *  flag of <code>entry</code> mentioned in a FLAG line or
   * </li>
   * <li>
   *  flag of <code>entry</code> is not mentioned in a NOT FLAG line and
   * </li>
   * <li>
   *  the description of <code>entry</code> is mentioned in an ENDINGS line or
   * </li>
   * <li>
   *  the description of <code>entry</code> is not mentioned in an NOT ENDINGS
   *  line
   * </li>
   * </ul>.
   * @param entry the affix entry
   * @return if entry is admitted by the configuration
   */
  public boolean admit(AffixEntry entry)
  {
    Character fc = new Character((char)entry.getFlag());

    if ((allowedFlags != null && !allowedFlags.contains(fc)) ||
        (forbiddenFlags != null && forbiddenFlags.contains(fc)))
    {
      return false;
    }

    // flags configuration didn't decide
    // use allowedEndings and forbiddenEndings
    Object extension = entry.getExtension();
    if (extension == null ||
        !(extension instanceof SwordAffixExtension))
    {
      return true;
    }

    SwordAffixExtension swExtension = (SwordAffixExtension)extension;

    if (allowedEndings != null)
    {
      Iterator it = allowedEndings.iterator();
      while (it.hasNext())
      {
        Endings endings = (Endings)it.next();
        if (endings.admit(swExtension) != null)
        {
          return true;
        }
      }
    }

    if (forbiddenEndings != null)
    {
      Iterator it = forbiddenEndings.iterator();
      while (it.hasNext())
      {
        Endings endings = (Endings)it.next();
        String admit = endings.admit(swExtension);
        if ( admit != null)
        {
          return false;
        }
      }
    }
    return true;
  }

  private class Endings
  {
    protected int type; // START = 0, CONTAIN = 1, EQU = 2
    protected String[] patterns;

    public Endings(
      URL url, LineNumberReader reader, String line, StringTokenizer st)
      throws ParseException
    {
      if (!st.hasMoreTokens())
      {
        throw error(url, reader, line);
      }

      String typeString = st.nextToken();

      if (typeString.equals(START))
      {
        type = 0;
      }
      else if (typeString.equals(CONTAIN))
      {
        type = 1;
      }
      else if (typeString.equals(EQU))
      {
        type = 2;
      }
      else
      {
        throw error(url, reader, line);
      }

      if (!st.hasMoreTokens())
      {
        throw error(url, reader, line);
      }

      Collection c = new LinkedList(); // pattern collection
      while (st.hasMoreTokens())
      {
        c.add(st.nextToken());
      }
      patterns = (String[])c.toArray(new String[c.size()]);
    }

    public String admit(SwordAffixExtension extension)
    {
      String s = extension.toString();
      // drop format characters (i.e. []{}+) from string form of extension
      String ss = s.replaceAll("[\\[\\]\\{\\}\\+]+", " ").trim();
      for(int i = 0; i < patterns.length; i++)
      {
        switch(type)
        {
          case 0: // START
            if (ss.startsWith(patterns[i]))
            {
              return patterns[i];
            }
            break;
          case 1: // CONTAIN
            if (ss.indexOf(patterns[i]) != -1)
            {
              return patterns[i];
            }
            break;
          case 2: // EQU
            if (ss.equals(patterns[i]))
            {
              return patterns[i];
            }
            break;
        }
      }
      return null;
    }

    public String toString()
    {
      StringBuffer sb = new StringBuffer("[" + TYPES[type] + ",");
      for(int i = 0; i < patterns.length; i++)
      {
        sb.append(" " + patterns[i]);
      }
      sb.append("]");
      return sb.toString();
    }
  }

  protected ParseException error(URL url, LineNumberReader reader, String line)
  {
    return new ParseException(
      "Invalid flagfilter line: " + reader.getLineNumber() + " of " + url);
  }
}