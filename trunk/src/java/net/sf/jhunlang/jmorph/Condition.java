package net.sf.jhunlang.jmorph;

import java.io.Serializable;

/**
 * Condition instances store affix rule conditions. Overwrites hashCode and
 * equals so that <code>Conditions</code> storing the same conditions be equal.
 */
public abstract class Condition implements Serializable
{
  /**
   * If this condition is negated
   */
  protected boolean not;
  /**
   * The String representing the content of this condition.
   */
  protected String contentString;

  /**
   * Create a new Condition with given 'negated' state.
   * @param not if this Condition is negated
   */
  protected Condition(boolean not)
  {
    this.not = not;
    contentString = not ? "^" : "";
  }

  /**
   * Return if this <code>Condition</code> is negated
   * @return if this condition is negated
   */
  public boolean not()
  {
    return not;
  }

  /**
   * Return if the character <code>c</code> satisfies this condition
   * @param c the character to check
   * @return if <code>c</code> satisfies this condition
   */
  public abstract boolean admit(char c);

  /**
   * Return the hash code of this instance
   * @return the hash code
   */
  public int hashCode()
  {
    return contentString.hashCode();
  }

  /**
   * Return if this instance is equal to the given object.
   * Return false if class of <code>o</code> is not the class of this instance.
   * Otherwise return if the {@link #contentString}'s are equal.
   * @param o the object with which to compare
   * @return if this instance and <code>o</code> are equal
   */
  public boolean equals(Object o)
  {
    if (o.getClass() == getClass())
    {
      return contentString.equals(((Condition)o).contentString);
    }
    return false;
  }

  /**
   * Return internal <code>String</code> representation of the content of
   * this instance.
   * @return the internal <code>String</code> represantion
   */
  public String contentString()
  {
    return contentString;
  }

  /**
   * Return String representation of this <code>Condition</code> reflecting
   * the runtime type (class) of the instance.
   * @return the <code>String</code> represantion of this instance
   */
  public String toString()
  {
    String s = getClass().getName();
    s = s.substring(s.lastIndexOf('.') + 1);
    return s + "[" + contentString() + "]";
  }
}
