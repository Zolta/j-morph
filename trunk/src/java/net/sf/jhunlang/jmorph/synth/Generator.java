package net.sf.jhunlang.jmorph.synth;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;

import net.sf.jhunlang.jmorph.AffixEntry;
import net.sf.jhunlang.jmorph.AffixEntryExtension;
import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.Dictionaries;
import net.sf.jhunlang.jmorph.Rules;
import net.sf.jhunlang.jmorph.Suffix;
import net.sf.jhunlang.jmorph.SuffixEntry;
import net.sf.jhunlang.jmorph.analysis.AnalysisEntry;

public class Generator
{  
  public final static int ALL = 0;
  public final static int DERIVATION = 1;
  public final static int INFLEXION = 2;
  
  protected Rules rules;
  /**
   * The dictionaires
   */
  protected Dictionaries dictionaries;
  /**
   * The generating suffix rules
   */
  protected Map suffixGenerators;
  
  protected int type = ALL;

  public Generator(Rules rules, Dictionaries dictionaries, int type)
  {
    this.rules = rules;
    this.dictionaries = dictionaries;
    this.type = type;
    
    suffixGenerators = new TreeMap();
    
    add(rules.getSuffixes());
    
    if (rules.getSubRules() != null)
    {
      add(rules.getSubRules().getSuffixes());
    }
  }
  
  protected void add(Collection affixes)
  {
    Iterator it = affixes.iterator();
    while (it.hasNext())
    {
      Suffix suffix = (Suffix)it.next();
      AffixEntry[] entries = suffix.getEntries();
      for(int i = 0; i < entries.length; i++)
      {
        if (entries[i] != null)
        {
          if (addAffix(entries[i]))
          {
            Character c = new Character(entries[i].getParent().getName());
            Collection coll = (Collection)suffixGenerators.get(c);
            if (coll == null)
            {
              coll = new LinkedList();
              suffixGenerators.put(c, coll);              
            }
            coll.add(entries[i]);
          }            
        }
      }
    }
  }

  protected boolean addAffix(AffixEntry entry)
  {
    AffixEntryExtension extension = entry.getExtension();
    switch (type)
    {
      case ALL:
        return true;
      case DERIVATION:
        return extension != null &&
          extension.isDerivator() && !extension.isInflexion();
      case INFLEXION:
        return extension != null && !extension.isDerivator();
    }
    throw new IllegalStateException("Illegal type " + type);
  }
  
  /**
   * Create and return the map of (#link AnalysisEntry}'s representing
   * the generations of <code>word</code>. The map groups the returned
   * analysises by their generated words.
   * @param word the word to generate from
   * @return the map of generations
   */
  public Map generate(String word)
  {
    DictEntry entry = dictionaries.get(word);
    if (entry == null)
    {
      return null;
    }
    Map generations = new TreeMap();   
    return generateHomonyms(entry, generations);    
  }
  
  public Map generateHomonyms(DictEntry entry, Map generations)
  {
    generateAll(entry, generations);
    Iterator it = entry.getHomonyms().iterator();
    while (it.hasNext())
    {
      DictEntry homonym = (DictEntry)it.next();
      generateAll(homonym, generations);
    }
    return generations;
  }
   
  public Map generateAll(DictEntry entry, Map generations)
  {
    generate(entry, generations);
    Collection reverseRoots = dictionaries.getReverseRoots(entry);    
    if (reverseRoots != null)
    {
      Iterator rit = reverseRoots.iterator();
      while (rit.hasNext())
      {
        DictEntry reverseRoot = (DictEntry)rit.next();
        // g tells if generate on this reverse root
        boolean g = true;
        boolean d = reverseRoot.derived(); 
        switch (type)
        {
          case ALL:
            put(generations, new AnalysisEntry(reverseRoot));
            break;
          case INFLEXION:
            g = !d;
            if (!d && reverseRoot.getInflexion() != null)
            {
              put(generations, new AnalysisEntry(reverseRoot));
            }
            break;
          case DERIVATION:
            if (d && reverseRoot.getInflexion() != null)
            {
              put(generations, new AnalysisEntry(reverseRoot));
            }
            break;
        }
        if (g)
        {
          generate(reverseRoot, generations);
        }
      }
    }
    return generations;
  }
   
  protected void generate(DictEntry entry, Map generations)
  {    
    String word = entry.getWord();
    String flags = entry.getAccumulatedFlags().getFlagString();
    // run by flags of entry
    for(int i = flags.length() - 1; i >= 0; i--)
    {
      Character ac = new Character(flags.charAt(i));
      Collection coll = (Collection)suffixGenerators.get(ac);
      
      if (coll == null)
      {
        continue;
      }
      
      Iterator it = coll.iterator();
      while (it.hasNext())
      {
        SuffixEntry suffixEntry = (SuffixEntry)it.next();
        if (suffixEntry.check4Inflexion(word))
        {
          switch(type)
          {
            case ALL:
              // should(?) generate for derivations of entry
              /*
              AffixEntryExtension extension = suffixEntry.getExtension();
              if (extension != null &&
                  extension.isDerivator() && !extension.isInflexion())
              {
                DictEntry d =
                  suffixEntry.derive(suffixEntry.inflex(word), entry);
                
                if (d.depth() < 2)
                {
                  put(generations, new AnalysisEntry(d));
                  generate(d, generations);
                }
                else
                {                
                   System.out.println(
                     "Generator stops at " + d.depth() + ": " + d);
                }
              }
              else
              {
                put(generations, new AnalysisEntry(entry, suffixEntry));                
              }
              break;
              */
            case INFLEXION: 
              put(generations, new AnalysisEntry(entry, suffixEntry));
              break;
            case DERIVATION:
              DictEntry d = suffixEntry.derive(suffixEntry.inflex(word), entry);
              put(generations, new AnalysisEntry(d));
              // should(?) generate derivations of d
              break;
          }
        }
      }
    }
  }
  
  protected void put(Map generations, AnalysisEntry analysis)
  {   
    String generation = analysis.getInflexedWord();
    Map c = (Map)generations.get(generation);
    
    if (c == null)
    {
      c = new HashMap();
      generations.put(generation, c);
    }
    c.put(analysis.morphString(), analysis);
  }

  /**
   * @return return the dictionaries.
   */
  public Dictionaries getDictionaries()
  {
    return dictionaries;
  }
  
  /**
   * @return return the rules.
   */
  public Rules getRules()
  {
    return rules;
  }
  
  /**
   * @return return the suffixGenerators.
   */
  public Map getSuffixGenerators()
  {
    return suffixGenerators;
  }
}