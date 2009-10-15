package net.sf.jhunlang.jmorph;

/**
 * CharacterCondition instances store single character conditions.
 */
public class CharacterCondition extends Condition
{
  /**
   * The character of the condition.
   */
  protected char character;

  /**
   * Create a new CharacterCondition with the given character.
   * The created condition will be affirmative, only the given character
   * will satisfy it.
   * @param character the condition character
   */
  public CharacterCondition(char character)
  {
    this(character, false);
  }

  /**
   * Create a new CharacterCondition with the given character.
   * The created condition will be negated if <code>not</code> is true.
   * @param character the condition character
   * @param not if the condition is negated
   */
  public CharacterCondition(char character, boolean not)
  {
    super(not);
    this.character = character;
    contentString = super.contentString() + character;
  }

  /**
   * Return the condition character
   */
  public char getCharacter()
  {
    return character;
  }

  /**
   * Return whether or not the given character satisfies this condition.
   */
  public boolean admit(char c)
  {
    return not ^ (c == character);
  }
}
