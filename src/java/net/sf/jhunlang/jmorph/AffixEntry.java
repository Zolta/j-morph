package net.sf.jhunlang.jmorph;

import java.io.Serializable;

import net.sf.jhunlang.jmorph.util.BooleanResults;
import net.sf.jhunlang.jmorph.util.Pair;

/**
 * The common base class of {@link PrefixEntry} and {@link SuffixEntry}.
 */
public abstract class AffixEntry implements Serializable
{
  public static boolean ignoreStripAppendCase;
  public static boolean compressStripAppend;
  
  protected String morph;
  
  /**
   * Parent of this entry
   */
  protected Affix parent;
  /**
   * Index of this entry at parent
   */
  protected int index;
  /**
   * Flag of parent
   */
  protected int flag;
  /**
   * {@link Conditions} of this entry without those for {@link #strip}
   */
  protected Conditions conds;
  /**
   * Index of {@link #conds} in {@link Conditions}
   */
  protected int condsIndex;
  /**
   * {@link Condition} array of this entry without those for strip
   */
  protected Condition[] conditions;
  /**
   * Length of {@link #conditions}
   */
  protected int cl;
  /**
   * {@link Condition} array of this entry for {@link #strip}
   */
  protected Condition[] stripConditions;
  /**
   * If this entry can be crossed
   */
  protected boolean crossable;

  protected boolean ignoreCase;
  
  protected String caseEnding;

  protected String flagAndCaseString;

  /**
   * The characters to strip from a stem before adding {@link #append}
   */
  protected String strip;
  /**
   * The characters to strip from a stem before adding {@link #append}
   */
  protected String lowerCaseStrip;
  /**
   * Length of {@link #strip}. <code>sl</code> is the length of
   * {@link #stripConditions} as well.
   */
  protected int sl;
  /**
   * The characters to append to stripped stems
   */
  protected String append;
  /**
   * Length of {@link #append}
   */
  protected int al;
  /**
   * Stores crossable/derivation affix flags
   */
  protected AffixFlags flags = AffixFlags.empty;
  /**
   * Stores future extension data (like description).
   */
  protected AffixEntryExtension extension;
  /**
   *  The String representation of this instance
   */
  transient protected String contentString;


  public static boolean isCompressStripAppend()
  {
    return compressStripAppend;
  }

  public static void setCompressStripAppend(boolean compressStripAppend)
  {
    AffixEntry.compressStripAppend = compressStripAppend;
  }

  public static boolean isIgnoreStripAppendCase()
  {
    return ignoreStripAppendCase;
  }

  public static void setIgnoreStripAppendCase(boolean ignoreStripAppendCase)
  {
    AffixEntry.ignoreStripAppendCase = ignoreStripAppendCase;
  }
  
  protected AffixEntry(Rules rules, Affix parent, int index,
    Condition[] acondition, String strip, String append, String morph)
  {
    this(rules, parent, index, acondition, strip, append);
    this.morph = morph;
  }   
  
  /**
   * Create a new AffixEntry instance with the given parameters. None of
   * <code>strip</code> and <code>append</code> can be <code>null</code>.
   * @param rules the rules this affix belongs to
   * @param parent the {@link Affix} parent
   * @param index the index of this entry at its parent (informal)
   * @param acondition the conditions of this entry
   * @param strip the characters to strip from stems before adding append
   * @param append the characters to append to stripped stems
   * throws IllegalArgumentException if either strip or append is null
   * or strip doesn't satisfy the conditions
   */
  protected AffixEntry(Rules rules, Affix parent, int index,
    Condition[] acondition, String strip, String append)
  {
    this.parent = parent;
    this.index = index;
    this.crossable = parent.crossable();
    this.morph = "";

    if (strip == null || append == null)
    {
      throw new IllegalArgumentException(
        "None of strip and append can be null: " + strip + ", " + append);
    }

    String ostrip = strip;
    String oappend = append;
    
    if (ignoreStripAppendCase)
    {
      strip = strip.toLowerCase();
      append = append.toLowerCase();
    }

    if (compressStripAppend)
    {
      Pair p = adjustStripAndAppend(strip, append);
      strip = (String)p.getA();
      append = (String)p.getB();
    }
    
    sl = strip.length();
    stripConditions = new Condition[sl];
    cl = acondition.length;
    System.arraycopy(acondition, cl - sl, stripConditions, 0, sl);
    conditions = new Condition[cl - sl];
    System.arraycopy(acondition, 0, conditions, 0, cl - sl);

    this.conds = rules.getConditionsMap().getConditions(conditions);
    condsIndex = conds.getIndex();
    this.conditions = conds.getConditions();

    for(int i = 0; i < stripConditions.length; i++)
    {
      if (!stripConditions[i].admit(strip.charAt(i)))
      {
        //System.out.println(toLongString() + ": " +
        // ">>" + strip + "<< doesn't satisfy " + stripConditions[i]);
          
        throw new IllegalArgumentException(
          ">>" + strip + "<< doesn't satisfy " + stripConditions[i]);
      }
    }
    
    this.strip = strip.intern();
    this.lowerCaseStrip = strip.toLowerCase().intern();
    this.append = append.intern();

    cl = conditions.length;
    flag = parent.getFlag();
    al = append.length();
  }
  
  protected AffixEntry(Affix parent, AffixEntry ae, int index, AffixFlags flags)
  {
    this.parent = parent;
    this.index = index;
    setFlags(flags);
    crossable = parent.crossable();
    
    flag = ae.flag;
    
    morph = ae.morph;
    
    sl = ae.sl;
    stripConditions = ae.stripConditions;
    cl = ae.cl;
    conditions = ae.conditions;
    conds = ae.conds;
    condsIndex = ae.condsIndex;
    conditions = ae.conditions;

    strip = ae.strip;
    lowerCaseStrip = ae.lowerCaseStrip;
    append = ae.append;

    cl = ae.cl;
    al = ae.al;
  }
  
  /**
   * Return if inflexing with this affix gives back the word itself i.e. if
   * {@link #strip} is {@link #append}.
   * @return if inflexing with this affix gives back the word itself
   */
  public boolean same()
  {
    return strip == append; // both are interned
  }

  /**
   * Return the length of {@link #strip}.
   * @return the length of {@link #strip}
   */
  public int getStripLength()
  {
    return sl;
  }

  /**
   * Create and return a {@link DictEntry} instance for derivation of
   * <code>word</code> by this entry. If {@link #extension} is not
   * <code>null</code> then call its <code>derive</code> method,
   * otherwise create {@link DictEntry} for <code>word</code> with
   * <code>root</code> and {@link #flags}.
   * @param word the derived word
   * @param root the root of the derivatoin
   * @return a {@link DictEntry} representing <code>word</code> as derived
   * from <code>root</code> by this entry
   */
  public DictEntry derive(String word, DictEntry root)
  {
    return extension != null ?
      extension.derive(this, word, root) : new DictEntry(word, root, flags);
  }

  public String getCaseEnding()
  {
    if (caseEnding == null)
    {
     caseEnding = extension != null ?  extension.getCaseEnding() : "";
    }
    return caseEnding;
  }

  public String getCase()
  {
    return extension != null ?
      extension.getCase() : "";
  }
  
  public Conditions getConditions()
  {
    return conds;
  }

  /**
   * Return if this entry can be crossed.
   * @return if this affix can crossed with other crossable affixes
   */
  public boolean crossable()
  {
    return crossable;
  }

  /**
   * Return the parent {@link Affix} of this entry.
   * @return the parent of this entry
   */
  public Affix getParent()
  {
    return parent;
  }

  /**
   * Return the string this entry appends/prepends to stems.
   * @return the string this entry appends/prepends to stems
   */
  public String getAppend()
  {
    return append;
  }

  /**
   * Return the string this entry strips off from stems before appending.
   * @return the strip of this entry
   */
  public String getStrip()
  {
    return strip;
  }

  /**
   * Return the index of this entry in its parent {@link Affix}.
   * @return the index of this entry in its parent
   */
  public int getIndex()
  {
    return index;
  }

  /**
   * Return the flag (the caracter identifier) of this entry.
   * @return the flag of this entry
   */
  public int getFlag()
  {
    return flag;
  }

  /**
   * Return length of {@link #conditions}.
   * @return the length of conditions without those for strip
   */
  public int rawConditionLength()
  {
    return cl;
  }

  /**
   * Return if <code>word</code> satisfies {@link #conditions}.
   * Calls {@link #checkStem(java.lang.CharSequence)}.
   * @param word the word to check
   * @return if <code>word</code> satisfies the conditions
   */
  public boolean admitStem(CharSequence word)
  {
    return checkStem(word);
  }

  /**
   * Return if <code>word</code> satisfies {@link #conditions}.
   * This method uses the result caching mechanism of {@link Conditions}
   * by calling {@link #checkStem(java.lang.CharSequence)} only if the cached
   * result in <code>results</code> is invalid.
   * @param word the word to check
   * @return if <code>word</code> satisfies the conditions
   */
  public boolean admitStem(CharSequence word, BooleanResults results)
  {
    return results.valid(condsIndex) ?
      results.result(condsIndex) :  results.set(condsIndex, checkStem(word));
  }

  /**
   * Set the {@link #flags} of this entry.
   * @param flagCharacters the flag characters to set
   */
  public void setFlags(char[] flagCharacters)
  {
    setFlags(AffixFlags.getAffixFlags(flagCharacters));
  }

  public void setFlags(AffixFlags flags)
  {
    this.flags = flags;
    flagAndCaseString = null;
  }

  /**
   * Return {@link #flags} of this entry.
   * @return {@link #flags} of this entry.
   */
  public AffixFlags getFlags()
  {
    return flags;
  }

  public String getFlagAndCaseString()
  {
    if (flagAndCaseString == null)
    {
      flagAndCaseString = getFlags().getFlagString() + getCaseEnding();
    }
    return flagAndCaseString;
  }

  public void setExtension(AffixEntryExtension extension)
  {
    this.extension = extension;
  }

  /**
   * Return the {@link #extension} of this entry.
   * @return the {@link #extension} of this entry
   */
  public AffixEntryExtension getExtension()
  {
    return extension;
  }
  
  /**
   * @return return {@link #ignoreCase}.
   */
  public boolean ignoreCase()
  {
    return ignoreCase;
  }
  
  /**
   * @param ignoreCase the ignoreCase to set.
   */
  public void setIgnoreCase(boolean ignoreCase)
  {
    this.ignoreCase = ignoreCase;
  }
  
  /**
   * Return if {@link #flags} contains <code>flag</code>.
   * @param flag the flag to check
   * @return if {@link #flags} contains <code>flag</code>
   */
  public boolean hasFlag(int flag)
  {
    return flags != null && flags.hasFlag(flag);
  }

  public abstract Pair adjustStripAndAppend(String strip, String append);
  /**
   * Return if <code>word</code> satisfies {@link #conditions}.
   * @param word the word to check
   * @return if the word satisfies the {@link #conditions}
   */
  public abstract boolean checkStem(CharSequence word);

  /**
   * Return if <code>word</code> satisfies {@link #stripConditions}.
   * @param word the word to check
   * @return if the word satisfies the {@link #stripConditions}
   */
  public abstract boolean check4Inflexion(CharSequence word);

  /**
   * Add {@link #strip} to <code>word</code> and return the resulted string.
   * @param word the word strip to be added to
   * @return the string resulted by adding {@link #strip} to <code>word</code>
   */
  public abstract String addStrip(String word);

  /**
   * Add {@link #strip} all lowercase to <code>word</code> and return the 
   * resulted string.
   * @param word the word strip to be added to
   * @return the string resulted by adding lower case {@link #strip} to
   * <code>word</code>
   */
  public abstract String addLowerCaseStrip(String word);

  /**
   * Strip off {@link #strip} from <code>word</code>.
   * @param word the word to strip
   * @return the stripped word
   */
  public abstract String strip(String word);

  /**
   * Return the word by appending {@link #append} <code>word</code>.
   * @param word the word append to be added to
   * @return the extended word
   */
  public abstract String append(String word);

  /**
   * Return the word by stripping {@link #append} from and
   * appending {@link #strip} to <code>word</code>.
   * @param word the word to reverse
   * @return the reversed word
   */
  public abstract String reverse(String word);

  public abstract char getAppendCharAt(int i);

  /**
   * Return the word by stripping {@link #strip} from and
   * appending {@link #append} to <code>word</code>.
   * @param word the word to inflex
   * @return the inflexed word
   */
  public final String inflex(String word)
  {
    return append(strip(word));
  }
  
  public String morphString()
  {
    return extension == null ? morph : extension.morphString();
  }

  /** 
   * Return the String representation of this entry.
   * @return the String representation of this entry
   */
  public String longContentString()
  {
    StringBuffer sb = new StringBuffer(parent.longContentString());
    sb.append(", " + index + ", -" + strip + ", +" + append + ", ");
    sb.append(conds.contentString());
    sb.append("<" + new Conditions(stripConditions).contentString() + ">");

    if (flags != null && flags != AffixFlags.empty)
    {
      //sb.append(", " + flags.intern());
    }
    if (extension != null)
    {
      sb.append(", " + extension.toString());
    }
    else if (morph != null)
    {
      sb.append(", " + morph);        
    }
    return new String(sb);
  }

  /** 
   * Return the String representation of this entry.
   * @return the String representation of this entry
   */
  public String contentString()
  {
    if (contentString == null)
    {
      StringBuffer sb = new StringBuffer(parent.contentString() + ", " + index);
      sb.append(", +" + append);
      if (extension != null)
      {
        sb.append(", " + extension.toString());
      }
      else if (morph != null)
      {
        sb.append(", " + morph);        
      }
      contentString = new String(sb);
    }
    return contentString;
  }

  protected String getClassNameString()
  {
    return "Afx";
  }

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
    StringBuffer sb = new StringBuffer(getClassNameString());
    sb.append("[");
    sb.append(contentString());
    sb.append("]");
    return new String(sb);
  }
}