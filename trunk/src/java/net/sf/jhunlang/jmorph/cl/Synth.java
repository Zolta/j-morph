package net.sf.jhunlang.jmorph.cl;

import java.util.Iterator;
import java.util.Collection;

import java.io.IOException;
import java.io.PrintWriter;

import net.sf.jhunlang.jmorph.analysis.AnalysisEntry;
import net.sf.jhunlang.jmorph.parser.ParseException;
import net.sf.jhunlang.jmorph.synth.Synthetizer;

public class Synth extends Cl
{
  protected Synthetizer synthetizer;
  
  public void configureAndRun(String[] args)
    throws IOException, ParseException 
  {
    load(args);
    synthetizer = new Synthetizer(rules, dic);
    run();
  }

  public void process(String morph, PrintWriter p)
  {
    int index;
    for(index = 0; index < morph.length(); index++)
    {
      if (Character.isWhitespace(morph.charAt(index)))
      {
        break;
      }
    }

    String word = morph.substring(0, index);
    morph = morph.substring(index).trim();
  
    Collection c = synthetizer.synthetize(word, morph);
    p.println("word: " + c.size());
  
    Iterator it = c.iterator();
    while (it.hasNext())
    {
      Object o = it.next();
      AnalysisEntry stem = (AnalysisEntry)o;
      p.println("\t" + stem.getInflexedWord() + "\t" + stem);
    }
  }
  
  public static void main(String[] args)
    throws Exception
  {
    new Synth().configureAndRun(args);
  }
}
