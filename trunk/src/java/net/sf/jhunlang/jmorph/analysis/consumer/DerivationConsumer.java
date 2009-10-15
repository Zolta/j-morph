package net.sf.jhunlang.jmorph.analysis.consumer;

import java.util.Iterator;

import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.SuffixEntry;

public class DerivationConsumer extends AffixConsumer
{
  protected boolean continueStemming = true;
  
  public DerivationConsumer(X x)
  {
    super(x);
  }

  public void startCache()
  {
    continueStemming = true;
  }

  // return what parent has told while caching
  public boolean cache()
  {
    return continueStemming;
  }

  // return true always for derivationCache; store parent's telling into
  // continueStemming
  public boolean continueStemming(String word, DictEntry entry)
  {
    if (entry.hasFlag(affix.getFlag()))
    {
      DictEntry derivation = affix.derive(word, entry);
      if (derivation != null)
      {
        // pass to parent only if it not rejected earlier
        if (continueStemming)
        {
          continueStemming =
            parent.continueStemming(derivation.getWord(), derivation);
        }
        if (lazy)
        {
          addcase(affix);
          return super.continueStemming(word, entry);
        }
        return continueStemming;
      }
      else
      {
        return true;
      }
    }
    else if (lazy)
    {
      return super.continueStemming(word, entry);
    }
    else
    {
      return true;
    }
  }

  /**
   * Called if the same set of stem candidates
   */
  public boolean continueStemming(String key)
  {
    startCache();
    Iterator it = entries.iterator();
    while (it.hasNext())
    {
      DictEntry entry = (DictEntry)it.next();
      if (entry.hasFlag(affix.getFlag()))
      {
        DictEntry derivation = affix.derive(key, entry);
        if (derivation != null)
        {
          if (continueStemming)
          {
            continueStemming =
              parent.continueStemming(derivation.getWord(), derivation);
          }
        }
      }
    }
    return cache();
  }

  public boolean ignoreCase()
  {
    return (affix != null && affix.ignoreCase()) ||
           (parent != null && parent.ignoreCase());
  }
  
  // suffix flag
  public boolean hasSuffixFlag(int flag)
  {
    if (affix != null && (affix instanceof SuffixEntry))
    {
      return affix.hasFlag(flag);
    }
    else // ????????????? check why not simply delegate to parent???
    {
      return (affix != null && affix.hasFlag(flag)) ||
        (parent != null && parent.hasSuffixFlag(flag));
    }
  }
}
