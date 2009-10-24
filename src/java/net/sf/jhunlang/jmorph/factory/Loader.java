package net.sf.jhunlang.jmorph.factory;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import net.sf.jhunlang.jmorph.Dictionaries;
import net.sf.jhunlang.jmorph.Rules;
import net.sf.jhunlang.jmorph.parser.ParseException;

public class Loader
{
  public final static String DEF_URL = "hu.def"; //"resource/hu.def";
    
  protected Definition[] adef;
  protected Rules rules;
  protected Dictionaries dic;
  
  public Loader()
  {}
  
  public Loader(Rules rules, Dictionaries dic)
  {
    this.rules = rules;
    this.dic = dic;
  }

  public JMorphFactory read(String[] args)
    throws IOException, ParseException 
  {
    JMorphFactory fac = new JMorphFactory();
  
    if (args.length == 0)
    {
      //This is the same as the system classloader, 
      //which handles -classpath and should not be used
      //otherwise, the code will only work in simple command-line applications	
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      URL defURL = cl.getResource(DEF_URL);
      if (defURL == null)
      {
        throw new IOException("File not found: " + DEF_URL);
      }
      adef = fac.readDef(defURL);
    }
    else
    {
      adef = fac.readDef(new File(args[0]).toURL());
    }
    return fac;
  }
  
  public JMorphFactory read(URL url)
    throws IOException, ParseException 
  {
    JMorphFactory fac = new JMorphFactory();
    adef = fac.readDef(url);
    return fac;
  }

  public void loadRules(String[] args)
    throws IOException, ParseException 
  {
    JMorphFactory fac = read(args);
    rules = fac.buildRules(adef[0]);
  }

  public void loadDictionaries(String[] args)
    throws IOException, ParseException 
  {
    JMorphFactory fac = read(args);
    dic = fac.buildDictionaries(adef);
  }

  public void load(String[] args)
    throws IOException, ParseException 
  {
    JMorphFactory fac = read(args);
    rules = fac.buildRules(adef[0]);
    dic = fac.buildDictionaries(adef);
  }

  public void load(URL url)
    throws IOException, ParseException 
  {
    JMorphFactory fac = read(url);
    rules = fac.buildRules(adef[0]);
    dic = fac.buildDictionaries(adef);
  }

  /**
   * @return return the {@link #DEF_URL}.
   */
  public static String getDefaultUrl()
  {
    return DEF_URL;
  }
  
  /**
   * @return return the dic.
   */
  public Dictionaries getDic()
  {
    return dic;
  }
  
  /**
   * @return return the rules.
   */
  public Rules getRules()
  {
    return rules;
  }
}
