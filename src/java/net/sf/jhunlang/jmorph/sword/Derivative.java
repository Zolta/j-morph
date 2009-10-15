package net.sf.jhunlang.jmorph.sword;

import java.util.ListIterator;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import java.io.Serializable;

/**
 * Derivative represent case-derivative-case affix categories.
 */ 
public class Derivative implements Serializable
{
  protected SimpleDerivative derivative;

  protected List preCases = new LinkedList();
  protected List postCases = new LinkedList();

  protected String postCase;

  public Derivative(SimpleDerivative derivative)
  {
    this.derivative = derivative;
  }

  public void addPreCase(Case cas)
  {
    preCases.add(cas);
  }

  public void addPostCase(Case cas)
  {
    postCases.add(cas);
  }

  public List getPreCases()
  {
    return preCases;
  }

  public List getPostCases()
  {
    return postCases;
  }

  public SimpleDerivative getDerivative()
  {
    return derivative;
  }

  public String getPostCase()
  {
    if (postCase == null)
    {
      ListIterator it = postCases.listIterator(postCases.size());
      if (it.hasPrevious())
      {
          postCase = ((Case)it.previous()).getName();
      }
      else
      {
        postCase = "";
      }
    }
    return postCase;
  }

  public String contentString()
  {
    StringBuffer sb = new StringBuffer();

    Iterator it = preCases.iterator();
    while (it.hasNext())
    {
      sb.append("+" + it.next());
    }

    sb.append(derivative);

    it = postCases.iterator();
    while (it.hasNext())
    {
      sb.append("+" + it.next());
    }

    return sb.toString();
  }

  public String morphString()
  {
    StringBuffer sb = new StringBuffer();

    Iterator it = preCases.iterator();
    while (it.hasNext())
    {
      sb.append(((Case)it.next()).morphString());
      sb.append(" ");
    }

    sb.append(derivative.morphString());

    it = postCases.iterator();
    if (it.hasNext())
    {
      sb.append(" ");
    }

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

  public String toString()
  {
    return "[" + contentString() + "]";
  }
}

