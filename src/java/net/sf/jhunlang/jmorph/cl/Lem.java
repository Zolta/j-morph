package net.sf.jhunlang.jmorph.cl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import net.sf.jhunlang.jmorph.analysis.AnalyserImpl;
import net.sf.jhunlang.jmorph.lemma.Lemma;
import net.sf.jhunlang.jmorph.lemma.Lemmatizer;
import net.sf.jhunlang.jmorph.lemma.LemmatizerImpl;
import net.sf.jhunlang.jmorph.parser.ParseException;

/**
 * Simple command line wrapper around the Lemmatizer class. Reads
 * simple words from the stdin and prints it's stem and POS.
 * 
 *  */
public class Lem extends Cl
{  
    
    protected Lemmatizer lemmatizer;
    
    protected boolean stripDerivates;
   
    public void configureAndRun(String[] args)
    throws IOException, ParseException 
    {
        if(args.length < 1) {
            System.err.println("usage: Lem definition [-stripderivates]");
            System.exit(-1);
            
        }
        for(int i = 0; i < args.length; i ++) {
            if(args[i].equalsIgnoreCase("-stripderivates")) {
                stripDerivates = true;
            }
        }
        load(args);
        lemmatizer = new LemmatizerImpl(new AnalyserImpl(rules, dic));
        run();
  }
  
  /**
   * Start reading words separated by newlines from the standard input and
   * write their lemmas to the standard output.<p>
   * @param args the command line parameters specifying the definition file.
   */
  public static void main(String[] args)
    throws Exception
  {
    new Lem().configureAndRun(args);
  }


  protected void process(String word, PrintWriter p) {
    Iterator  lemmas = lemmatizer.lemmatize(word, stripDerivates).iterator ();
    while(lemmas.hasNext()) {
        Lemma lemma = (Lemma) lemmas.next();
        p.println(lemma.getWord() + "/" + lemma.getPOS());
    }
  }
}
