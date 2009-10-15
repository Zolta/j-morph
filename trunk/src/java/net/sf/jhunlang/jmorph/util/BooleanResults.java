package net.sf.jhunlang.jmorph.util;

public class BooleanResults
{
  protected int round;
  protected int[] rounds;
  protected boolean[] results;

  public BooleanResults(int size)
  {
    rounds = new int[size];
    results = new boolean[size];
  }

  public int nextRound()
  {
    return ++round;
  }

  public boolean valid(int index)
  {
    return round == rounds[index];
  }

  public boolean result(int index)
  {
    return results[index];
  }

  public boolean set(int index, boolean result)
  {
    rounds[index] = round;
    return results[index] = result;
  }
}