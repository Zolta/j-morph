package net.sf.jhunlang.jmorph.cl;

import net.sf.jhunlang.jmorph.synth.Generator;

public abstract class Der extends Gen
{
  public static void main(String[] args)
    throws Exception
  {
    new Gen().configureAndRun(args, Generator.DERIVATION);
  }
}
