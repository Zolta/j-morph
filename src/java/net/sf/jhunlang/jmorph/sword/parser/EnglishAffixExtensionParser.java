package net.sf.jhunlang.jmorph.sword.parser;

import java.util.Collection;

import net.sf.jhunlang.jmorph.parser.ParseException;
import net.sf.jhunlang.jmorph.parser.Parser;
import net.sf.jhunlang.jmorph.sword.Case;
import net.sf.jhunlang.jmorph.sword.Derivative;
import net.sf.jhunlang.jmorph.sword.SwordAffixExtension;

/**
 * EnglishAffixExtensionParser stands for parsing hunlex-specific
 * affix extension data.
 */
public class EnglishAffixExtensionParser extends EnglishExtensionParser
{
  public EnglishAffixExtensionParser(SwordAffixExtension ext)
  {
    super(ext);
  }

  /**
   * Parse affix description and return if it is legal.
   * @param parser the parser providing the description
   * @param ders the list of derivatives
   * @return true
   * @throws ParseException if the description is 'syntactically' incorrect -
   * read the implementation for the <i>reverse engineered</i> syntax. 
   */
  public boolean parseDescription(Parser parser, Collection ders)
    throws ParseException
  {
    String affix;
    String description = parser.string(false).intern();
    
    int endIndex = 0;

    while(true)
    {
      int startIndex = description.indexOf(START, endIndex);     
      
      if (startIndex == -1)
      {
        if ((endIndex = description.indexOf(END, startIndex)) == -1)
        {
          throw parser.error("No '" + START + "' found in " + description);
        }
        return true;
      }
      
      affix = endIndex >= startIndex ?
          "" : description.substring(endIndex + 1, startIndex);
      
      endIndex = description.indexOf(END, startIndex);
  
      if (endIndex == -1)
      {
        throw parser.error("Unmatched '" + END + "' in " + description);
      }
  
      String explanation = description.substring(startIndex + 1, endIndex);
      
      if (ders.contains(explanation))
      {        
        Derivative derivative = derivative("", explanation, "");
        addDerivative(parser, derivative);
      }
      else
      {
        addCase(parser, Case.getCase(affix, explanation));
      }
    }
  }
}