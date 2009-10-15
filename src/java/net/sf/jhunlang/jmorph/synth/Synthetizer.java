package net.sf.jhunlang.jmorph.synth;

import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;

import net.sf.jhunlang.jmorph.Affix;
import net.sf.jhunlang.jmorph.AffixEntry;
import net.sf.jhunlang.jmorph.AffixEntryExtension;
import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.Dictionaries;
import net.sf.jhunlang.jmorph.PrefixEntry;
import net.sf.jhunlang.jmorph.Rules;
import net.sf.jhunlang.jmorph.SuffixEntry;
import net.sf.jhunlang.jmorph.analysis.AnalysisEntry;
import net.sf.jhunlang.jmorph.sword.parser.SwordReader;

/**
 * "I could keep my anxiety and curiosity from eating the heart out of me
 * for forty-eight hours, I should then find out for certain whether this
 * boy was telling me the truth or not. 
 * Wherefore, being a practical Connecticut man, I now shoved this whole
 * problem clear out of my mind till its appointed day and hour should come,
 * in order that I might turn all my attention to the circumstances of the
 * present moment, and be alert and ready to make the most out of them that
 * could be made.
 * One thing at a time, is my motto -- and just play that thing for all it is
 * worth, even if it's only two pair and a jack."
 */
public class Synthetizer
{
  public final static String[] ZERO_MORPHS =
  {
    " NOM",
    " PRES_INDIC_INDEF_SG_3"
  };

  protected Rules rules;
  protected Rules subRules;
  protected Dictionaries dictionaries;

  protected Map suffixMorph = new HashMap();
  protected Map prefixMorph = new HashMap();

  // map for derivators allowing lower case on firts character
  protected Map lowers = new HashMap();

  /**
   * Create and initialize a Synthetizer for synthetizing with the
   * specified <code>rules</code> and <code>dictionaries</code>.
   * @param rules the affix rules
   * @param dictionaries the dictionary
   */
  public Synthetizer(Rules rules, Dictionaries dictionaries)
  {
    this.rules = rules;
    this.dictionaries = dictionaries;

    buildMorph(prefixMorph, rules.getPrefixes());
    buildMorph(suffixMorph , rules.getSuffixes());

    if ((subRules = rules.getSubRules()) != null)
    {
      buildMorph(prefixMorph, subRules.getPrefixes());
      buildMorph(suffixMorph, subRules.getSuffixes());
    }
    toArray(prefixMorph);
    toArray(suffixMorph);
  }

  public Map getPrefixMorph()
  {
    return prefixMorph;
  }

  public Map getSuffixMorph()
  {
    return suffixMorph;
  }

  /**
   * Build the morphological map of affix entries, belonging to the affix in
   * <code>affixes</code>, into <code>morphMap</code>. <code>morphMap</code>
   * collects entries with the same morphological marker and associates this
   * collection with the morpholigal marker. The associated collection is a
   * map again: collects entries with the same flag and associates this
   * collection with the flag.
   * @param morphMap the morphological map
   * @param affixes the collection of affix the affix flags or <code>null</code>
   */
  public void buildMorph(Map morphMap, Collection affixes)
  {
    Iterator it = affixes.iterator();
    while (it.hasNext())
    {
      Affix affix = (Affix)it.next();
      Character flag = new Character(affix.getName());

      AffixEntry[] entries = affix.getEntries();
      for(int i = 0; i < entries.length; i++)
      {
        if (entries[i] != null)
        {
          AffixEntryExtension extension = entries[i].getExtension();

          String morph;

          if (extension != null)
          {
            morph = extension.morphString();
            // if entries[i] is a derivator then
            // cut OUT zero morphemes and put entries[i] to lowers if
            // it has the lowercase flag "
            if (extension.isDerivator())
            {
              for(int j = 0; j < ZERO_MORPHS.length; j++)
              {
                String zeroMorph = ZERO_MORPHS[j];

                int index;
                while((index = morph.indexOf(zeroMorph)) != -1)
                {
                  morph = morph.substring(0, index) + 
                    morph.substring(index + zeroMorph.length());
                }
              }

              if (entries[i].hasFlag('"'))
              {
                lowers.put(morph, Boolean.TRUE);
              }
            }
          }
          else
          {
            morph = "";
          }

          Map byFlag = (Map)morphMap.get(morph);
          if (byFlag == null)
          {
            byFlag = new HashMap();
            morphMap.put(morph, byFlag);
          }

          Collection c = (Collection)byFlag.get(flag);
          if (c == null)
          {
            c = new HashSet();
            byFlag.put(flag, c);
          }
          c.add(entries[i]);
        }
      }
    }
  }

  /**
   * Return the collection of synthetizations of <code>word</code> by
   * the morphological markers enlisted in <code>m</code>.
   * @param word the word to synthetize
   * @param m the morpholigal markers
   * @return the collection of synthetizations
   */
  public Collection synthetize(String word, String m)
  {
    Morph morph = new Morph(m);

    Collection derivators = morph.getDerivators();
    boolean inflexed = morph.inflexed();
    
    boolean lower = derivators.size() == 0 ?
      false : lowers.get(derivators.iterator().next()) != null;

    Collection wordEntries = getEntries(word, null, lower);

    if (wordEntries.size() == 0)
    {
      return new ArrayList(1);
    }

    if (derivators.size() > 0)
    {
      // derive and inflex
      Collection entries = wordEntries;
      Iterator it = derivators.iterator();
      while (entries.size() > 0 && it.hasNext())
      {
        String derivator = (String)it.next();
        // derive entries into derivations; ask stems only if not inflexed
        // and  derivator is the last
        entries = derive(entries, derivator, inflexed || it.hasNext());
      }
      if (inflexed)
      {
        entries = inflex(entries, morph);
      }
      if (entries.size() > 0)
      {
        return entries;
      }

      // hack 1, 2 and 3 follows

      // 1.
      // as there are specific derivator series that can be synthetized
      // only together, try all possible derivator grouping
      entries = hack(wordEntries, morph, 0, derivators.size(), inflexed);     
      if (inflexed)
      {
        entries = inflex(entries, morph);
      }
      if (entries.size() > 0)
      {
        return entries;
      }
      // 2.
      // as there might be circumflexes everywhere, try them too
      entries = circumHack(wordEntries, morph, inflexed);
      if (inflexed)
      {
        entries = inflex(entries, morph);
      }
      if (entries.size() > 0)
      {
        return entries;
      }
      // 3.
      // as there are specific derivator series with inflexion,
      // try 1. and 2. with suffix added to the last derivator
      // note that suffixMorph is not changed 
      if (morph.addSuffix2Derivator())
      {
        entries = hack(wordEntries, morph, 0, derivators.size(), false);
        if (entries.size() > 0)
        {
          return entries;
        }
        entries = circumHack(wordEntries, morph, false);
        if (entries.size() > 0)
        {
          return entries;
        }
      }
      // if no entries AND not prefix inflexed then try derivation
      // by the whole suffix morph
      if (entries.size() == 0 && morph.getPrefix() == null)
      {
        return derive(wordEntries, morph.getSuffixMorph(), false);
      }
      return entries;
    }
    else // no derivation
    {
      if (!inflexed)
      {
        Collection synth = new ArrayList(1);
        Iterator it = wordEntries.iterator();
        while (it.hasNext())
        {
          synth.add(new AnalysisEntry((DictEntry)it.next()));
        }
        return synth;
      }
      else
      {
        return inflex(wordEntries, morph);
      }
    }    
  }

  // hack 1. try all possible groupings of derivators
  protected Collection hack(Collection entries, Morph morph,
    int from, int until, boolean dict)
  {
    // contract [from, j) and call hack for [j, until)
    for(int j = from; j <= until; j++)
    {
      if (j == from)      // all to contract
      {
        String a = morph.getDerivatorMorph(from, until);
        Collection derivations = derive(entries, a, dict);
        if (derivations.size() > 0)
        {
          return derivations;
        }
        continue;
      }
      else
      {
        String a = morph.getDerivatorMorph(from, j);
        boolean d = dict || j < until;

        Collection derivations = derive(entries, a, d);
        derivations = hack(derivations, morph, j, until, dict);
        if (derivations.size() > 0)
        {
          return derivations;
        }
      }
    }
    return new ArrayList(0);
  }

  /**
   * Return the collection of synthesis by <code>morph</code> trying all
   * possible circumflexes. Generate all possible groupings of derivators
   * into groups first, middle and last, handling last and first as circumflex,
   * i.e. derive first by middle, then by last and finally by first.
   * @param entries the dictionary entries
   * @param morph the morpholigal markers
   * @param dict if dictionary or stem entries should be synthetized
   * @return the collection of synthetized entries
   */
  protected Collection circumHack(Collection entries, Morph morph,
    boolean dict)
  {
    Collection derivations = new ArrayList(1);

    int d = morph.getDerivators().size();
    // as no circumfix without prefix, start from 1
    for(int i = 1; derivations.size() == 0 && i < d; i++)
    {
      // as no circumfix without suffix, stop before d
      for(int j = i; derivations.size() == 0 && j < d; j++)
      {
        // middle derivation: ask dict for both last first follow
        derivations = j > i ?
          hack(entries, morph, i, j, true) : entries;
        if (derivations.size() > 0)
        {
          // last derivation; ask dict for first will follow
          derivations = hack(derivations, morph, j, d, true);
        }
        if (derivations.size() > 0)
        {
          // prefix derivation
          derivations = hack(derivations, morph, 0, i, dict);
        }
      }
    }
    return derivations;
  }
  
  /**
   * Return collection of dictionary entries for word with part of speech 
   * <code>pos</code>. <code>lower</code> tells if lower case on first
   * character allowed.
   * @param word the word
   * @param pos the part of speech wanted
   * @param lower if lower case on first character allowed
   */
  protected Collection getEntries(String word, String pos, boolean lower)
  {
    Object o = dictionaries.rawGet(word.toLowerCase());
    Collection c = new ArrayList();

    if (o != null)
    {
      if (o instanceof DictEntry)
      {
        dictEntry(c, word, (DictEntry)o, lower);
      }
      else // o is map of entries
      {
        Iterator it = ((Map)o).values().iterator();
        while (it.hasNext())
        {
          dictEntry(c, word, (DictEntry)it.next(), lower);
        }
      }
    }
    return c;
  }

  // return collection of <code>entry</code> and its homonyms
  // if either <code>entry</code> admits the capitalization of
  // <code>word</code> or <code>lower</code> is true and
  // <code>entry</code> admits capitalization of <code>word</code>
  // regardless of the capitalization of its first character
  protected void dictEntry(Collection c,
    String word, DictEntry entry, boolean lower)
  {
    if (entry.admitCapitalization(word) ||
        (lower && entry.admitLowerCapitalization(word)))
    {
      c.add(entry);
      Iterator it = entry.getHomonyms().iterator();
      while (it.hasNext())
      {
        DictEntry homonym = (DictEntry)it.next();
        c.add(homonym);
      }
    }
  }
  
  /**
   * Return derivations of from <code>entries</code> by <code>morph</code>.
   * @param entries the dictionary entries to derive from
   * @param morph the morphological markers
   * @param dict if dictionary or stem entries should be returned
   * @return the collection of derived dictionary or stem entries depending on
   * whether or not <code>dict</code> is true
   */
  protected Collection derive(Collection entries, String morph, boolean dict)
  {
    Collection derivations = new ArrayList(1);
    
    Map entryMap = getEntryMap(morph);
    if (entryMap == null)
    {
      return derivations;
    }

    Iterator it = entries.iterator();
    while (it.hasNext())
    {
      DictEntry entry = (DictEntry)it.next();      
      derive(derivations, entry, entryMap, dict);
      Collection reverseRoots = dictionaries.getReverseRoots(entry);
      
      if (reverseRoots != null)
      {
        Iterator rit = reverseRoots.iterator();
        while (rit.hasNext())
        {
          DictEntry reverseRoot = (DictEntry)rit.next();
          if (reverseRoot.derived())
          {
            if (reverseRoot.getInflexion() != null &&
                reverseRoot.getInflexion().derivatorString().equals(morph))
            {
              derivations.add(dict ? (Object)reverseRoot :
                                     (Object)new AnalysisEntry(reverseRoot));
            }
            else
            {
              derive(derivations, reverseRoot, entryMap, dict);
            }
          }
        }
      }
    }
    return derivations;
  }
  
  protected void derive(
    Collection derivations, DictEntry entry, Map entryMap, boolean dict)
  {
    String word = entry.getWord();
    String flags = entry.getFlagString();
    // run by flags of entry
    for(int i = flags.length() - 1; i >= 0; i--)
    {
      AffixEntry[] aentries =
        (AffixEntry[])entryMap.get(new Character(flags.charAt(i)));
      if (aentries != null)
      {
        for(int j = aentries.length - 1; j >= 0; j--)
        {
          AffixEntry affixEntry = aentries[j];
          if (affixEntry.check4Inflexion(word))
          {
            String derivedWord = affixEntry.inflex(word);
            DictEntry derivation = affixEntry.derive(derivedWord, entry);
            if (derivation != null)
            {
              if (dict)
              {
                derivations.add(derivation);
              }
              else
              {
                derivations.add(new AnalysisEntry(derivation));
              }
            }
          }
        }  // end of for on aentries
      }    // end of check if aentries not null
    }      // end of for on flags
  }

  /**
   * Return inflexions of <code>entries</code> by <code>prefix</code> and
   * <code>suffix</code>.
   * @param entries the dictionary entries to inflex
   * @param morph the morphological annotation to synthetize
   * @return the collection of inflected entries
   */
  protected Collection inflex(Collection entries, Morph morph)
  {
    Collection inflexions = new ArrayList(1);
    
    String prefix = morph.getPrefix();
    String suffix = morph.getSuffix();

    Map aMap = prefix == null ? null : getEntryMap(prefix);
    Map bMap = suffix == null ? null : getEntryMap(suffix);

    if (aMap == null)
    {
      aMap = bMap;
      bMap = null;
    }
    
    if (aMap == null)
    {
      return inflexions;
    }

    Iterator it = entries.iterator();
    while (it.hasNext())
    {
      DictEntry entry = (DictEntry)it.next();      
      inflex(entry, aMap, bMap, inflexions);

      Collection reverseRoots = dictionaries.getReverseRoots(entry);
      if (reverseRoots != null)
      {
        Iterator rit = reverseRoots.iterator();
        while (rit.hasNext())
        {
          DictEntry reverseRoot = (DictEntry)rit.next();
          if (!reverseRoot.derived())
          {
            if (reverseRoot.getInflexion() != null &&
                reverseRoot.getInflexion().morphString().equals(suffix))
            {
              inflexions.add(new AnalysisEntry(reverseRoot));
            }
            else
            {
              inflex(reverseRoot, aMap, bMap, inflexions);
            }
          }
        }
      }
    }        // end of iterator on entries
    return inflexions;
  }

  /**
   * Inflex <code>entry</code> by affix rules in <code>aMap</code> and
   * <code>bMap</code>.
   * @param entry the entry to inflex
   * @param aMap the map of prefix or suffix inflexions
   * @param bMap the map of suffix inflexions if aMap is prefix
   * @return if inflexion found
   */
  protected boolean inflex(
    DictEntry entry, Map aMap, Map bMap, Collection inflexions)
  {
    boolean inflexed = false;
    String word = entry.getWord();
    String flags = entry.getAccumulatedFlags().getFlagString();
       
    // run by flags of entry
    for(int i = flags.length() - 1; i >= 0; i--)
    {
      Character ac = new Character(flags.charAt(i));
      AffixEntry[] afx = (AffixEntry[])aMap.get(ac);
      if (afx != null)
      {
        for(int j = afx.length - 1; j >= 0; j--)
        {
          AffixEntry aEntry = afx[j];
          if (aEntry.check4Inflexion(word))
          {
            if (bMap == null)
            {
              inflexed = true;
              inflexions.add(new AnalysisEntry(entry, aEntry));
            }
            else if ((aEntry instanceof PrefixEntry) && aEntry.crossable())
            {
              for(int k = flags.length() - 1; k >= 0; k--)
              {
                Character bc = new Character(flags.charAt(k));
                AffixEntry[] bfx = (AffixEntry[])bMap.get(bc);
                if (bfx != null)
                {
                  for(int l = bfx.length - 1; l >= 0; l--)
                  {
                    AffixEntry bEntry = bfx[l];
                    if ((bEntry instanceof SuffixEntry) &&
                        bEntry.crossable() && bEntry.check4Inflexion(word))
                    {
                      inflexed = true;
                      inflexions.add(new AnalysisEntry(
                        entry, (PrefixEntry)aEntry, (SuffixEntry)bEntry));
                    }
                  }
                }
              }
            }
          }
        }  // end of for on aentries
      }    // end of check if aentries not null
    }      // end of for on flags
    return inflexed;
  }

  /**
   * Return if <code>morph</code> is a derivator mark. Generator marks
   * are those containing exactly underscores ('_') so that the middle
   * mark is either derivator by {@link SwordReader} or it is PREF (preverb).
   * @param morph the morphological mark
   * @return if <code>morph</code> represents a derivator
   */
  public boolean isDerivator(String morph)
  {
    if (morph.equals("lowercase"))
    {
      return true;
    }
    int firstIndex = morph.indexOf('_');
    if (firstIndex != -1)
    {
      int lastIndex = morph.indexOf('_', firstIndex + 1);

      if (lastIndex == -1)
      {
        return false;
      }
      String morph1 = morph.substring(firstIndex + 1, lastIndex);
      return SwordReader.derivative(morph1);
    }
    return false;
  }

  /**
   * Convert affix entry collections stored in <code>morphMap</code> to
   * affix entry arrays.
   * @param morphMap the morphological map
   */
  protected void toArray(Map morphMap)
  {
    Iterator it = morphMap.values().iterator();
    while (it.hasNext())
    {
      Map byFlag = (Map)it.next();
      Iterator bit = byFlag.entrySet().iterator();
      while (bit.hasNext())
      {
        Map.Entry byFlagEntry = (Map.Entry)bit.next();
        Collection entries = (Collection)byFlagEntry.getValue();
        byFlagEntry.setValue(entries.toArray(new AffixEntry[entries.size()]));
      }
    }
  }

  /**
   * Return the map associated with <code>morph</code>
   * or <code>null</code>. The returned map associates flag character
   * with affix entries arrays the associated entry containing the affix entries
   * having flag and <code>morph</code>.
   * @param morph the morpholigal markers
   * @return the map of affix entry arrays or <code>null</code>
   */
  public Map getEntryMap(String morph)
  {
    Map map = (Map)suffixMorph.get(morph);
    if (map == null)
    {
      map = (Map)prefixMorph.get(morph);
    }
    return map;
  }

  private class Morph
  {
    // the part of speech of the input
    protected String pos;
    // prefix inflexion
    protected String prefix;
    // suffix inflexion
    protected String suffix;
    // tells if either prefix or suffix is not null
    protected boolean inflexed;
    // derivations
    protected List derivators = new ArrayList(1);
    // the suffix morph
    protected String suffixMorph;
    // the original morph
    protected String morph;

    /**
     * Split up <code>morph</code> to prefix inflexion, derivatives and
     * suffix inflexion where derivative is
     *  - either the derivative mark plus NOM (bb_COMPARATIVE_adj)
     *  - or the derivative mark alone
     * and the remainder is the inflexion.
     * Inflexion before derivation is appended to the previous
     * derivation ('artificial derivation' with inflexion) if any. Otherwise
     * this inflexion is the prefix inflexion.
     * @param morph the morphological markers
     */
    public Morph(String morph)
    {
      this.morph = morph = morph.trim();

      StringTokenizer st = new StringTokenizer(morph);

      while (st.hasMoreTokens())
      {
        String derivator = st.nextToken();

        if (isDerivator(derivator))
        {
          // inflexion between derivators - add inflexion to prev. derivator
          // or set it as prefix inflexion
          if (suffix != null)
          {
            if (derivators.size() > 0)
            {
              String before = (String)derivators.remove(derivators.size() - 1);
              before += " " + suffix;
              derivators.add(before);
            }
            else
            {
              inflexed = true;
              prefix = suffix;
            }
          }
          //derivator anyway and clear suffix inflexion
          derivators.add(derivator);
          inflexed = prefix != null;
          suffix = null;
        }
        else // not derivator
        {
          inflexed = true;
          if (suffix == null)
          {
            suffix = derivator;
          }
          else
          {
            suffix += " " + derivator;
          }
        }
      }
      suffixMorph = prefix == null ?
        morph : morph.substring(prefix.length()).trim();
    }

    public boolean addSuffix2Derivator()
    {
      if (suffix == null)
      {
        return false;
      }

      ListIterator it = derivators.listIterator(derivators.size());
      if (it.hasPrevious())
      {
        String lastDerivator = (String)it.previous();
        it.remove();
        lastDerivator += " " + suffix;
        derivators.add(lastDerivator);
        return true;
      }
      else
      {
        return false;
      }
    }

    public String getPrefix()
    {
      return prefix;
    }

    public String getSuffix()
    {
      return suffix;
    }

    public Collection getDerivators()
    {
      return derivators;
    }

    public String getMorph()
    {
      return morph;
    }

    public String getSuffixMorph()
    {
      return suffixMorph;
    }

    public boolean inflexed()
    {
      return inflexed;
    }

    public String getDerivatorMorph(int from, int until)
    {
      StringBuffer sb = new StringBuffer();
      Iterator it = derivators.listIterator(from);
      while (from++ < until && it.hasNext())
      {
        sb.append(it.next().toString());
        if (from < until && it.hasNext())
        {
          sb.append(" ");
        }
      }
      return sb.toString();
    }

    public String toString()
    {
      StringBuffer sb = new StringBuffer(inflexed ? "inflexed " : "");
      if (prefix != null)
      {
        sb.append("PFX[" + prefix + "]");
      }
      Iterator it = derivators.iterator();
      while (it.hasNext())
      {
        sb.append("{" + it.next() + "}");
      }
      if (suffix != null)
      {
        sb.append("SFX[" + suffix + "]");
      }
      return sb.toString();
    }
  }
}