package net.sf.jhunlang.jmorph.sword.parser;

import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.Collection;
import java.util.TreeSet;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.URL;

import net.sf.jhunlang.jmorph.AffixEntry;
import net.sf.jhunlang.jmorph.AffixFlags;
import net.sf.jhunlang.jmorph.Rules;

import net.sf.jhunlang.jmorph.analysis.CompoundAnyController;
import net.sf.jhunlang.jmorph.analysis.CompoundController;
import net.sf.jhunlang.jmorph.analysis.CompoundPOSController;

import net.sf.jhunlang.jmorph.parser.AffixReader;
import net.sf.jhunlang.jmorph.parser.ParseException;
import net.sf.jhunlang.jmorph.parser.Parser;

import net.sf.jhunlang.jmorph.sword.Case;
import net.sf.jhunlang.jmorph.sword.POSName;
import net.sf.jhunlang.jmorph.sword.SimpleDerivative;
import net.sf.jhunlang.jmorph.sword.SwordAffixExtension;
import net.sf.jhunlang.jmorph.sword.SwordAffixFlagFilter;

/**
 * SwordAffixReader stands for reading the szoszablya-specific extension
 * of AffixEntry's.
 */
public class SwordAffixReader extends AffixReader implements Rules.Controller
{
  /**
   * Constant for the 'ignore case' prefix flag.
   */
  public final static char IGNORE_CASE = '"';

  public final static String DERS = "DERIVATIVES";
  public final static String REC = "RECURSION";
  public final static String FLAGS = "FLAGFILTER";
  public final static String COMPOUND_CONTROLLER = "COMPOUND";
  public final static String COMPOUND_ANY = "ANY";

  //protected CompoundPOSController compoundController;
  protected CompoundController compoundController;

  protected Collection derivatives = new TreeSet();

  protected SwordAffixFlagFilter flagFilter = new SwordAffixFlagFilter();

  /**
   * Read derivatives from <code>file</code> and add them to
   * {@link #derivatives}.
   * @param file the file ensliting the derivatives
   * @throws IOException if opening or reading <code>file</code> fails
   */
  public final void readDerivatives(String file)
    throws IOException
  {
    URL fileURL =
      definition == null ? new File(file).toURL() : definition.getFileURL(file);
    
    BufferedReader r =
      new BufferedReader(new InputStreamReader(fileURL.openStream()));
    String line;
    while ((line = r.readLine()) != null)
    {
      line = line.trim();
      if (line.length() > 0)
      {
        derivatives.add(line);
      }
    }
  }

  /**
   * Return if <code>rules</code> should add or ignore <code>entry</code>.
   * Return true if <code>rules</code> is not the root rules, otherwise return
   * if <code>entry</code> is a derivative.
   * @param rules the calling {@link Rules} instance
   * @param entry the affix entry to decide
   * @return if <code>entry</code> should be added to <code>rules</code>
   */
  public boolean add(Rules rules, AffixEntry entry)
  {
    if (rules.hasParentRules())
    {
      return true;
    }
    else
    {
      return !add2sub(rules, entry);
    }
  }

  /**
   * Return if <code>entry</code> should be added to the subrules
   * of <code>rules</code>. Return if <code>entry</code> is a derivative.
   * @param rules the calling {@link Rules} instance
   * @param entry the affix entry to decide
   * @return if <code>entry</code> should be added to subrule of
   * <code>rules</code>
   */
  public boolean add2sub(Rules rules, AffixEntry entry)
  {
    return entry.getExtension().isDerivator();
  }

  protected Rules createRules()
    throws IOException, ParseException
  {
    // the submost rules
    Rules rules = new Rules();

    if (definition != null)
    {
      Collection extensionLines = definition.getExtensionLines();
      Iterator it = extensionLines.iterator();

      while (it.hasNext())
      {
        String line = ((String)it.next()).trim();
        StringTokenizer st = new StringTokenizer(line);

        if (st.countTokens() != 2)
        {
          throw new ParseException("Invalid extensionline " + line);
        }

        String type = st.nextToken();
        if (!st.hasMoreTokens())
        {
          throw new ParseException("Invalid extensionline " + line);
        }

        if (type.equals(COMPOUND_CONTROLLER))
        {
          compoundController = createCompoundController(st.nextToken());
        }
        else if (type.equals(DERS))
        {
          readDerivatives(st.nextToken());
        }
        else if (type.equals(REC))
        {
          int x = Integer.parseInt(st.nextToken());
          while (x-- != 0)
          {
            rules = new Rules(rules, this);
          }
        }
        else if (type.equals(FLAGS))
        {
          flagFilter.read(definition.getFileURL(st.nextToken()), "UTF-8");
        }
      }
    }
    SwordReader.derivatives = derivatives;
    return rules;
  }

  public void done()
  {
    if (compoundController != null)
    {
      compoundController.setFlags(rules);
      rules.setCompoundController(compoundController);
    }
    super.done();
  }

  /**
   * Create and return <code>CompoundPOSController</code>
   * for <code>file</code>
   * @param file the file defining legal part-of-speech pairs for
   * in compound words
   * @return the created <code>CompoundPOSController</code> 
   */
  protected CompoundController createCompoundController(String file)
    throws IOException
  {
    if (file.equals(COMPOUND_ANY))
    {
      return new CompoundAnyController();
    }
    
    CompoundPOSController controller = new CompoundPOSController();

    URL fileURL = definition.getFileURL(file);
    BufferedReader r =
      new BufferedReader(new InputStreamReader(fileURL.openStream()));

    String line;
    while ((line = r.readLine()) != null)
    {
      line = line.trim();
      if (line.length() > 0)
      {
        StringTokenizer st = new StringTokenizer(line);
        String left = st.nextToken();
        String right = st.nextToken();
        
        AffixFlags add = AffixFlags.getAffixFlags("");
        AffixFlags remove = AffixFlags.getAffixFlags("");
        
        for(int i = 0; i < 2 && st.hasMoreTokens(); i++)
        {
          String s = st.nextToken();
          if (s.startsWith("-"))
          {
            remove = AffixFlags.getAffixFlags(s.substring(1));
          }
          else
          {
            add = AffixFlags.getAffixFlags(s.substring(1));
          }
        }
        controller.addCompound(left, right, add, remove);
      }
    }
    return controller;
  }

  /**
   * Parse the extension read from the current line of <code>parser</code>
   * and set the resulted {@link SwordAffixExtensionParser} as the
   * {@link AffixEntry#getExtension()} of <code>entry</code>.
   * Return <code>entry</code> if the extension is legal, return
   * <code>null</code> otherwise.
   * @param parser the parser providing the extension
   * @param entry the affix entry
   * @return <code>entry</code> or <code>null</code> depending on whether
   * or not the extension is legal
   * @see SwordAffixExtensionParser#parseDescription(
   * Parser, Collection, AffixEntry)
   */
  protected AffixEntry parseExtension(Parser parser, AffixEntry entry)
    throws ParseException
  {    
    SwordAffixExtension ext = new SwordAffixExtension();
    SwordAffixExtensionParser extParser = new SwordAffixExtensionParser(ext);

    if (!extParser.parseDescription(parser, derivatives, entry))
    {
      return null;
    }

    entry.setExtension(ext);

    String flags = parser.string(true);
    if (flags != null)
    {
      entry.setFlags(flags.toCharArray());
      if (entry.hasFlag(IGNORE_CASE))
      {
        entry.setIgnoreCase(true);
      }
    }
    // parse line even if entry is not allowed!
    return flagFilter.admit(entry) ? entry : null;
  }

  private void print(String s, Iterator it)
  {
    System.out.println(s);
    int count= 0;
    while (it.hasNext())
    {
      count++;
      System.out.println("  " + it.next());
    }
    System.out.println("There were " + count + " " + s + " instances printed");
  }
  
  protected void test(String[] args)
    throws IOException, ParseException
  {
    readDerivatives("derivatives.lst");
    super.test(args);
    print("POS",
      POSName.getPOSNameIterator());
    print("Cases",
      Case.getCaseIterator());
    print("SimpleDerivatives",
      SimpleDerivative.getSimpleDerivativeIterator());
  }

  /**
   * Basic test of SwordAffixReader.<p>
   * The command line parameters must specify the affix file and optionally
   * the encoding. The default encoding is ISO-8859.
   * @param args the command line parameters
   */
  public static void main(String[] args)
    throws Exception
  {
    new SwordAffixReader().test(args);
  }
}
