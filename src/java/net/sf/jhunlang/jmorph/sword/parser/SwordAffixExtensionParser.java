package net.sf.jhunlang.jmorph.sword.parser;

import java.util.Collection;

import net.sf.jhunlang.jmorph.AffixEntry;
import net.sf.jhunlang.jmorph.parser.ParseException;
import net.sf.jhunlang.jmorph.parser.Parser;

import net.sf.jhunlang.jmorph.sword.Case;
import net.sf.jhunlang.jmorph.sword.Derivative;
import net.sf.jhunlang.jmorph.sword.SwordAffixExtension;

/**
 * SwordAffixExtensionParser stands for parsing szoszablya-specific
 * extension data.
 */
public class SwordAffixExtensionParser extends SwordExtensionParser
{
  protected SwordAffixExtension ext;
  
  public SwordAffixExtensionParser(SwordAffixExtension ext)
  {
    super(ext);
    this.ext = ext;
  }

  /**
   * Parse affix description of <code>entry</code>and return if it is legal.
   * The present implementation return <code>false</code> for the 'lowercase'
   * prefixes. 
   * @param parser the parser providing the description
   * @param ders the list of derivatives
   * @param entry the affix entry
   * @return if the description is legal - if it is not a 'lowercase' prefix
   * @throws ParseException if the description is 'syntactically' incorrect -
   * read the implementation for the <i>reverse engineered</i> syntax. 
   */
  public boolean parseDescription(
    Parser parser, Collection ders, AffixEntry entry)
    throws ParseException
  {
    String affix;
    String description = parser.string(false).intern();

    if (description.charAt(0) == CASE_START)
    {
      if (description.equals("{[lowercase]}"))
      {
        return false;
      }
    }
    else
    {
      int startIndex = description.indexOf(START);
      if (startIndex == -1)
      {
        throw parser.error("No '" + START + "' found in " + description);
      }

      affix = description.substring(0, startIndex);
      int endIndex = description.indexOf(END, startIndex);

      if (endIndex == -1)
      {
        throw unmatched(parser, END, description);
      }

      String explanation = description.substring(startIndex + 1, endIndex);
      Derivative derivative = null;
      
      // preverb
      if ((derivative = preverb(affix, explanation)) != null)
      {
        ext.setAdd(true);
      }
      else
      {
        derivative = derivative(parser, ders, explanation);
      }

      if (derivative != null ||
          (derivative = adjnum(affix, explanation, entry))!= null)
      {
        addDerivative(parser, derivative);
      }
      else
      {
        addCase(parser, Case.getCase(affix, explanation));
      }
      // if continues with { then +[case];
      // if continues with + then [case/derivative]
      // otherwise error
      while (++endIndex < description.length())
      {
        char next = description.charAt(endIndex);
        switch (next)
        {
          // {+[
          case CASE_START:
            if (endIndex + 2 >= description.length() ||               
                description.charAt(endIndex + 1) != PLUS ||
                description.charAt(endIndex + 2) != START)
            {
                throw parser.error("Expected '" + PLUS + START + "' in " +
                  description);
            }

            startIndex = endIndex + 3;
            if ((endIndex = description.indexOf(END, startIndex)) == -1 ||
                endIndex + 1 >= description.length() ||
                description.charAt(endIndex + 1) != CASE_END)
            {
              throw unmatched(parser, CASE_START, description);
            }

            explanation = description.substring(startIndex, endIndex);

            addCase(parser, Case.getCase(explanation));
            endIndex++; // skip CASE_END
            break;
          // +[
          case PLUS:
            endIndex = plus(parser, ders, description, endIndex);
            break;
         default:
            throw parser.error("Unexpected '" + next + "' in " + description);
        }
      }
    }
    return true;
  }
}