package net.sf.jhunlang.jmorph.cl;

import java.util.Iterator;
import java.util.Map;

import java.io.IOException;
import java.io.PrintWriter;

import net.sf.jhunlang.jmorph.analysis.AnalysisEntry;

import net.sf.jhunlang.jmorph.parser.ParseException;

import net.sf.jhunlang.jmorph.synth.Generator;

public class Gen extends Cl
{
  public final String NONE = "\tNOT_A_WORD";
  
  protected Generator generator;
  
  public void process(String word, PrintWriter p)
  {
    Map generations = generator.generate(word);
    
    if (generations == null)
    {
      p.println(word + NONE);
      return;
    }
    
    p.println(word + "\t" + generations.size());

    Iterator it = generations.entrySet().iterator();
    while (it.hasNext())
    {
      Map.Entry m = (Map.Entry)it.next();
      Map c = (Map)m.getValue();
      
      p.print("\t" + m.getKey() + "\t" + c.size());
      
      Iterator cit = c.values().iterator();
      while (cit.hasNext())
      {
        AnalysisEntry entry = (AnalysisEntry)cit.next();
        p.print("\t" + entry.morphString());
      }
      p.println();
    }
  }

  public void configureAndRun(String[] args, int type)
    throws IOException, ParseException 
  {
    load(args);
    generator = new Generator(rules, dic, type);    
    run();
  }

  /**
   * Start reading words separated by newlines from the standard input and
   * write their generations to the standard output.<p>
   * @param args the command line parameters specifying the definition file.
   */
  public static void main(String[] args)
    throws Exception
  {
    new Gen().configureAndRun(args, Generator.ALL);
  }
}
