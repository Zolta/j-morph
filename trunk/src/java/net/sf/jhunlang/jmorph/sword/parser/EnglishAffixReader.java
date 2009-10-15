package net.sf.jhunlang.jmorph.sword.parser;

import java.util.Collection;

import net.sf.jhunlang.jmorph.AffixEntry;
import net.sf.jhunlang.jmorph.parser.ParseException;
import net.sf.jhunlang.jmorph.parser.Parser;
import net.sf.jhunlang.jmorph.sword.SwordAffixExtension;

/**
 * EnglishAffixReader stands for reading the hunlex-specific extension
 * of AffixEntry's.
 */
public class EnglishAffixReader extends SwordAffixReader
{
  /**
   * Parse the extension read from the current line of <code>parser</code>
   * and set the resulted {@link EnglishAffixExtensionParser} as the
   * {@link AffixEntry#getExtension()} of  <code>entry</code>.
   * Return <code>entry</code> if the extension is legal, return
   * <code>null</code> otherwise.
   * @param parser the parser providing the extension
   * @param entry the affix entry
   * @return <code>entry</code> or <code>null</code> depending on whether
   * or not the extension is legal
   * @see EnglishAffixExtensionParser#parseDescription(Parser, Collection)
   */
  protected AffixEntry parseExtension(Parser parser, AffixEntry entry)
    throws ParseException
  {    
    SwordAffixExtension ext = new SwordAffixExtension();
    EnglishAffixExtensionParser extParser =
      new EnglishAffixExtensionParser(ext);

    if (!extParser.parseDescription(parser, derivatives))
    {
      return null;
    }

    entry.setExtension(ext);

    String flags = parser.string(true);
    if (flags != null)
    {
      entry.setFlags(flags.toCharArray());
      if (entry.hasFlag(IGNORE_CASE))
      {
        entry.setIgnoreCase(true);
      }
    }
    // parse line even if entry is not allowed!
    return flagFilter.admit(entry) ? entry : null;
  }

  /**
   * Basic test of EglishAffixReader.<p>
   * The command line parameters must specify the affix file and optionally
   * the encoding. The default encoding is ISO-8859.
   * @param args the command line parameters
   */
  public static void main(String[] args)
    throws Exception
  {
    new EnglishAffixReader().test(args);
  }
}
