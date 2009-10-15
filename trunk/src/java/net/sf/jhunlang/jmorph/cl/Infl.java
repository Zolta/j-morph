package net.sf.jhunlang.jmorph.cl;

import net.sf.jhunlang.jmorph.synth.Generator;

public abstract class Infl extends Gen
{
  public static void main(String[] args)
    throws Exception
  {
    new Gen().configureAndRun(args, Generator.INFLEXION);
  }
}
