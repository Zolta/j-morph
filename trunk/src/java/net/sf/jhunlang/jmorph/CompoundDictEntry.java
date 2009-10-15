package net.sf.jhunlang.jmorph;

import net.sf.jhunlang.jmorph.sword.SwordEntry;
import net.sf.jhunlang.jmorph.sword.SwordEntryExtension;

public class CompoundDictEntry extends SwordEntry
{
  protected DictEntry right;
  protected DictEntry left;

  /**
   * Create a CompoundDictEntry representing the compound word consisting of
   * <code>left</code> and <code>right</code> as component words.
   * <code>add</code> are flags to add to, while <code>remove</code> are those
   * to remove from the inherited flags.  
   * @param left the left component
   * @param right the right component 
   * @param add the flags to add to the inherited flags
   * @param remove the flags to remove form the inherited flags
   * @see #CompoundDictEntry(Rules, DictEntry, DictEntry)
   */
  public CompoundDictEntry(Rules rules,
    DictEntry left, DictEntry right, AffixFlags add, AffixFlags remove)
  {
    this(rules, left, right);
    flags = flags.add(add).remove(remove);
  }
  
  /**
   * Create a CompoundDictEntry representing the compound word consisting of
   * <code>left</code> and <code>right</code> as component words with the
   * following properties:<ul><p>
   * <li>
   * the {@link DictEntry#root} is a new CompoundDictEntry with components
   * <code>left</code> and with the root of <code>right</code> if the latter
   * is not <code>null</code>; the root is <code>null</code> otherwise
   * </li>
   * <li>
   * the {@link DictEntry#flags} are<ul>
   *   <li>
   *   the flags of the <i>leftmost</i> component <i>minus</i>
   *   {@link Rules#suffixPattern}
   *   </li>
   *   <li>
   *      <i>plus</i> the flags of the <i>rightmost</i> component
   *      <i>minus</i>  {@link Rules#prefixPattern}
   *   </li>
   *   </ul>
   * where the <i>leftmost</i> is <code>left</code> if it is not compound; its
   * {@link #getLeftMost()} otherwise, <i>rightmost</i> is <code>right</code>
   * if it is not compound; its {@link #getRightMost()} otherwise
   * <li>
   * the {@link DictEntry#word} is the concatenation of word of
   * <code>left</code> and that of <code>right</code> 
   * </li>
   * <li>
   * the {@link DictEntry#inflexion} is the inflexion of <code>right</code>.
   * Here we assume that only suffix inflexion (i.e. derivation) can be
   * inherited - change it to symmetric AND clear the use of words inflexion,
   * derivation.
   * </li>
   * </ul>
   * @param left the left component
   * @param right the right component 
   */
  public CompoundDictEntry(Rules rules, DictEntry left, DictEntry right)
  {
    super(left.getWord() + right.getWord());
    
    this.left = left;
    this.right = right;

    AffixFlags leftFlags =  left instanceof CompoundDictEntry ?
      ((CompoundDictEntry)left).getLeftMost().getFlags() : left.getFlags();
      
    flags = leftFlags.remove(rules.getSuffixPattern());
    
    AffixFlags rightFlags = right instanceof CompoundDictEntry ?
      ((CompoundDictEntry)right).getRightMost().getFlags() : right.getFlags();

    flags = flags.add(rightFlags.remove(rules.getPrefixPattern()));    
    
    inflexion = right.getInflexion();
    
    if (inflexion != null && inflexion instanceof SwordEntryExtension)
    {
      SwordEntryExtension rightInflexion = (SwordEntryExtension)inflexion;
      // inflexed right entry!
      DictEntry rightRoot = right.getRootEntry();
      if (rightRoot != null && rightInflexion.getDerivatives().size() == 0)
      {
        root = new CompoundDictEntry(rules, left, rightRoot);
        inflexion = rightInflexion;
      }
      else
      {
        inflexion = new SwordEntryExtension(
          rightInflexion.getAllCases(), rightInflexion.getPOSName());
      }
    }
  }

  /**
   * Return true.
   * @return <code>true</code>
   */
  public boolean compound()
  {
    return true;
  }

  /**
   * Return the sum of the length of left and right component.
   * @return the length of this compound entry.
   */
  public int length()
  {
    return left.length() + right.length();
  }

  public DictEntry getRight()
  {
    return right;
  }

  public DictEntry getLeft()
  {
    return left;
  }

  public DictEntry getLeftMost()
  {
    return left instanceof CompoundDictEntry ?
      ((CompoundDictEntry)left).getLeftMost() : left;
  }

  public DictEntry getRightMost()
  {
    return right instanceof CompoundDictEntry ?
      ((CompoundDictEntry)right).getRightMost() : right;
  }

  public String getRelativeRootWord()
  {
    return left.getWord() + right.getRelativeRootWord();
  }

  /**
   * Tells if this entry has been read from an input dictionary.
   * The present implementation returns <code>true</code>.
   * @return if this entry has been read from the dictionary file
   */
  public boolean dictionaryWord()
  {
    return false;
  }

  /**
   * Return the internal <code>String</code> representation of the content
   * of this entry.
   * @return the <code>String</code> representation of the of content of this
   * entry
   */
  public String longContentString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(length() + ", ");
    sb.append(super.longContentString());
    sb.append(" ");
    sb.append(left.toLongString());
    sb.append(" + ");
    sb.append(right.toLongString());
    return sb.toString();
  }

  public String contentString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(length() + ", ");
    sb.append(super.contentString());
    sb.append(" ");
    sb.append(left);
    sb.append(" + ");
    sb.append(right);
    return sb.toString();
  }
  
  protected String shortClassName()
  {
    return "Co";
  }  
}
