package net.sf.jhunlang.jmorph.factory;

import java.util.LinkedList;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.net.URL;

import net.sf.jhunlang.jmorph.Dictionaries;
import net.sf.jhunlang.jmorph.Hypothetiser;
import net.sf.jhunlang.jmorph.Rules;
import net.sf.jhunlang.jmorph.WordList;
import net.sf.jhunlang.jmorph.analysis.Analyser;
import net.sf.jhunlang.jmorph.analysis.AnalyserImpl;
import net.sf.jhunlang.jmorph.lemma.Lemmatizer;
import net.sf.jhunlang.jmorph.lemma.LemmatizerImpl;
import net.sf.jhunlang.jmorph.parser.AbstractReader;
import net.sf.jhunlang.jmorph.parser.ListReader;
import net.sf.jhunlang.jmorph.parser.ParseException;

/**
 * Read affix and dictionaries and build Analyser.
 * The affix file and the dictionary files, their encodings and readers
 * are defined in the def file. The def file contains three lines for the
 * affix and three lines for each dictionary specifying:
 * <ul>
 * <li> the name of affix/dictionary file</li>
 * <li> the encoding of the affix/dictionary file</li>
 * <li> the dotted classname of the affix/dictionary reader</li>
 * </lu>
 * Lines starting with '#' are treated as comment lines.<p>
 */
public class JMorphFactory
{
  public final static String COMMENT = "#";
  public final static String EXT = "EXT";

  /**
   * Create JMorphFactory.
   */
  public JMorphFactory()
  {}
 
  /**
   * Build and return an Analyser for the affix rules and dictionaries
   * specified in the definition file <code>def</code>.
   * @param def the name of the definition file
   * @return the created Analyser
   * @throws IOException if reading the definition file or an affix file or
   * a dictionary file fails
   * @throws ParseException if the affix reader or a dictionary reader
   * throws it or if cannot instantiate the affix reader or a dictionary reader
   */
  public Analyser build(String def)
    throws IOException, ParseException
  {
    return build(new File(def).toURL());
  }

  /**
   * Build and return an Analyser for the affix rules and dictionaries
   * specified in the definition read from <code>defURL</code>.
   * @param defURL the URL of the definition
   * @return the created Analyser
   * @throws IOException if reading the definition or an affix file or a
   * dictionary fails
   * @throws ParseException if the affix reader or a dictionary reader
   * throws it or if cannot instantiate the affix reader or a dictionary reader
   */
  public Analyser build(URL defURL)
    throws IOException, ParseException
  {
    return build(readDef(defURL));
  }

  /**
   * Build and return a Rules instance for the affix rules specified in the
   * definition file <code>def</code>.
   * @param def the name of the definition file
   * @return the created Rules
   * @throws IOException if reading the definition file or the affix file fails
   * @throws ParseException if the affix reader throws it or if cannot
   * instantiate the affix reader
   */
  public Rules buildRules(String def)
    throws IOException, ParseException
  {
    return buildRules(new File(def).toURL());
  }

  /**
   * Build and return a Rules instance for the affix rules specified in the
   * definition <code>def</code>.
   * @param def the affix rule definition
   * @return the created Rules
   * @throws IOException if reading the affix file fails
   * @throws ParseException if the affix reader throws it
   */
  public Rules buildRules(Definition def)
    throws IOException, ParseException
  {
    Rules rules = (Rules)def.read(); 
    return rules;
  }

  /**
   * Build and return a Rules instance for the affix rules specified in the
   * definition read from <code>defURL</code>.
   * @param defURL the URL of the definition
   * @return the created Rules
   * @throws IOException if reading the definition or the affix file fails
   * @throws ParseException if the affix reader throws it or if cannot
   * instantiate the affix reader or a dictionary reader
   */
  public Rules buildRules(URL defURL)
    throws IOException, ParseException
  {
    return buildRules(readDef(defURL)[0]);
  }

  /**
   * Read definitions from <code>defURL</code> and return the array of them.
   * @param defURL the definition URL
   * @return the array of definitions
   * @throws IOException if reading fails
   * @throws ParseException if a definition is illegal or if cannot instantiate
   * the affix reader or a dictionary reader
   */
  public Definition[] readDef(URL defURL)
    throws IOException, ParseException
  {
    InputStream is = defURL.openStream();
    BufferedReader reader =
      new BufferedReader(new InputStreamReader(is, "ISO-8859-2"));
    try
    {
      return readDef(reader, defURL);
    }
    finally
    {
      try
      {
        reader.close();
      }
      catch (Throwable t)
      {}
    }
  }

  /**
   * Read definitions from reader and return the array of them.
   * @param reader the reader
   * @param baseURL the base URL for affix and dictionary files
   * @return the array of definitions
   * @throws IOException if reading fails
   * @throws ParseException if a definition is illegal or if cannot
   * instantiate the affix reader or a dictionary reader
   */
  public Definition[] readDef(BufferedReader reader, URL baseURL)
    throws IOException, ParseException
  {
    LinkedList l = new LinkedList();
    String line;
    String[] definition = new String[3];

    Definition currentDef = null;

    int i = 0;
    while ((line = reader.readLine()) != null)
    {
      line = line.trim();
      if (line.length() == 0 || line.startsWith(COMMENT))
      {
        continue;
      }

      if (line.startsWith(EXT))
      {
        if (currentDef == null)
        {
          throw new ParseException("Unexpected extension: " + line);
        }
        currentDef.addExtensionLine(line.substring(EXT.length()));
        continue;
      }

      definition[i++] = line;

      if (i < 3)
      {
        continue;
      }

      i = 0;

      String file = definition[0];
      String encoding = definition[1];
      String className = definition[2];

      URL fileURL = new URL(baseURL, file);

      try
      {
        Class clz = Class.forName(className);

        currentDef = new Definition(baseURL,
          fileURL, encoding, (AbstractReader)clz.newInstance());
        l.add(currentDef);
      }
      catch (ClassNotFoundException cnfx)
      {
        throw new ParseException("Cannot find " + className, cnfx);
      }
      catch (InstantiationException ix)
      {
        throw new ParseException("Cannot instantiate " + className, ix);
      }
      catch (IllegalAccessException iax)
      {
        throw new ParseException("Cannot instantiate " + className, iax);
      }
    }

    if (i != 0)
    {
      String s = "Read maimed definition:";
      while (--i >= 0)
      {
        s += "\n  " + definition[i];
      }
      throw new ParseException(s);
    }
    Definition[] adef = new Definition[l.size()];
    return (Definition[])l.toArray(adef);
  }

  /**
   * Create and return an Analyser instance for <code>adef</adef>.
   * The first element of <code>adef</adef> specifies the affix rules,
   * the others define the dictionaries.
   * @param adef the definitions of the affix rules and the dictionaries
   * @return the created Analyser
   * @throws IOException if reading either the affix file or a dictionary file
   * fails
   * @throws ParseException if either the specified affix reader or a
   * dictionary reader throws it
   */
  public Analyser build(Definition[] adef)
    throws IOException, ParseException
  {
    Rules rules = buildRules(adef[0]); 
    Dictionaries dicts = buildDictionaries(adef);
    return createAnalyser(rules, dicts);
  }
  
  /**
   * Create and return a Lemmatizer for <code>adef</adef>.
   * The first element of <code>adef</adef> specifies the affix rules,
   * others define the dictionaries.
   * @param adef the definitions of the affix rules and the dictionaries
   * @return the created stemmer
   * @throws IOException if reading either the affix file or a dictionary file
   * fails
   * @throws ParseException if either the specified affix reader or a dictionary
   * reader throws it
   */
  public Lemmatizer buildLemmatizer(Definition[] adef)
    throws IOException, ParseException
  {
    return new LemmatizerImpl(build(adef));
  }

  /**
   * Create and return a Dictionaries for <code>adef</adef>. As it specifies
   * the affix rules, ignore the first element of <code>adef</adef>.
   * @param adef the definitions of the affix rules and the dictionaries
   * @return the created Dictionaries instance
   * @throws IOException if reading a dictionary file fails
   * @throws ParseException if a dictionary reader throws it
   */
  public Dictionaries buildDictionaries(Definition[] adef)
    throws IOException, ParseException
  {
    Dictionaries dicts = new Dictionaries();
    for(int i = 1; i < adef.length; i ++)
    {
      if (adef[i].getReader() instanceof ListReader)
      {
        ((ListReader)adef[i].getReader()).setDictionaries(dicts);
      }
      WordList wl = (WordList)adef[i].read();
      dicts.add(wl);
    }
    return dicts.size() > 0 ? dicts : new Hypothetiser();
  }

  /**
   * Create and return an Analyser for <code>rules</adef> and
   * <code>dicts</code>.
   * @param rules the affix rules
   * @param dicts the dictionaries
   * @return the created analyser
   */
  public Analyser createAnalyser(Rules rules, Dictionaries dicts)
  {
    return new AnalyserImpl(rules, dicts);
  }
}