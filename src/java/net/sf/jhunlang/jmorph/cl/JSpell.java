package net.sf.jhunlang.jmorph.cl;

import java.io.IOException;
import java.io.PrintWriter;

import net.sf.jhunlang.jmorph.analysis.Analyser;
import net.sf.jhunlang.jmorph.analysis.AnalyserImpl;
import net.sf.jhunlang.jmorph.analysis.IStem;

import net.sf.jhunlang.jmorph.parser.ParseException;

/**
 * JSpell is an application producing ispell-like output for text files.
 */
public class JSpell extends Cl
{
  protected Analyser analyser;
  
  public void process(String word, PrintWriter p)
  {  
    IStem istem = analyser.istem(word, 0);
    p.println(istem + "");
  }
  
  public void configureAndRun(String[] args)
    throws IOException, ParseException 
  {
    load(args);  
    analyser = new AnalyserImpl(rules, dic);
    run();
  }

  /**
   * Start reading words separated by newlines from the standard input and
   * write their analyses to the standard output.<p>
   * @param args the command line parameters specifying the definition file
   */
  public static void main(String[] args)
    throws Exception
  {
    new JSpell().configureAndRun(args);
  }
}