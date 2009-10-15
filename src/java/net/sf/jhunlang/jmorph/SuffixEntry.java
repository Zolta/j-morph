package net.sf.jhunlang.jmorph;

import net.sf.jhunlang.jmorph.util.Pair;

/**
 * SuffixEntry instances store suffix inflexion rules.
 */
public class SuffixEntry extends AffixEntry
{
  /**
   * Create a new SuffixEntry instance with the given parameters.
   * @param rules the rules this affix belongs to
   * @param parent the Suffix parent
   * @param index the index of this entry at its parent (informal)
   * @param acondition the conditions of this entry
   * @param strip the characters to strip from stems before adding append
   * @param append the characters to append to stripped stems
   * @exception IllegalArgumentException if strip doesn't satisfy the conditions
   */
  public SuffixEntry(Rules rules, Suffix parent, int index,
    Condition[] acondition, String strip, String append)
  {
    super(rules, parent, index, acondition, strip, append);
  }

  public SuffixEntry(Rules rules, Suffix parent, int index,
    Condition[] acondition, String strip, String append, String morph)
  {
    super(rules, parent, index, acondition, strip, append, morph);
  }

  public SuffixEntry(Suffix parent, SuffixEntry se, int index, AffixFlags flags)
  {
    super(parent, se, index, flags);
  }
  
  public Pair adjustStripAndAppend(String strip, String append)
  {
    return sa(strip, append);
  }
  
  public static Pair sa(String strip, String append)
  {
    if (strip.length() > 0 && append.startsWith(strip))
    {
      String sstrip = "";
      String sappend = append.substring(strip.length());
      //System.out.println(
      //  "SFX(" + strip + "|" + append + ", " + sstrip + "|" + sappend + ")");
      return new Pair(sstrip, sappend);        
    }
    return new Pair(strip, append);
  }
  
  public char getAppendCharAt(int i)
  {
    return append.charAt(i);
  }

  /**
   * Append {@link AffixEntry#strip} to <code>word</code>.
   * Return the resulted <code>String</code>.
   * @param word the word strip to be added to
   * @return the concatenation of <code>word</code> and <code>strip</code>
   */
  public String addStrip(String word)
  {
    return word + strip;
  }

  /**
   * Append {@link AffixEntry#lowerCaseStrip} to <code>word</code>.
   * Return the resulted <code>String</code>.
   * @param word the word strip to be added to
   * @return the concatenation of <code>word</code> and
   * <code>lowerCaseStrip</code>
   * 
   */
  public String addLowerCaseStrip(String word)
  {
    return lowerCaseStrip + word;
  }
  
  /**
   * Return the word by stripping off {@link #strip} from the given word.
   * @param word the word strip to be added to
   * @return the stripped word
   */
  public String strip(String word)
  {
    return word.substring(0, word.length() - sl);
  }

  /**
   * Append {@link AffixEntry#append} to <code>word</code>.
   * Return the resulted <code>String</code>.
   * @param word the word append to be added to
   * @return the concatenation of <code>word</code> and <code>append</code>
   */
  public String append(String word)
  {
    return word + append;
  }

  /**
   * Return the word by stripping {@link #append} from and
   * appending {@link #strip} to <code>word</code>.
   * @param word the word to reverse
   * @return the reversed word
   */
  public final String reverse(String word)
  {
    return addStrip(word.substring(0, word.length() - al));
  }

  /**
   * Return if <code>word</code> satisfies {@link AffixEntry#conditions}.
   * @param word the word to check
   * @return if <code>word</code> satisfies <code>conditions</code>
   */
  public boolean checkStem(CharSequence word)
  {
    int wl = word.length();
    int j = wl - cl;

    if (j < 0)
    {
      return false;
    }

    for(int i = 0; i < cl; i++, j++)
    {
      if (!conditions[i].admit(word.charAt(j)))
      {
        return false;
      }
    }
    return true;
  }

  /**
   * Return if <code>word</code> satisfies {@link AffixEntry#stripConditions}.
   * @param word the word to check
   * @return if <code>word</code> satisfies <code>stripConditions</code>
   */
  public boolean check4Inflexion(CharSequence word)
  {
    if (stripConditions.length == 0)
    {
      return admitStem(word);
    }
    else
    {
      int wl = word.length();
      int j = wl - sl;

      if (j < 0)
      {
        return false;
      }
      
      if (!checkStem(word.subSequence(0, wl - sl)))
      {
        return false;
      }

      for(int i = 0; i < sl; i++, j++)
      {
        if (!stripConditions[i].admit(word.charAt(j)))
        {
          return false;
        }
      }
      return true;
    }
  }

  protected String getClassNameString()
  {
    return "Sfx";
  }
  
  public static void main(String[] args)
  {
    Pair p = SuffixEntry.sa(args[0], args[1]);
    
    System.out.println(args[0]);
    System.out.println(args[1]);
    
    System.out.println(p.getA());
    System.out.println(p.getB());
    
    p = SuffixEntry.sa("b", "bal");
    System.out.println(p.getA());
    System.out.println(p.getB());    
  }
}