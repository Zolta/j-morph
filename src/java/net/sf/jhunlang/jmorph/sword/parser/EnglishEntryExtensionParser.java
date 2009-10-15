package net.sf.jhunlang.jmorph.sword.parser;

import java.util.Collection;

import net.sf.jhunlang.jmorph.parser.ParseException;
import net.sf.jhunlang.jmorph.parser.Parser;
import net.sf.jhunlang.jmorph.sword.Case;
import net.sf.jhunlang.jmorph.sword.Derivative;
import net.sf.jhunlang.jmorph.sword.POSName;

import net.sf.jhunlang.jmorph.sword.SwordEntryExtension;

public class EnglishEntryExtensionParser extends EnglishExtensionParser
{
  protected SwordEntryExtension ext;
  
  public EnglishEntryExtensionParser(SwordEntryExtension ext)
  {
    super(ext);
    this.ext = ext;
  }

  /**
   * Parse the description of a dictionary word. Return the specified root word
   * or <code>null</code>. The description structure is as follows:<p>
   * [root][<POS[<MORPH>*<MORPH]]<p>
   * where<ul>
   * <li>root is the root word if any</li> 
   * <li>POS is the part-of-speech if any</li> 
   * <li>MORPH is a morphological feature</li>
   * @param parser the affix parser
   * @param ders the derivators
   * @return the specified root word or <code>null</code>
   */
  public String parseDescription(Parser parser, Collection ders)
    throws ParseException
  {   
    String description = parser.string(true);
    
    if (description == null)
    {
      return null;
    }
    
    int startIndex = description.indexOf(START);
    
    String root = root(description, startIndex);

    int endIndex;
    
    // pick up part-of-speech
    if (startIndex != -1)
    {
      endIndex = description.indexOf(START, startIndex + 1);
      
      if (endIndex == -1)
      {
        endIndex = description.indexOf(END, startIndex + 1);
        String posString = endIndex == -1 ?
            description.substring(startIndex + 1) :
            description.substring(startIndex + 1, endIndex);
        ext.setPos(POSName.getPOSName(posString));
        return root;
      }
      
      String posString = endIndex == -1 ?
          description.substring(startIndex + 1) :
          description.substring(startIndex + 1, endIndex);
      startIndex = endIndex;
      
      ext.setPos(POSName.getPOSName(posString));
    }
    else
    {
      return root;
    }
    
    while (startIndex != -1)
    {
      endIndex = description.indexOf(START, startIndex + 1);

      String morph = endIndex == -1 ?
          description.substring(startIndex + 1) :
          description.substring(startIndex + 1, endIndex);
      startIndex = endIndex;
      
      Derivative derivative = derivative("", morph, "");

      if (derivative != null)
      {
        addDerivative(parser, derivative);
      }
      else
      {
        addCase(parser, Case.getCase(morph));
      }         
    }
    if (derivatives.size() > 0)
    {
      Derivative der = (Derivative)derivatives.get(derivatives.size() - 1);
      ext.setPos(der.getDerivative().getPOSName());
    }    
    return root;
  }
  
  private String root(String description, int till)
  {
    if (till == -1)
    {
      return null;
    }
    String root = description.substring(0, till);
    till = root.lastIndexOf('_');
    if (till != -1 && till < (root.length() - 1) &&
        Character.isDigit(root.charAt(till + 1)))
    {   
      root = root.substring(0, till);
    }
    root = root.trim();
    return root.length() == 0 ? null : root;
  }
}