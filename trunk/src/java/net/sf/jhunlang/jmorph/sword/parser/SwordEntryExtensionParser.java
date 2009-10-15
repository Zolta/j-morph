package net.sf.jhunlang.jmorph.sword.parser;

import java.util.Collection;

import net.sf.jhunlang.jmorph.parser.ParseException;
import net.sf.jhunlang.jmorph.parser.Parser;
import net.sf.jhunlang.jmorph.sword.Case;
import net.sf.jhunlang.jmorph.sword.Derivative;
import net.sf.jhunlang.jmorph.sword.POSName;

import net.sf.jhunlang.jmorph.sword.SwordEntryExtension;

public class SwordEntryExtensionParser extends SwordExtensionParser
{
  protected SwordEntryExtension ext;
  
  public SwordEntryExtensionParser(SwordEntryExtension ext)
  {
    super(ext);
    this.ext = ext;
  }

  /**
   * Parse description of a dictionary word.
   */
  public String parseDescription(Parser parser, Collection ders)
    throws ParseException
  {   
    String affix = null;
    String root = null;
    String description = parser.string(true);

    if (description == null)
    {
      return root;
    }

    // look for first START
    int posStart = description.indexOf(START);
    if (posStart == -1)
    {
      return null;
    }

    int posEnd = description.indexOf(END, posStart);
    if (posEnd == -1)
    {
      throw unmatched(parser, START, description);
    }

    // get part-of-speech name between []'s
    String posString = description.substring(posStart + 1, posEnd);

    //String pref = null;
    // if pos is PREF then add prefix case and look for root
    if (posString.equals(PREF))
    {
      affix = description.substring(0, posStart);
      //pref = posString;
      addCase(parser, Case.getCase(affix, posString));
      // cases.add(Case.getCase(affix, posString));
      
      int rootStart = posEnd + 2; // skip +
      if ((posStart = description.indexOf(START, rootStart)) == -1)
      {
        throw parser.error(
          "Root word must be given after '" + PREF + "' in " + description);
      }
      if ((posEnd = description.indexOf(END, posStart)) == -1)
      {
        throw unmatched(parser, START, description);
      }
      root = description.substring(rootStart, posStart);
    }
    else if (posStart > 0)
    {
      root = description.substring(0, posStart);
    }

    posString = description.substring(posStart + 1, posEnd);
    /*
      if (pref != null)
      {
        SimpleDerivative sd =
          SimpleDerivative.getSimpleDerivative(affix, pref, posString);
        addDerivative(parser, new Derivative(sd));
      }
    */
    ext.setPos(POSName.getPOSName(posString));

    int endIndex = posEnd;
    while (++endIndex < description.length())
    {
      char next = description.charAt(endIndex);
      switch (next)
      {
        // erroneous repetition of pos like [adj][adj]; skip it
        case START:
          if ((endIndex = description.indexOf(END, endIndex)) == -1)
          {
            throw unmatched(parser, START, description);
          }
          break;
        // erroneous (?) SEPARATOR like /#van, /#u.a.
        case '/':
          endIndex = description.length();
          break;
        // {
        case CASE_START:
          endIndex++;
          // while +[ look for ] and add what between
          while (endIndex + 1 < description.length() &&
                 description.charAt(endIndex) == PLUS)
          {
            int startIndex;
            // erroneous +{+NOM}
            if (description.charAt(endIndex + 1) == START)
            {
              startIndex = endIndex + 2;
              // erroneous +{[NOM} (missing ] before })
              if ((endIndex = description.indexOf(END, startIndex)) == -1)
              {
                endIndex = description.indexOf(CASE_END, endIndex);
              }

              if (endIndex == -1) //|| endIndex + 1 >= description.length())
              {
                throw unmatched(parser, START, description);
              }
            }
            else // the error handling
            {
              startIndex = endIndex + 1;
              if ((endIndex = description.indexOf(CASE_END, endIndex)) == -1)
              {
                throw unmatched(parser, CASE_START, description);
              }
            }

            String explanation = description.substring(startIndex, endIndex);
            addCase(parser, Case.getCase(explanation));
            if (description.charAt(endIndex) == END)
            {
              endIndex++; // skip END only if ] - error handling again
            }
          }
          
          if (endIndex >= description.length() ||
              description.charAt(endIndex) != CASE_END)
          {
            throw unmatched(parser, CASE_START, description);
          }
          break;
        // +[
        case PLUS:
          endIndex = plus(parser, ders, description, endIndex);
          break;
        default:
          throw parser.error("Unexpected '" + next + "' in " + description);

      }
    }
    
    if (derivatives.size() > 0)
    {
      Derivative der = (Derivative)derivatives.get(derivatives.size() - 1);
      ext.setPos(der.getDerivative().getPOSName());
    }
    return root;
  }
}