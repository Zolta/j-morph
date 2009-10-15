package net.sf.jhunlang.jmorph.sword;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.List;
import java.util.LinkedList;

import net.sf.jhunlang.jmorph.DictEntryExtension;

public class SwordExtension implements DictEntryExtension
{
  protected List derivatives = new LinkedList();
  protected List cases = new LinkedList();

  protected boolean add;

  protected String caseEnding;
  protected String cas;

  public SwordExtension()
  {}

  public SwordExtension(List cases)
  {
    this.cases = cases;
  }

  public SwordExtension(SwordExtension ext)
  {
    derivatives = ext.getDerivatives();
    cases = ext.getCases();
  }
  
  public void setAdd(boolean b)
  {
    add = b;
  }

  public POSName getPOSName()
  {
    int index = derivatives.size() - 1;
    return index == -1 ? POSName.empty :
      ((Derivative)derivatives.get(index)).getDerivative().getPOSName();
  }

  public String getPOS()
  {
    return getPOSName().getName();
  }
  
  public boolean isDerivator()
  {
    return !derivatives.isEmpty();
  }

  public boolean isInflexion()
  {
    Iterator it = getAllCases().iterator();
    while (it.hasNext())
    {
      Case c = (Case)it.next();
      if (!c.zero())
      {
        return true;
      }
    }
    return false;
  }
  
  public List getCases()
  {
    return cases;
  }

  // return cases AND postCases of last derivative
  public List getAllCases()
  {
    if (derivatives.size() > 0)
    {
      List allCases = new LinkedList();
      Derivative derivator =
        (Derivative)derivatives.get(derivatives.size() - 1);
      allCases.addAll(derivator.getPostCases());
      allCases.addAll(cases);
      return allCases;
    }
    else
    {
      return cases;
    }
  }

  public List getDerivatives()
  {
    return derivatives;
  }

  public String getCaseEnding()
  {
    if (caseEnding == null)
    {
      StringBuffer sb = new StringBuffer();
      if (cases.size() > 0)
      {
        Iterator it = cases.iterator();
        while (it.hasNext())
        {
          sb.append("+" + it.next());
        }
      }
      else if (derivatives.size() > 0)
      {
        ListIterator dit = derivatives.listIterator(derivatives.size());
        Derivative der = (Derivative)dit.previous();

        Iterator it = der.getPostCases().iterator();
        while (it.hasNext())
        {
          sb.append("+" + it.next());
        }
      }
      caseEnding = sb.toString();
    }
    return caseEnding;
  }

  public String getCase()
  {
    if (cas == null)
    {
      ListIterator it = cases.listIterator(cases.size());
      if (it.hasPrevious())
      {
        cas = ((Case)it.previous()).getName();
      }
      else
      {
        it = derivatives.listIterator(derivatives.size());
        if (it.hasPrevious())
        {
          Derivative der = (Derivative)it.previous();
          cas = der.getPostCase();
        }
        else
        {
          cas = "";
        }
      }
    }
    return cas;
  }

  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    Iterator it = derivatives.iterator();

    while (it.hasNext())
    {
      sb.append("+" + it.next());
    }

    it = cases.iterator();
    while (it.hasNext())
    {
      sb.append("+" + it.next());
    }
    return sb.toString();
  }

  public String morphString()
  {
    StringBuffer sb = new StringBuffer();
    Iterator it = derivatives.iterator();

    while (it.hasNext())
    {
      sb.append(((Derivative)it.next()).morphString());
      if (it.hasNext())
      {
        sb.append(" ");
      }
    }

    if (derivatives.size() > 0 && cases.size() > 0)
    {
      sb.append(" ");
    }

    it = cases.iterator();

    while (it.hasNext())
    {
      sb.append(((Case)it.next()).morphString());
      if (it.hasNext())
      {
        sb.append(" ");
      }
    }
    return sb.toString();
  }
  
  public String inflexionString()
  {
    StringBuffer sb = new StringBuffer();

    Iterator it = cases.iterator();

    while (it.hasNext())
    {
      sb.append(((Case)it.next()).morphString());
      if (it.hasNext())
      {
        sb.append(" ");
      }
    }
    return sb.toString();
  }
  
  public String derivatorString()
  {
    StringBuffer sb = new StringBuffer();
    Iterator it = derivatives.iterator();

    while (it.hasNext())
    {
      Derivative der = (Derivative)it.next();
      if (der.getPreCases().size() != 0)
      {
        break;
      }
      if (sb.length() != 0)
      {
        sb.append(' ');
      }
      sb.append(der.getDerivative().morphString());
      /*
      if (der.getPostCases().size() != 0)
      {
        break;
      }
      */
    }
    return sb.toString();
  }
}