package net.sf.jhunlang.jmorph.cl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import net.sf.jhunlang.jmorph.analysis.Analyser;
import net.sf.jhunlang.jmorph.analysis.AnalyserContext;
import net.sf.jhunlang.jmorph.analysis.AnalyserControl;
import net.sf.jhunlang.jmorph.analysis.AnalyserImpl;
import net.sf.jhunlang.jmorph.analysis.AnalysisEntry;
import net.sf.jhunlang.jmorph.parser.ParseException;

public class Morph extends Cl
{
  public final static String DEPTH = "depth";
  public final static String TYPE = "type";
  
  public final String NONE = "\tNOT_A_WORD";
  
  public final int RELATIVE = 0;
  public final int DICTIONARY = 1;
  public final int ABSOLUTE = 2;
  public final int ALL = 3;

  protected Analyser analyser;
  protected AnalyserContext context;
  
  protected int analysisType; 
  
  protected int count;
  protected int notword;
  
  protected long start;
  
  public void process(String word, PrintWriter p)
  {
    if (count == 0)
    {
      start = System.currentTimeMillis();
    }
    count++;
    List analyses = analyser.analyse(word, context);    
    if (analyses.size() == 0)
    {
      notword++;
    }    
    if (count % 10000 == 0)
    {
      long end = System.currentTimeMillis();
      p.println(count + "/" + notword + " in " + (end - start));
    }
  }
  
  public void process1(String word, PrintWriter p)
  {
    List analyses = analyser.analyse(word, context);    
    if (analyses.size() == 0)
    {
      p.println(word + NONE);
      return;
    }
    p.print(word + "\t" + analyses.size());
    Iterator it = analyses.iterator();
    while (it.hasNext())
    {
      AnalysisEntry entry = (AnalysisEntry)it.next();
      
      switch (analysisType)
      {
        case RELATIVE:
          p.println(
              "\t" + entry.getRelativeRootWord() +
              "\t" + entry.relativeMorphString());
          break;
        case DICTIONARY:
          p.println(
              "\t" + entry.getDictionaryRootWord() +
              "\t" + entry.dictionaryMorphString());
          break;
        case ABSOLUTE:
          p.println(
              "\t" + entry.getAbsoluteRootWord() +
              "\t" + entry.absoluteMorphString());
          break;
        case ALL:
          p.println(
              "\t" + entry.getRelativeRootWord() +
              "\t" + entry.relativeMorphString());
          p.println(
              "\t" + entry.getDictionaryRootWord() +
              "\t" + entry.dictionaryMorphString());
          p.println(
              "\t" + entry.getAbsoluteRootWord() +
              "\t" + entry.absoluteMorphString());
          break;
      }
    }
  }
  
  public void configureAndRun(String[] args)
    throws IOException, ParseException 
  {    
    analysisType = Math.max(0, Math.min(ALL,
      Integer.parseInt(System.getProperty(TYPE, "0"))));
    
    load(args);
    String s = System.getProperty(DEPTH);
    int depth =
      s == null ? AnalyserControl.DEFAULT_DEPTH : Integer.parseInt(s);
    AnalyserControl control = new AnalyserControl(depth);
    context = new AnalyserContext(control);
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
    new Morph().configureAndRun(args);
  }
}
