package net.sf.jhunlang.jmorph.app;

import java.util.Iterator;
import java.util.Map;

import java.io.IOException;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.sf.jhunlang.jmorph.synth.Generator;

import net.sf.jhunlang.jmorph.util.app.AffixViewer;
import net.sf.jhunlang.jmorph.util.app.WordViewer;

import net.sf.jhunlang.jmorph.analysis.Analysis;
import net.sf.jhunlang.jmorph.factory.Loader;

import net.sf.jhunlang.jmorph.parser.ParseException;

/**
 * Generator is an application providing a simple user interface for derivation.
 */
public class Gen extends JFrame implements ActionListener
{
  /**
   * Input field for the word to be derived.
   */
  protected JTextField word;
  /**
   * Button to start generate.
   */
  protected JButton generate;
  /**
   * The model of the list of derived words
   */
  protected DefaultListModel genModel;
  /**
   * The list of derived words
   */
  protected JList genList;
  /**
   * Scroll for deriveList
   */
  protected JScrollPane genScroll;
  /**
   * Frame for showing dictionary words.
   */
  protected WordViewer words;
  /**
   * Frame for showing affixes.
   */
  protected AffixViewer affixes;
  /**
   * The generator
   */
  protected Generator generator;

  public Gen()
  {}
  
  public Gen(String title)
  {
    super(title);
  }
  
  /**
   * Configure the main window for generating with the
   * specified <code>generator</code>.
   * @param generator the generator
   */
  void configure(Generator generator)
  {
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    this.generator = generator;

    Container c = getContentPane();
    c.setLayout(new BorderLayout());

    // word to generate
    word = new JTextField(32);
    // derivations
    genModel = new DefaultListModel();
    genList = new JList(genModel);
    genScroll = new JScrollPane(genList);

    c.add(word, BorderLayout.NORTH);
    c.add(genScroll, BorderLayout.CENTER);

    // buttons for generate, words and affixes
    Container control = new JPanel(new FlowLayout());
    c.add(control, BorderLayout.SOUTH);

    generate = new JButton("Generate");
    control.add(generate);

    generate.addActionListener(this);
    generate.setDefaultCapable(true);
    getRootPane().setDefaultButton(generate);

    JButton w = new JButton("Words");
    control.add(w);

    w.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent ev)
      {
        getWords().setVisible(true);
      }
    });

    w = new JButton("Generators");
    control.add(w);

    w.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent ev)
      {
        getAffixes().showup();
      }
    });
    pack();
  }

  /**
   * Perform stemming of the word typed into the input field with
   * the selected controlling parameters. Called by java whenever the
   * Analysis button is pressed.
   * @param ev the event representing the button press
   */
  public void actionPerformed(ActionEvent ev)
  {
    String w = word.getText();
    genModel.clear();

    Map generations = generator.generate(w);
    if (generations == null)
    {
      JOptionPane.showMessageDialog(this,
        "There is no such dictionary word \"" + w + "\".",
        "No dictionary word found", JOptionPane.INFORMATION_MESSAGE);
      return;
    }

    genModel.addElement(w + " has " + generations.size() + " generations");

    Iterator it = generations.entrySet().iterator();
    while (it.hasNext())
    {
      Map.Entry m = (Map.Entry)it.next();
      Map c = (Map)m.getValue();
      
      genModel.addElement(m.getKey() + " <== " + c.size());
      
      Iterator cit = c.values().iterator();
      while (cit.hasNext())
      {
        Analysis o = (Analysis)cit.next();
        genModel.addElement("  " + o);
      }
    }
  }

  /**
   * Return viewer for {@link Generator#getDictionaries()}.
   * @return the viewer for {@link Generator#getDictionaries()}
   */
  protected WordViewer getWords()
  {
    if (words == null)
    {
      words = new WordViewer(generator.getDictionaries());
    }
    return words;
  }

  /**
   * Return viewer for {@link Generator#getRules()}.
   * @return the viewer for {@link Generator#getRules()}
   */
  protected AffixViewer getAffixes()
  {
    if (affixes == null)
    {
      affixes = new AffixViewer(
        "Generator affixes", null, generator.getSuffixGenerators().values());
    }
    return affixes;
  }

  public static Generator load(String[] args, int type)
    throws IOException, ParseException 
  {
    Loader loader = new Loader();
    loader.load(args);
    return new Generator(loader.getRules(), loader.getDic(), type);
  }
  
  /**
   * Starts the application.<p>
   * The command line parameter specifies the definition file.
   * Read and parse the definition file, build Rules and Dictionaries and
   * create a Generator. Create and show a Gen.<p>
   * Errors are printed to the standard error.
   */
  public static void main(String[] args)
    throws Exception
  {
    Gen gen = new Gen("Generator"); 
    gen.configure(load(args, Generator.ALL));
    gen.setVisible(true);
  }
}