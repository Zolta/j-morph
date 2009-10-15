package net.sf.jhunlang.jmorph;

import java.io.Serializable;

/**
 * Conditions instances encapsulate {@link Condition} arrays. Conditions
 * overrites hashCode and equals so that Conditions instances be identified
 * by the wrapped Condition arrays. As there are about 100 times more affix
 * entries than Condition arrays, storing the result of checking a word against
 * different Conditions instances in
 *  {@link net.sf.jhunlang.jmorph.util.BooleanResults}
 * dramatically lessens the number of condition checks performed.
 */
public class Conditions implements Serializable
{
  /**
   * The array of encapsulated {@link Condition} instances.
   */
  protected Condition[] conditions;
  /**
   * The String representing the {@link Condition} array of this Conditions.
   */
  protected String contentString;
  /**
   * The hash code of this instance computed by the constructor.
   */
  protected int hash;
  /**
   * The identifier index of this condition instance set by
   * {@link Conditionsmap#getConditions(Condition[])}
   */
  protected int index = -1;

  /**
   * Return the <code>index</code> of this instance.
   * @return the <code>index</code> of this instance
   */
  public int getIndex()
  {
    return index;
  }
  
  protected void setIndex(int index)
  {
    this.index = index;
  }
  
  /**
   * Create a Conditions instance wrapping the given array of
   * {@link Condition}'s. The only constructor being protected, the normal
   * way of mapping {@link Condition} arrays to Conditions intances is to
   * call the static {@link #getConditions} method.
   * @param conditions the {@link Condition} array to wrap
   */
  protected Conditions(Condition[] conditions)
  {
    this.conditions = conditions;
    
    StringBuffer sb = new StringBuffer();
    for(int i = 0; i < conditions.length; i++)
    {
      sb.append(conditions[i].contentString());
      hash = 31 * hash + conditions[i].hashCode();
    }
    contentString = new String(sb);
  }

  /**
   * Return the array of {@link Condition} instances wrapped by this instance.
   * @return the <code>Condition</code> array of this instance
   */
  public Condition[] getConditions()
  {
    return conditions;
  }

  /**
   * Return the hash code computed by the constructor.
   * @return the hash code of this instance
   */
  public int hashCode()
  {
    return hash;
  }

  /**
   * Return if this instance is equal to the given object.
   * Return false if <code>o</code> is not a <code>Conditions</code> instance.
   * Otherwise return if the {@link #contentString}'s are equal.
   * @param o the object with which to compare
   * @return if this instance and <code>o</code> are equal
   */
  public boolean equals(Object o)
  {
    if (o instanceof Conditions)
    {
      Conditions oc = (Conditions)o;
      return contentString.equals(oc.contentString());
    }
    return false;
  }

  public String contentString()
  {
    return contentString;
  }

  public String toString()
  {
    return "Conditions[" + contentString + "]";
  }
}