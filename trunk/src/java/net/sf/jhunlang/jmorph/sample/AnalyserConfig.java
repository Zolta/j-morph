package net.sf.jhunlang.jmorph.sample;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import net.sf.jhunlang.jmorph.analysis.Analyser;
import net.sf.jhunlang.jmorph.app.Morph;
import net.sf.jhunlang.jmorph.factory.Definition;
import net.sf.jhunlang.jmorph.factory.JMorphFactory;

import net.sf.jhunlang.jmorph.parser.AbstractReader;
import net.sf.jhunlang.jmorph.sword.parser.SwordReader;
import net.sf.jhunlang.jmorph.sword.parser.SwordAffixReader;

/**
 * AnalyserConfig shows how to configure and instantiate an Analyser.
 */
public class AnalyserConfig {

	/**
	 * Create and return a {@link Definition} for the specified file understood
	 * by an instance of <code>classname</code>. Read {@link #main(String[])} as
	 * sample.
	 * 
	 * @param resourceUrl
	 * @param encoding
	 * @param clz
	 */
	public Definition createDefinition(URL resourceUrl, String encoding,
			Class clz) throws IOException, IllegalAccessException,
			InstantiationException {
		return new Definition(resourceUrl, encoding, (AbstractReader) clz
				.newInstance());
	}

	/**
	 * Create and return a {@link Definition} for the specified file understood
	 * by an instance of <code>classname</code>. Read {@link #main(String[])} as
	 * sample.
	 * 
	 * @param resourceFileName
	 * @param encoding
	 * @param clz
	 */
	public Definition createDefinition(String resourceFileName,
			String encoding, Class clz) throws IOException,
			IllegalAccessException, InstantiationException {
		URL fileURL = new File(resourceFileName).toURL();
		return new Definition(fileURL, encoding, (AbstractReader) clz
				.newInstance());
	}

	/**
	 * Set the file enlisting the derivatives on <code>def</code>.
	 * 
	 * @param def
	 *            the definition
	 * @param file
	 *            the file
	 */
	public void setDerivativeFile(Definition def, String file) {
		def.addExtensionLine(SwordAffixReader.DERS + " " + file);
	}

	/**
	 * Set the file specifying the flag filters on <code>def</code>.
	 * 
	 * @param def
	 *            the definition
	 * @param file
	 *            the file
	 */
	public void setFlagfilterFile(Definition def, String file) {
		def.addExtensionLine(SwordAffixReader.FLAGS + " " + file);
	}

	/**
	 * Set the file enlisting allowed pos pairs in compound words on
	 * <code>def</code>.
	 * 
	 * @param def
	 *            the definition
	 * @param file
	 *            the file
	 */
	public void setCompoundFile(Definition def, String file) {
		def.addExtensionLine(SwordAffixReader.COMPOUND_CONTROLLER + " " + file);
	}

	/**
	 * Set the depth of the derivation recursion on <code>def</code>.
	 * 
	 * @param def
	 *            the definition
	 * @param depth
	 *            the depth of the recursion
	 */
	public void setRecursionDepth(Definition def, int depth) {
		def.addExtensionLine(SwordAffixReader.REC + " " + depth);
	}

	static String encoding = "ISO-8859-2";

	/**
	 * The source of main for those who cannot get it from cvs:
	 * 
	 * <pre>
	 * AnalyserConfig ac = new AnalyserConfig();
	 * Definition affixDef = ac.createDefinition(&quot;hu_HU.aff&quot;, &quot;ISO-8859-2&quot;,
	 * 		SwordAffixReader.class);
	 * Definition dicDef = ac.createDefinition(&quot;hu_HU.dic&quot;, &quot;ISO-8859-2&quot;,
	 * 		SwordReader.class);
	 * ac.setDerivativeFile(affixDef, &quot;derivatives.lst&quot;);
	 * ac.setRecursionDepth(affixDef, 4);
	 * ac.setCompoundFile(affixDef, &quot;compound.lst&quot;);
	 * JMorphFactory jf = new JMorphFactory();
	 * Analyser analyser = jf.build(new Definition[] { affixDef, dicDef });
	 * </pre>
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		AnalyserConfig ac = new AnalyserConfig();

		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Definition affixDef = ac.createDefinition(cl.getResource("hu_HU.aff"),
				encoding, SwordAffixReader.class);

		Definition dicDef = ac.createDefinition(cl.getResource("hu_HU.dic"),
				encoding, SwordReader.class);

		ac.setDerivativeFile(affixDef, cl.getResource("derivatives.lst")
				.getPath());
		ac.setRecursionDepth(affixDef, 2);
		ac.setCompoundFile(affixDef, cl.getResource("compound.lst").getPath());

		JMorphFactory jf = new JMorphFactory();
		Analyser analyser = jf.build(new Definition[] { affixDef, dicDef });

		new Morph(analyser).setVisible(true);
	}
}
