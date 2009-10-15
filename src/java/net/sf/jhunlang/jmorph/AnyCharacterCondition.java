package net.sf.jhunlang.jmorph;

/**
 * AnyCharacterCondition represent a single any character condition.
 */
public class AnyCharacterCondition extends Condition
{
  public final static AnyCharacterCondition YES = new AnyCharacterCondition();
  public final static AnyCharacterCondition NOT = new AnyCharacterCondition();
  
  public AnyCharacterCondition()
  {
    this(false);
  }

  public AnyCharacterCondition(boolean not)
  {
    super(not);
  }

  public boolean admit(char c)
  {
    return not ^ true;
  }
}
