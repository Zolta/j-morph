package net.sf.jhunlang.jmorph;

public class NumberEntry extends DummyEntry
{
  public NumberEntry(String word)
  {
    super(word);
  }
  
  /* 
   * @see net.sf.jhunlang.jmorph.DictEntry#getPOS()
   */
  public String getPOS()
  {
    return "adj_num";
  }
  
  /**
   * Return the inflexed word resulted by applying <code>affix</code> to the
   * {@link #word} of this entry.
   * @param affix the affix
   * @return the inflexed word
   */
  public String inflex(AffixEntry affix)
  {
    return affix.append(word);
  }
  
  /**
   * Return the inflexed word resulted by applying <code>prefix</code> and
   * <code>suffix</code> to the {@link #word} of this entry.
   * @param prefix the prefix inflexion
   * @param suffix the suffix inflexion
   * @return the inflexed word
   */
  public String inflex(PrefixEntry prefix, SuffixEntry suffix)
  {
    return prefix.append(suffix.append(word));
  }
  
  
}
