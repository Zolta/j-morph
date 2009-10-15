package net.sf.jhunlang.jmorph.sword;

import net.sf.jhunlang.jmorph.AffixEntry;
import net.sf.jhunlang.jmorph.AffixEntryExtension;
import net.sf.jhunlang.jmorph.AffixFlags;
import net.sf.jhunlang.jmorph.DictEntry;
import net.sf.jhunlang.jmorph.PrefixEntry;

/**
 * SwordAffixExtensionParser stores szoszablya-specific extension data
 * (called morphological description) for affixes.
 */
public class SwordAffixExtension extends SwordExtension
  implements AffixEntryExtension
{
  public SwordAffixExtension()
  {}

  /**
   * Create and return the derivation of <code>root</code> by the deriver
   * <code>entry</code>. The derived word is <code>word</code>.
   * @param entry the deriver affix
   * @param word the derived word
   * @param root the derivation root
   * @return the derived dictionary entry
   */
  public DictEntry derive(AffixEntry entry, String word, DictEntry root)
  {  
    // if root is inflexed (like lőtt <- lő or védelmek <- védelem) then take
    // derive from root of root (like agyonlő or védelemez) and inflex the
    // derivation - and here is the trouble: we can do this only IF inflexion
    // and derivation stand on opposite sides (how could we now ...) and their
    // effects are independent
    if (root.inflexed() && (entry instanceof PrefixEntry))
    {
      DictEntry iRoot = root.getRootEntry(); 
      DictEntry d = new SwordEntry(iRoot, entry, add); // agyonl?
      
      AffixFlags flags = add ?
        entry.getFlags().add(root.getFlags()) : entry.getFlags();
        
      if (add)
      {
        flags = flags.remove((char)entry.getFlag());
      }
        
      DictEntry id = new SwordEntry(
          entry.inflex(word),
          flags.getFlagString().toCharArray(),
          (SwordEntryExtension)root.getInflexion(),
          d);

      return id;
    }
    else
    {
      return new SwordEntry(root, entry, add);
    }
  }
}