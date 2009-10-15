package net.sf.jhunlang.jmorph.analysis;

import net.sf.jhunlang.jmorph.AffixEntry;
import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.PrefixEntry;
import net.sf.jhunlang.jmorph.SuffixEntry;

/**
 * AnalysisEntry describes either an uninflexed dictionary word or an
 * inflexion of a dictionary word either by a prefix rule or by a
 * suffix rule or by a crossable prefix and a crossable suffix rule
 */
public class AnalysisEntry implements Analysis
{
  /**
   * The dictionary entry
   */
  protected DictEntry dict;
  /**
   * The first applied inflexion if any
   */
  protected AffixEntry affix0;
  /**
   * The second applied inflexion if any
   */
  protected SuffixEntry affix1;

  /**
   * Create a new AnalysisEntry for the given dictionary word.
   * @param dict the dictionary word
   */
  public AnalysisEntry(DictEntry dict)
  {
    this.dict = dict;
  }

  /**
   * Create a new AnalysisEntry for the inflexion by <code>affix</code> of
   * the dictionary word <code>dict</code>.
   * by the given rule
   * @param dict the dictionary word
   * @param affix the applied affix rule
   */
  public AnalysisEntry(DictEntry dict, AffixEntry affix)
  {
    this(dict);
    this.affix0 = affix;
  }

  /**
   * Create a AnalysisEntry for the inflexion by <code>prefix</code> and
   * <code>suffix</code> of the dictionary word <code>dict</code>.
   * @param dict the dictionary word
   * @param prefix the applied prefix rule
   * @param suffix the applied suffix rule
   */
  public AnalysisEntry(DictEntry dict, PrefixEntry prefix, SuffixEntry suffix)
  {
    this(dict);
    this.affix0 = prefix;
    this.affix1 = suffix;
  }

  /**
   * Return the 'absolute stem' of this entry.
   * @return the 'aboslute stem' of this entry
   * @see DictEntry#getAbsoluteRootWord()
   */
  public String getAbsoluteRootWord()
  {
    return dict.getAbsoluteRootWord();
  }

  /**
   * Return the 'dictionary stem' of this entry.
   * @return the 'dictionary stem' of this entry
   * @see DictEntry#getDictionaryRootWord()
   */
  public String getDictionaryRootWord()
  {
    return dict.getDictionaryRootWord();
  }

  /**
   * Return the 'relative stem' of this entry.
   * @return the 'relative stem' of this entry
   * @see DictEntry#getRelativeRootWord()
   */
  public String getRelativeRootWord()
  {
    return dict.getRelativeRootWord();
  }

  public boolean derived()
  {
    return dict.derived();
  }

  public String getPOS()
  {
    return dict.getPOS();
  }

  public String getCaseEnding()
  {
    if (affix0 != null && (affix0 instanceof SuffixEntry))
    {
      return affix0.getCase();
    }
    else if (affix1 != null)
    {
      return affix1.getCase();
    }
    else
    {
      return dict.getCaseEnding();
    }
  }

  /**
   * Return the inflexed word.
   */
  public String getInflexedWord()
  {
    if (affix1 != null)
    {
      return dict.inflex((PrefixEntry)affix0, affix1);
    }
    else if (affix0 != null)
    {
      return dict.inflex(affix0);
    }
    else
    {
      return dict.getWord();
    }
  }

  public DictEntry getDictEntry()
  {
    return dict;
  }

  /**
   * Set {@link #affix0}
   * @param entry the new affix entry
   */
  public void setAffixEntry0(AffixEntry entry)
  {
    affix0 = entry;
  }

  /**
   * Return the first inflexion rule if any
   */
  public AffixEntry getAffixEntry0()
  {
    return affix0;
  }

  public PrefixEntry getPrefixEntry()
  {
    if (affix0 != null && (affix0 instanceof PrefixEntry))
    {
      return (PrefixEntry)affix0;
    }
    else
    {
      return null;
    }
  }

  public SuffixEntry getSuffixEntry()
  {
    if (affix0 != null && (affix0 instanceof SuffixEntry))
    {
      return (SuffixEntry)affix0;
    }
    else
    {
      return affix1;
    }
  }

  /**
   * Return the second inflexion rule if any.
   */
  public SuffixEntry getAffixEntry1()
  {
    return affix1;
  }

  /**
   * Return if this entry represents an inflexed word.
   */
  public boolean inflexed()
  {
    return affix0 != null || dict.inflexed();
  }

  public boolean compound()
  {
    return dict.length() > 1;
  }
  
  public String relativeMorphString()
  {
    return morphString(dict.getRelativeRootEntry());
  }

  public String dictionaryMorphString()
  {
    return morphString(dict.getDictionaryRootEntry());
  }

  public String absoluteMorphString()
  {
    return morphString(dict.getAbsoluteRootEntry());
  }
  
  public String morphString()
  {
    return dictionaryMorphString();
  }
  
  // return morph string with derivations up to the dictionary root
  protected String morphString(DictEntry stem)
  {
    String s = dict.getPOS();
    String d = dict.derivatorString(stem);
    
    if (d.length() > 0)
    {
      s += ' ' + d;
    }

    String inflexion;
    SuffixEntry suffix = getSuffixEntry();
    
    if (suffix != null)
    {
      inflexion = suffix.morphString();
    }
    else
    {
      inflexion = dict.inflexionString();
    }
    
    if (inflexion.length() == 0)
    {
      return s;
    }
    else if (s.length() == 0)
    {
      return inflexion;
    }
    else
    {
      return s + ' ' + inflexion;
    }
  }
  
  /**
   * Return internal String representation of this instance.
   */
  public StringBuffer contentString()
  {
    StringBuffer sb;
    if (dict.inflexed())
    {
      sb = new StringBuffer(dict.getRelativeRootEntry().toString());
      // append dict inflexion only if not inflexed
      if (affix0 == null)
      {
        sb.append(", " + dict.getInflexion());
      }
    }
    else
    {
      sb = new StringBuffer(dict.toString());
    }

    if (affix0 != null)
    {
      sb.append(", " + affix0);
    }
    if (affix1 != null)
    {
      sb.append(", " + affix1);
    }
    return sb;
  }

  public StringBuffer longContentString()
  {
    StringBuffer sb = new StringBuffer(dict.toLongString());
    if (affix0 != null)
    {
      sb.append(", " + affix0.toLongString());
    }
    if (affix1 != null)
    {
      sb.append(", " + affix1.toLongString());
    }
    return sb;
  }

  protected String getClassNameString()
  {
    return "Se";
  }

  /**
   * Return String representation of this instance reflecting the
   * runtime type (class)
   */
  public String toLongString()
  {
    StringBuffer sb = new StringBuffer(getClassNameString());
    sb.append("[");
    sb.append(longContentString());
    sb.append("]");
    return new String(sb);
  }

  public String toString()
  {
    return contentString().toString();
  }
}