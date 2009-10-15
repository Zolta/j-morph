/*
 * Created on Jun 6, 2005
 */
package net.sf.jhunlang.jmorph.lemma;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.analysis.Analyser;
import net.sf.jhunlang.jmorph.analysis.AnalyserContext;
import net.sf.jhunlang.jmorph.analysis.AnalyserControl;
import net.sf.jhunlang.jmorph.analysis.AnalysisEntry;
import net.sf.jhunlang.jmorph.util.Comparables;

/**
 * @author hp
 */

/*
 * Avalon wrapper for JMorph.
 */
public class LemmatizerImpl implements Lemmatizer
{    
  /**
   * The jmorph analyser that the work is delegated to.
   */
  private Analyser  analyser;
  
  private AnalyserContext analyserContext;
	
  public LemmatizerImpl(Analyser analyser)
  {
    this.analyser = analyser;
    AnalyserControl ac = new AnalyserControl(AnalyserControl.ALL_COMPOUNDS);
    this.analyserContext = new AnalyserContext(ac);
  }

  public List lemmatize(final String word, boolean stripDerivates)
  {
    List anals = analyser.analyse(word, analyserContext);
		
	// return empyt list if no analysis found
	if(anals.size() == 0)
    {
      return anals;
	}		
		
	Iterator it = anals.iterator();
	HashMap lemmas = new HashMap();
		
	// drop duplicates i.e. those giving the same stem and POS		 
    while(it.hasNext())
    {
	  AnalysisEntry anal = (AnalysisEntry)it.next();
		   
      DictEntry e = anal.getDictEntry();
      DictEntry root = e.getRootEntry();
		    
      if (root == null)
      {
		lemmas.put(new Comparables(e.getWord(), e.getPOS(), false), e);
      }
      else if (root.inflexed())
      {
		// 'kezek' (plural of kéz) might be root
        lemmas.put(new Comparables(e.getWord(), e.getPOS(), false), e);
      }
      else if (e.derived() && !stripDerivates)
      {
		lemmas.put(new Comparables(e.getWord(), e.getPOS(), false), e);
      }
      else
      {
		lemmas.put(new Comparables(root.getWord(), root.getPOS(), false), root);
      }
    } 
		
	// make list of stems
	LinkedList lemmaList = new LinkedList();
	it = lemmas.values().iterator();
	while(it.hasNext()) 
	{
	  DictEntry dictEntry = (DictEntry) it.next();
	  lemmaList.add(new Lemma(dictEntry.getWord(), dictEntry.getPOS()));
    }
	return lemmaList;
  }
}
