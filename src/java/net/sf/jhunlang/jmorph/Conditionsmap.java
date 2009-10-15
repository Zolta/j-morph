package net.sf.jhunlang.jmorph;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

public class Conditionsmap implements Serializable
{
  /**
   * The {@link Conditions#index} of the <code>Conditions</code> created next. 
   */
  protected int nextIndex;
  /**
   * Stores different interned Conditions instances
   */
  protected final Map conds = new HashMap();

  public int size()
  {
    return conds.size();
  }

  /**
   * Return the <code>Conditions</code> instance wrapping
   * <code>conditions</code>. Look for the instance in {@link #conds}
   * and return it if found. Otherwise create a new <code>Conditions</code>
   * instance and put it into {@link #conds} and return the new instance.
   * @param conditions the {@link Condition} array to wrap
   * @return the <code>Conditions</code> instance wrapping
   * <code>conditions</code>
   */
  public Conditions getConditions(Condition[] conditions)
  {
    Conditions c = new Conditions(conditions);
    Conditions old = (Conditions)conds.get(c);
    if (old == null)
    {
      c.setIndex(nextIndex++);
      old = c;
      conds.put(old, old);
    }
    return old;
  }
}
