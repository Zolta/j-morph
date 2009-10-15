package net.sf.jhunlang.jmorph.app;

import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.LinkedHashSet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.DefaultComboBoxModel;
import javax.swing.MutableComboBoxModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import net.sf.jhunlang.jmorph.analysis.AnalyserImpl;
import net.sf.jhunlang.jmorph.analysis.CompoundControl;
import net.sf.jhunlang.jmorph.analysis.Analysis;
import net.sf.jhunlang.jmorph.analysis.IStem;
import net.sf.jhunlang.jmorph.analysis.AnalyserContext;
import net.sf.jhunlang.jmorph.analysis.AnalyserControl;
import net.sf.jhunlang.jmorph.analysis.Analyser;
import net.sf.jhunlang.jmorph.analysis.consumer.X;

import net.sf.jhunlang.jmorph.factory.Loader;

import net.sf.jhunlang.jmorph.util.BoundedHashtable;
import net.sf.jhunlang.jmorph.util.app.WordViewer;

/**
 * Morph is an application providing a simple user interface for stemming.
 */
public class Morph extends JFrame implements ActionListener
{
  /**
   * Input field for the word to be stemmed.
   */
  protected JComboBox word;
  /**
   * The model of the input box.
   */
  protected MutableComboBoxModel wordModel;
  /**
   * The set of input words already analysed
   */
  protected Set wordSet = new LinkedHashSet();
  /**
   * Button to start stemming.
   */
  protected JButton stem;
  /**
   * List for the stems.
   */
  protected Analyses stems;
  /**
   * Combobox for selecting the depth of analysis.
   */
  protected JComboBox depth;
  /**
   * Combobox for selecting x for identifying affixes for the analyser.
   */
  protected JComboBox x;
  /**
   * Spin for setting the minimum length of component words.
   */
  protected JSpinner min;
  /**
   * Spin for setting the maximum number of component words.
   */
  protected JSpinner max;
  /**
   * If call istem on stemmer.
   */
  protected JCheckBox istem;
  /**
   * The stemmer to use for stemming.
   */
  protected Analyser analyser;
  /**
   * Frame for showing dictionary words.
   */
  protected WordViewer words;
  /**
   * The control used for stemming.
   */
  protected AnalyserControl control;
  /**
   * The stemming context.
   */
  protected AnalyserContext context;
  
  protected BoundedHashtable cache = new BoundedHashtable(3, 2);

  protected int pu;
  protected int pminWord;
  protected int pmaxWord;
  protected X px;
  
  /**
   * Create and initialize the main window for analysing with the
   * specified analyser.
   * @param analyser the analyser
   */
  public Morph(Analyser analyser)
  {
    super("Morph");
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    this.analyser = analyser;

    Container c = getContentPane();
    c.setLayout(new BorderLayout());

    Container northPanel = new JPanel(new BorderLayout());
    c.add(northPanel, BorderLayout.NORTH);
    wordModel = new DefaultComboBoxModel();
    word = new JComboBox(wordModel);
    word.setEditable(true);
    
    northPanel.add(word, BorderLayout.NORTH);
    
    Container stemControl = new JPanel(new GridLayout(1, 5));
    northPanel.add(stemControl, BorderLayout.SOUTH);

    depth = new  JComboBox(AnalyserControl.DEPTHS);
    depth.setSelectedIndex(AnalyserControl.DEFAULT_DEPTH);

    x = new  JComboBox(AnalyserControl.XES);
    x.setSelectedIndex(0);

    stemControl.add(depth);
    stemControl.add(x);

    istem = new JCheckBox("istem");
    stemControl.add(istem);

    min = new JSpinner(new SpinnerNumberModel(
        new Integer(CompoundControl.DEFAULT_MIN), new Integer(2),
        new Integer(CompoundControl.MAX_MIN), new Integer(1)));

    stemControl.add(min);

    max = new JSpinner(new SpinnerNumberModel(
        new Integer(CompoundControl.DEFAULT_MAX), new Integer(2),
        new Integer(CompoundControl.MAX_MAX), new Integer(1)));

    stemControl.add(max);

    Container control = new JPanel(new FlowLayout());
    c.add(control, BorderLayout.SOUTH);

    stem = new JButton("Morph");
    control.add(stem);

    stem.addActionListener(this);
    stem.setDefaultCapable(true);

    stem.getRootPane().setDefaultButton(stem);

    JButton w = new JButton("Words");
    control.add(w);

    w.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent ev)
      {
        getWords().setVisible(true);
      }
    });

    Container stemc = new JPanel(new GridLayout(1, 1));
    stems = new Analyses(stemc);
    c.add(stemc, BorderLayout.CENTER);
    pack();
  }

  protected WordViewer getWords()
  {
    if (words == null)
    {
      words = new WordViewer(analyser.getDictionaries());
    }
    return words;
  }

  /**
   * Perform stemming of the word typed into the input field with
   * the selected controlling parameters. Called by java whenever the
   * Morph button is pressed.
   * @param ev the event representing the button press
   */
  public void actionPerformed(ActionEvent ev)
  {
    String w = (String)word.getSelectedItem();
    
    if (!wordSet.add(w))
    {
      wordModel.removeElement(w);
    }
    
    wordModel.insertElementAt(w, 0);
    word.setSelectedIndex(0);
    
    int u = depth.getSelectedIndex();
    int minWord = ((Integer)min.getValue()).intValue();
    int maxWord = ((Integer)max.getValue()).intValue();
    X xx = AnalyserControl.XES[x.getSelectedIndex()];
    
    if (u != pu || minWord != pminWord || maxWord != pmaxWord || xx != px)
    {
      pu = u;
      px = xx;      
      pminWord = minWord;
      pmaxWord = maxWord;
      control =
        new AnalyserControl(new CompoundControl(minWord, maxWord), u, xx);
      context = new AnalyserContext(control);
      cache.clear();
    }
    stems.stem(w, context);
  }

  /**
   * Analyses encapsulates the List for showing the analyses of a word.
   */
  private class Analyses
  {
    /**
     * The model of the list
     */
    protected DefaultListModel stemsModel;
    /**
     * The list of stems
     */
    protected JList stemList;
    /**
     * Scroll for the list of stems
     */
    protected JScrollPane scroll;

    /**
     * Create a new Morph instance and it to the given container
     * @param stems the container
     */
    public Analyses(Container stems)
    {
      stemsModel = new DefaultListModel();
      stemList = new JList(stemsModel);
      scroll = new JScrollPane(stemList);

      Border border = new LineBorder(Color.gray);
      scroll.setBorder(border);
      stems.add(scroll);
    }

    /**
     * Analyses the given word with the givne control and show the resulting
     * stems.
     * @param word the word to stem
     * @param context the stemming context
     */
    public void stem(String word, AnalyserContext context)
    {
      stemsModel.clear();
      if (istem.isSelected())
      {
        IStem istem = analyser.istem(word, 0);
        net.sf.jhunlang.jmorph.analysis.Analysis analysis = istem.getAnalysis();
        if (stem != null)
        {
          stemsModel.addElement(istem.getType() + " " +
            analysis.getRelativeRootWord() + ", " +
            analysis.getAbsoluteRootWord() + "(" + analysis + "), " +
            analysis.getPOS() + ", " + analysis.getCaseEnding());
        }
      }
      else
      {
        List analyses = (List)cache.get(word);
        if (analyses == null)
        {
          analyses = analyser.analyse(word, context);
          //cache.put(word, analyses);
        }
        Iterator it = analyses.iterator();
        while (it.hasNext())
        {
          Analysis analysis = (Analysis)it.next();
          stemsModel.addElement(
            analysis.getRelativeRootWord() + "(" +
            analysis.getDictionaryRootWord() + ", " +
            analysis.getAbsoluteRootWord()+ "), " +
            analysis.getPOS() + ", " +
            analysis.getCaseEnding() + ": " +
            (Boolean.getBoolean("long") ?
              analysis.toLongString() : analysis.toString()));
        }
      }
    }
  }

  /**
   * Starts the application.<p>
   * The command line parameter specifies the definition file.
   * Read and parse the definition file, build Rules and Dictionaries,
   * create Analyser and create and show a Morph.<p>
   * Errors are printed to the standard error.
   */
  public static void main(String[] args)
    throws Exception
  {
    Loader loader = new Loader();
    loader.load(args);
    Analyser analyser = new AnalyserImpl(loader.getRules(), loader.getDic());
    new Morph(analyser).setVisible(true);
  }
}