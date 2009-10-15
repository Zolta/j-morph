package net.sf.jhunlang.jmorph.sword;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import java.io.Serializable;

import net.sf.jhunlang.jmorph.util.Comparables;

/**
 * SimpleDerivative represent derivative categories. They are mapped by their
 * [{@link #affix}, {@link #type}, {@link #pos}] triplets.
 */ 
public class SimpleDerivative implements Serializable
{
  /**
   * The map of SimpleDerivative instances by their
   * {@link #affix}, {@link #type} and {@link #pos}.
   */
  protected final static Map derivatives = new TreeMap();

  /**
   * The affix morpheme 
   */
  protected String affix;
  /**
   * The type (the name) of this derivative 
   */
  protected String type;
  /**
   * The part-of-speech resulted when applying this derivative 
   */
  protected POSName pos;

  /**
   * Return a SimpleDerivative instance for <code>affix</code>,
   * <code>type</code> and <code>pos</code>. Return the SimpleDerivative
   * instance from {@link #derivatives} for the parameters if any; create
   * and return a new one otherwise.
   * @param affix the morpheme
   * @param type the type
   * @param pos the resulting part-of-speech
   * @return the SimpleDerivative instance for <code>affix</code>,
   * <code>type</code> and <code>pos</code>  
   */
  public static SimpleDerivative getSimpleDerivative(
    String affix, String type, String pos)
  {
    Comparables c = new Comparables(affix, 
      new Comparables(type, pos, false), false);

    SimpleDerivative derivative = (SimpleDerivative)derivatives.get(c);
    if (derivative == null)
    {
      derivative = new SimpleDerivative(affix, type, POSName.getPOSName(pos));
      derivatives.put(c, derivative);
    }
    return derivative;
  }

  /**
   * Return an iterator for all the SimpleDerivative instances created so far.
   * @return an iterator for all the SimpleDerivative instances created so far
   */
  public static Iterator getSimpleDerivativeIterator()
  {
    return derivatives.values().iterator();
  }

  /**
   * Cretae a SimpleDerivative instance for <code>affix</code>,
   * <code>type</code> and <code>pos</code>.
   * @param affix the morpheme
   * @param type the type
   * @param pos the resulting part-of-speech
   */
  protected SimpleDerivative(String affix, String type, POSName pos)
  {
    this.affix = affix;
    this.type = type;
    this.pos = pos;
  }

  /**
   * Return the morpheme of this SimpleDerivative instance.
   * @return the morpheme of this SimpleDerivative instance
   */
  public String getAffix()
  {
    return affix;
  }

  /**
   * Return the type of this SimpleDerivative instance.
   * @return the type of this SimpleDerivative instance
   */
  public String getType()
  {
    return type;
  }

  /**
   * Return the resulting part-of-speech of this SimpleDerivative instance.
   * @return the resulting part-of-speech of this SimpleDerivative instance
   */
  public POSName getPOSName()
  {
    return pos;
  }

  public String morphString()
  {
    StringBuffer sb = new StringBuffer(affix);
    sb.append("_");
    sb.append(type);
    sb.append("_");
    sb.append(pos.getName());
    return sb.toString();
  }

  public String contentString()
  {
    return morphString();
  }

  public String toString()
  {
    return "[" + contentString() + "]";
  }
}