package net.sf.jhunlang.jmorph.app;

import net.sf.jhunlang.jmorph.synth.Generator;

public class Infl extends Gen
{
  public static void main(String[] args)
    throws Exception
  {
    Gen infl = new Gen("Inflector");
    infl.configure(load(args, Generator.INFLEXION));
    infl.setVisible(true);
  }
}
