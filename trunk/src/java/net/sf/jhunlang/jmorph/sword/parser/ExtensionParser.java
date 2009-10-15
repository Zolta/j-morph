package net.sf.jhunlang.jmorph.sword.parser;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import net.sf.jhunlang.jmorph.parser.Parser;
import net.sf.jhunlang.jmorph.sword.Case;
import net.sf.jhunlang.jmorph.sword.Derivative;
import net.sf.jhunlang.jmorph.sword.SimpleDerivative;
import net.sf.jhunlang.jmorph.sword.SwordExtension;

public class ExtensionParser
{
  protected List derivatives = new LinkedList();
  protected List cases = new LinkedList();

  public ExtensionParser(SwordExtension ext)
  {
    derivatives = ext.getDerivatives();
    cases = ext.getCases();
  }

  /**
   * Create and return a {@link Derivative} if <code>explanation</code>
   * matches <code>derivatives</code>.
   */
  protected Derivative derivative(String affix, String type, String pos)
  {
    return
      new Derivative(SimpleDerivative.getSimpleDerivative(affix, type, pos));
  }

  protected void addCase(Parser parser, Case cas)
  {
    ListIterator it = derivatives.listIterator(derivatives.size());
    if (it.hasPrevious())
    {
      Derivative der = (Derivative)it.previous();
      der.addPostCase(cas);
    }
    else
    {
      cases.add(cas);
    }
  }

  protected void addDerivative(Parser parser, Derivative der)
  {
    // handle special derivative after POSS_SG
    if (cases.size() > 0)
    {
      der.getPreCases().addAll(cases);
      cases.clear();
    }
    derivatives.add(der);
  }
}