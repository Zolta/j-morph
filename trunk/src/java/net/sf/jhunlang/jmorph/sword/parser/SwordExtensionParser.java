package net.sf.jhunlang.jmorph.sword.parser;

import java.util.Collection;

import net.sf.jhunlang.jmorph.parser.ParseException;
import net.sf.jhunlang.jmorph.parser.Parser;
import net.sf.jhunlang.jmorph.sword.Case;
import net.sf.jhunlang.jmorph.sword.Derivative;
import net.sf.jhunlang.jmorph.sword.SimpleDerivative;
import net.sf.jhunlang.jmorph.sword.SwordExtension;

public class SwordExtensionParser extends ExtensionParser
{
  public final static char PLUS = '+';
  public final static char START = '[';
  public final static char END = ']';

  public final static char CASE_START = '{';
  public final static char CASE_END = '}';

  public final static char SEP = '_';
  
  public final static String PREF = "PREF";
  
  public SwordExtensionParser(SwordExtension ext)
  {
    super(ext);
  }

  public int plus(
    Parser parser, Collection ders, String description, int endIndex)
    throws ParseException
  {
    // there are affix lines ending with '+ flags'
    // does this + mean anything special or it is an error?
    if (endIndex + 1 >= description.length())
    {
      return endIndex;
    }

    char next = description.charAt(endIndex + 1);
    // handle erroneous +{+ in occuring in dic file
    if (next == CASE_START)
    {
      return endIndex;
    }

    // handle special case
    // 1. '+Odik+; final + before flags and WHAT does this Odik means?
    // 2. xxx[PREF]
    if ((next = description.charAt(endIndex + 1)) != START)
    {
      int startIndex = endIndex + 1;

      // look for ending + for 'Odik'
      int odikPlus = description.indexOf(PLUS, startIndex);
      if (odikPlus != -1 &&
          description.substring(startIndex, odikPlus).equals("Odik"))
      {
        SimpleDerivative sd =
          SimpleDerivative.getSimpleDerivative("", "Odik", "");
        addDerivative(parser, new Derivative(sd));
        endIndex = odikPlus + 1;
        return endIndex;
      }
      // startIndex points one beyond +
      startIndex = description.indexOf(START, startIndex + 1);
      if (startIndex == -1)
      {
        throw parser.error("Expected '" + START + "' in " + description);
      }

      String affix = description.substring(endIndex + 1, startIndex);
      endIndex = description.indexOf(END, startIndex);

      if (endIndex == -1)
      {
        throw parser.error("Unmatched '" + END + "' in " + description);
      }

      String explanation =
        description.substring(startIndex + 1, endIndex);

      Derivative derivative =
        exceptionalDerivative(affix, explanation, description);      
      if (derivative != null)
      {
        addDerivative(parser, derivative);
      }
      else
      {
        addCase(parser, Case.getCase(affix, explanation));
      }
      return endIndex;
    }

    // the normal '+[' case/derivative start
    int startIndex = endIndex + 2;
    if ((endIndex = description.indexOf(END, startIndex)) == -1)
    {
      throw parser.error("Unmatched '" + END + "' in " + description);
    }

    String explanation = description.substring(startIndex, endIndex);

    Derivative derivative = derivative(parser, ders, explanation);

    if (derivative != null)
    {
      addDerivative(parser, derivative);
    }
    else
    {
      addCase(parser, Case.getCase(explanation));
    }
    return endIndex;
  }

  protected Derivative exceptionalDerivative(
    String affix, String explanation, Object o)
  {
    Derivative der = preverb(affix, explanation);
    return der == null ? adjnum(affix, explanation, o) : der;
  }
  
  protected Derivative preverb(String affix, String explanation)
  {
    if (explanation.equals(PREF))
    {
      SimpleDerivative sd =
        SimpleDerivative.getSimpleDerivative(affix, explanation, "");
      return new Derivative(sd);
    }
    else
    {
      return null;
    }
  }
  
  // hack of hacks: there are numbers defined as prefixes in the affix file
  protected Derivative adjnum(String affix, String explanation, Object o)
  {
    if (affix.length() == 0 && explanation.equals("adj_num"))
    {
      SimpleDerivative sd =
        SimpleDerivative.getSimpleDerivative(affix, explanation, "adj_num");
//System.out.println("**" + o);
      return new Derivative(sd);
    }
    else
    {
      return null;
    }
  }

  /**
   * Create and return a {@link Derivative} if <code>explanation</code>
   * matches <code>derivatives</code>.
   */
  protected Derivative derivative(
    Parser parser, Collection derivatives, String explanation)
  {
    int index0 = explanation.indexOf(SEP) + 1;

    if (index0 == 0)
    {
      return null;
    }

    int index1 = explanation.indexOf(SEP, index0);
    if (index1 == -1)
    {
      return null;
    }

    String affix = explanation.substring(0, index0 - 1);
    String type = explanation.substring(index0, index1);
    
    if (!derivatives.contains(type))
    {
      return null;
    }

    String pos = explanation.substring(index1 + 1);
    if (!pos.toLowerCase().equals(pos))
    {
      return null;
    }
    
    return derivative(affix, type, pos);
  }
  
  protected ParseException unmatched(Parser parser, char c, String description)
  {
    return parser.error("Unmatched '" + c + "' in " + description);
  }
}