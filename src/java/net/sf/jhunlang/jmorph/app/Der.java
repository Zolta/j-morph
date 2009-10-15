package net.sf.jhunlang.jmorph.app;

import net.sf.jhunlang.jmorph.synth.Generator;

public class Der extends Gen
{
  public static void main(String[] args)
    throws Exception
  {
    Gen der = new Gen("Derivator");
    der.configure(load(args, Generator.DERIVATION));
    der.setVisible(true);
  }
}
