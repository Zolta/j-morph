package net.sf.jhunlang.jmorph.parser;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.StringTokenizer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import net.sf.jhunlang.jmorph.Affix;
import net.sf.jhunlang.jmorph.AffixEntry;
import net.sf.jhunlang.jmorph.AnyCharacterCondition;
import net.sf.jhunlang.jmorph.CharacterCondition;
import net.sf.jhunlang.jmorph.Condition;
import net.sf.jhunlang.jmorph.Prefix;
import net.sf.jhunlang.jmorph.RangeCondition;
import net.sf.jhunlang.jmorph.Rules;
import net.sf.jhunlang.jmorph.Suffix;

/**
 * AffixReader stands for reading the affix definition file and
 * building a Rules instance storing the rules.
 */
public class AffixReader extends AbstractReader implements AffixConstants
{
  /**
   * The rules read form the affix file.
   */
  protected Rules rules;
  /**
   * Tells if ever seen an affix line
   */
  protected boolean skipping;

  /**
   * Return the affix rules
   * @return the affix rules
   */
  public Rules getRules()
  {
    return rules;
  }

  /**
   * Read affix definitions from the given reader and build rules.
   * @param reader the reader providing the definition stream
   * @throws IOException if an IO error occurs while reading
   * @throws ParseException if a syntax error occurs while parsing
   */
  public Object read(BufferedReader reader)
    throws IOException, ParseException
  {
    Parser parser = new Parser();
    rules = createRules();

    String line;
    // tell skip all lines until first affix
    skipping = true;
    try
    {
      while ((line = reader.readLine()) != null)
      {
        parser.setLine(line);
        line = comment(parser);
        if (line.length() == 0 || parseCompound(parser))
        {
          continue;
        }
        Affix affix = parseAffix(parser);
        if (affix != null)
        {
          int size = affix.getEntries().length;
          for(int i = 0; i < size;)
          {
            parser.setLine(reader.readLine());
            if ((line = comment(parser)).length() == 0)
            {
              continue;
            }

            AffixEntry entry = parseAffixEntry(parser, affix, i);
            affix.addEntry(entry);
            i++;
          }
          affix.done();
          rules.add(affix);
        }
      }
      done();
      return rules;
    }
    catch (IOException iox)
    {
      throw iox;
    }
    catch (ParseException px)
    {
      throw px;
    }
    catch (Throwable t)
    {
      throw parser.error("Unexpected error", t);
    }
  }

  public void done()
  {
    rules.done();
  }

  /** 
   * Parse current line of <code>parser</code> if it contains a
   * {@link AffixConstants#COMPOUND} definition. Set the appropriate compound
   * flag on {@link #rules} and return true if yes, return false otherwise.
   * @param parser the parser of the line
   * @return if the current line contains a compound flag definition
   * @throws ParseException if the line contains an erroneous or unexpected definition
   */
  protected boolean parseCompound(Parser parser)
    throws ParseException
  {
    String line = parser.getLine();

    if (line.startsWith(COMPOUND))
    {
      if (!skipping)
      {
        throw new ParseException(
          "COMPOUND after affix definition is not allowed at line " +
          parser.getLineNumber());
      }

      StringTokenizer st = new StringTokenizer(line);
      st.nextToken(); // skip COMPOUND keyword

      if (!st.hasMoreTokens())
      {
        throw new ParseException("Unexpected end of line at line " +
          parser.getLineNumber());
      }

      String flagstring = st.nextToken();
      char flag = flagstring.charAt(0);

      if (line.startsWith(COMPOUNDFLAG))
      {
        rules.setCompound(flag);
      }
      else if (line.startsWith(COMPOUNDFIRST))
      {
        rules.setCompoundFirst(flag);
      }
      else if (line.startsWith(COMPOUNDLAST))
      {
        rules.setCompoundLast(flag);
      }
      else if (line.startsWith(COMPOUNDFORBIDDEN))
      {
        rules.setCompoundForbidden(flag);
      }
      else if (line.startsWith(COMPOUNDWORD))
      {
        int number;
        try
        {
          number = Integer.parseInt(flagstring);
        }
        catch (NumberFormatException nfx)
        {
          throw new ParseException("Not a number at line " +
            parser.getLineNumber());
        }

        if (!st.hasMoreTokens())
        {
          throw new ParseException("Unexpected end of line at line " +
            parser.getLineNumber());
        }

        flagstring = st.nextToken();
        flag = flagstring.charAt(0);

        rules.setCompoundWord(number, flag);
      }
      // ignore all other COMPOUND keywords like COMPOUNDMIN and COMPOUNDSYLLABLE
      return true;
    }
    return false;
  }

  /**
   * Read an affix definition from the current line of <code>parser</code> and
   * return the resulting {@link Affix} instance. Return null if the line is
   * a comment line.
   * @param parser the parser of the line
   * @return the <code>Affix</code> instance read from the current line
   * @throws ParseException if the line contains an erroneous definition
   */
  protected Affix parseAffix(Parser parser)
    throws ParseException
  {    
    // parse line if not empty; FLAGS, NAME, YESNO, num. of entries
    int type = 0;
    // skip lines until first affix
    try
    {
      type = parser.string(FLAGS);
      skipping = false;
    }
    catch (ParseException psx)
    {
      /*
      if (!skipping)
      {
        throw psx;
      }
      */
      return null;
    }
    char name = parser.character();
    boolean cross = parser.string(CROSS) == 0;
    int size = parser.integer();

    parser.lineDone();

    if (type == 0)
    {
      return new Suffix(name, cross, size);
    }
    else
    {
      return new Prefix(name, cross, size);
    }
  }

  /**
   * Read affix entry definitions from the current line of <code>parser</code>
   * and return the resulting array of {@link AffixEntry} instances.
   * Return null if the line is a comment line. Add the created AffixEntry's
   * to <code>affix</code> at <code>index</code>.
   * @param parser the parser of the line
   * @param affix the affix group of the entry
   * @param index the index of the entry in its group
   * @return the arraya of <code>AffixEntry</code> instances read from the
   * current line
   * @throws ParseException if the line contains an erroneous definition
   */
  protected AffixEntry parseAffixEntry(Parser parser, Affix affix, int index)
    throws ParseException
  {    
    // check SFX|PFX and name
    String type = (affix instanceof Prefix) ? PREFIX : SUFFIX;
    parser.string(type);
    parser.character(affix.getName());
    // get strip; 0 stands for none
    String strip = nullstring(parser);
    // get append; 0 stands for none
    String append = nullstring(parser);
    // get conditions; . stands for none
    Condition[] conditions = conditions(parser);
    // check if there are more tokens
    AffixEntry entry =
      affix.createEntry(rules, index, conditions, strip, append);

    entry = parseExtension(parser, entry);

    // check if there remained unparsed tokens
    parser.lineDone();
    return entry;
  }

  /**
   * Create a {@link FakeExtension} for the remainder of the current line and
   * add it to <code>entry</code> as extension. 
   * @param parser the parser of the current line
   * @param entry the affix entry built for the current line
   * @return the affix entry
   * @throws ParseException
   */
  protected AffixEntry parseExtension(Parser parser, AffixEntry entry)
    throws ParseException
  {
    // check if there remained unparsed tokens
    String remainder = parser.remainder();
    if (remainder != null)
    {
      entry.setExtension(new FakeExtension(remainder));
    }
    return entry;
  }

  protected Rules createRules()
    throws IOException, ParseException
  {
    return new Rules();
  }

  /**
   * Read the condition part of from the current line of <code>parser</code>
   * and return the resulting {@link Condition} array.
   * @param parser the parser of the line
   * @return the <code>Conadition</code> array read from the current line
   * @throws ParseException if the line contains an erroneous definition
   */
  public static Condition[] conditions(Parser parser)
    throws ParseException
  {
    String s = parser.string();
    return conditions(parser, s);
  }
  
  public static Condition[] conditions(Parser parser, String s)
    throws ParseException
  {
    int length = s.length();
    // szoszablya 1.0 affix file contains superfluous dots
    if (s.charAt(0) == NOCOND)
    {
      if (length == 1)
      {
        return new Condition[0];
      }
      else
      {
        s = s.substring(1, s.length());
        length = s.length();
      }
    }

    LinkedList l = new LinkedList();
    
    LinkedHashSet range = null;
    boolean maynot = true;
    boolean not = false;

    for(int i = 0; i < length; i++)
    {
      char c = s.charAt(i);
      switch (c)
      {
        case RANGE_START:
          if (range != null)
          {
            throw parser.error("Unexpected start of range");
          }
          else
          {
            range = new LinkedHashSet();
          }
          break;

        case RANGE_END:
          if (range == null)
          {
            throw parser.error("Unexpected end of range");
          }
          else
          {
            int size = range.size();
            if (size == 0)
            {
              throw parser.error("Empty character range is not allowed");
            }
            char[] cr = new char[size];
            Iterator it = range.iterator();
            size = 0;
            while (it.hasNext())
            {
              cr[size++] = ((Character)it.next()).charValue();
            }

            Condition rc = new RangeCondition(cr, not);
            l.add(rc);
            
            range = null;
            maynot = true;
            not = false;
          }
          break;

        case NEG_COND:
          if (!maynot)
          {
            throw parser.error("Unexpected negation");
          }
          maynot = false;
          not = true;
          break;

        default:
          if (range != null)
          {
            Character cc = new Character(Character.toLowerCase(c));
            range.add(cc);
            maynot = false;
          }
          else
          {
            Condition cc =
              c == NOCOND ?
                (not ? AnyCharacterCondition.NOT : AnyCharacterCondition.YES) :
                (Condition)new CharacterCondition(c, not);
            l.add(cc);
            maynot = true;
            not = false;
          }
          break;
      }
    }
    return (Condition[])l.toArray(new Condition[l.size()]);
  }

  /**
   * Return the next token of the current line of <code>parser</code> or the
   * {@link AffixConstants#EMPTY} String if the token represents NULL.
   * @param parser the parser holding the current line
   * @return the <code>token</code> read from the current line
   * @throws ParseException if the line has no more tokens
   */
  protected String nullstring(Parser parser)
    throws ParseException
  {
    String s = parser.string();
    return s.length() == 1 && s.charAt(0) == NULL ? EMPTY : s;
  }

  /**
   * Trim current line of <code>parser</code> and cut off the comment from
   * the end of it. Return the resulted String.
   * @param parser the parser holding the current line
   * @return the trimmed line
   */
  protected String comment(Parser parser)
  {
    String line = parser.getLine().trim();
    int comment = line.indexOf(COMMENT);
    if (comment != -1)
    {
      line = line.substring(0, comment).trim();
    }
    return line;
  }

  protected void test(String[] args)
    throws IOException, ParseException
  {
    if (args.length == 0)
    {
      System.out.println("Usage: " +
        getClass().getName() + " affix-file [encoding]");
      System.exit(2);
    }

    String encoding = args.length < 2 ? DEFAULT_ENCODING : args[1];

    BufferedReader br = new BufferedReader(new InputStreamReader(
      new FileInputStream(args[0]), encoding));
    read(br);
  }
  
  /**
   * Basic test of AffixReader.<p>
   * The command line parameters must specify the affix file and optionally
   * the encoding. The default encoding is ISO-8859.
   * @param args the command line parameters
   */
  public static void main(String[] args)
    throws Exception
  {
    new AffixReader().test(args);
  }
}
