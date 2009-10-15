package net.sf.jhunlang.jmorph.app;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import net.sf.jhunlang.jmorph.analysis.AnalysisEntry;
import net.sf.jhunlang.jmorph.factory.Loader;
import net.sf.jhunlang.jmorph.synth.Synthetizer;

/**
 * Synth is an application providing a simple user interface
 * for morphological synthesis.
 */
public class Synth extends JFrame implements ActionListener
{
  /**
   * Input field for the morph to synthetize.
   */
  protected JComboBox morph;
  /**
   * Button to start synthetizing.
   */
  protected JButton synth;
  /**
   * List for the synthesis.
   */
  protected Synthesis synthesis;
  /**
   * The synthetizer.
   */
  protected Synthetizer synthetizer;

  /**
   * Create and initialize the main window for synthetizing with the
   * specified synthetizer.
   * @param synthetizer the synthetizer
   */
  public Synth(Synthetizer synthetizer)
  {
    super("Synthetizer");
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    this.synthetizer = synthetizer;

    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    // morph on NORTH
    morph = new JComboBox();
    morph.setEditable(true);
    
    contentPane.add(morph, BorderLayout.NORTH);

    // control on SOUTH
    Container control = new JPanel(new FlowLayout());
    contentPane.add(control, BorderLayout.SOUTH);

    synth = new JButton("Synthetize");
    synth.addActionListener(this);
    synth.setDefaultCapable(true);
    getRootPane().setDefaultButton(synth);
    control.add(synth);

    // synthesis and morphs on CENTER 
    JSplitPane centerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
    contentPane.add(centerPane, BorderLayout.CENTER);

    synthesis = new Synthesis();
    centerPane.setLeftComponent(synthesis.getComponent());

    JSplitPane morphPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
    centerPane.setRightComponent(morphPane);

    morphPane.setLeftComponent(
      createListPane("suffix", synthetizer.getSuffixMorph()));
    morphPane.setRightComponent(
      createListPane("prefix", synthetizer.getPrefixMorph()));

    pack();
  }

  protected Component createListPane(String title, Map morphMap)
  {
    // alphabetical order
    Set keySet = new TreeSet(String.CASE_INSENSITIVE_ORDER);
    Set derivatorSet = new TreeSet(String.CASE_INSENSITIVE_ORDER);
    Set inflexionSet = new TreeSet(String.CASE_INSENSITIVE_ORDER);

    Iterator it = morphMap.keySet().iterator();
    while (it.hasNext())
    {
      String morph = (String)it.next();
      StringTokenizer st = new StringTokenizer(morph);
      while (st.hasMoreTokens())
      {
        String s = st.nextToken();
        if (synthetizer.isDerivator(s))
        {
          derivatorSet.add(s);
        }
        else
        {
          inflexionSet.add(s);
        }
        keySet.add(s);
      }
    }

    JSplitPane listPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
    JSplitPane morphPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);

    listPane.setRightComponent(morphPane);

    DefaultListModel listModel = new DefaultListModel();
    JList list = new JList(listModel);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    list.addMouseListener(new MouseListener(listModel, list));
    
    JScrollPane scroll = new JScrollPane(list);
    Border border = new TitledBorder(
      new LineBorder(Color.gray), title + ": " + keySet.size());
    scroll.setBorder(border);

    listPane.setLeftComponent(scroll);

    DefaultListModel derivatorModel = new DefaultListModel();
    list = new JList(derivatorModel);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    list.addMouseListener(new MouseListener(derivatorModel, list));
    
    scroll = new JScrollPane(list);
    border = new TitledBorder(
      new LineBorder(Color.gray), "derivators: " + derivatorSet.size());
    scroll.setBorder(border);

    morphPane.setLeftComponent(scroll);

    DefaultListModel inflexionModel = new DefaultListModel();
    list = new JList(inflexionModel);
    list.addMouseListener(new MouseListener(inflexionModel, list));
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    scroll = new JScrollPane(list);
    border = new TitledBorder(
      new LineBorder(Color.gray), "inflexions: " + inflexionSet.size());
    scroll.setBorder(border);

    morphPane.setRightComponent(scroll);
    
    it = keySet.iterator();
    while (it.hasNext())
    {
      listModel.addElement(it.next());
    }
    
    it = derivatorSet.iterator();
    while (it.hasNext())
    {
      derivatorModel.addElement(it.next());
    }
    
    it = inflexionSet.iterator();
    while (it.hasNext())
    {
      inflexionModel.addElement(it.next());
    }

    return listPane;
  }

  private class MouseListener extends MouseAdapter
  {
    protected ListModel model;
    protected JList list;
    
    protected MouseListener(ListModel model, JList list)
    {
      this.model = model;
      this.list = list;
    }
    
    public void mouseClicked(MouseEvent e)
    {
      if (e.getClickCount() == 2)
      {
        int index = list.locationToIndex(e.getPoint());
        String s = model.getElementAt(index).toString();
        String m = (String)morph.getModel().getSelectedItem();
        m = m.trim() + ' ' + s;
        morph.setSelectedItem(m);        
      }
    }
  }
  
  /**
   * Synthetize the morph typed into the input field.
   * @param ev the event representing the button press
   */
  public void actionPerformed(ActionEvent ev)
  {
    String w = (String)morph.getSelectedItem();
    morph.insertItemAt(w, 0);
    synthesis.synthetize(w);
  }

  /**
   * Synthesis encapsulates the List for showing the synthesis of a morph.
   */
  private class Synthesis
  {
    /**
     * The model of the list
     */
    protected DefaultListModel synthModel;
    /**
     * The list of synthesis
     */
    protected JList synthList;
    /**
     * Scroll for the list of synthesis
     */
    protected JScrollPane scroll;

    /**
     * Create a new Synthesis
     */
    public Synthesis()
    {
      synthModel = new DefaultListModel();
      synthList = new JList(synthModel);
      scroll = new JScrollPane(synthList);
      Border border =
        new TitledBorder(new LineBorder(Color.gray), "Synthesis");
      scroll.setBorder(border);
    }

    public Component getComponent()
    {
      return scroll;
    }

    /**
     * Synthetize <code>morph</code>
     * @param morph the morph to synthetize
     */
    public void synthetize(String morph)
    {
      synthModel.clear();
      try
      {
        morph = morph.trim();

        int index;
        for(index = 0; index < morph.length(); index++)
        {
          if (Character.isWhitespace(morph.charAt(index)))
          {
            break;
          }
        }

        String word = morph.substring(0, index);
        morph = morph.substring(index).trim();

        Collection c = synthetizer.synthetize(word, morph);

        Iterator it = c.iterator();
        while (it.hasNext())
        {
          Object o = it.next();
          AnalysisEntry stem = (AnalysisEntry)o;
          synthModel.addElement(stem.getInflexedWord() + ", " + 
            stem.getRelativeRootWord() + "(" + stem.getAbsoluteRootWord()+ "), " +
            stem.getPOS() + ", " + stem.getCaseEnding() +
            ":\n    " + stem);
        }
      }
      catch (Throwable t)
      {
        t.printStackTrace();
        synthModel.addElement("Internal Error: "  + t);
      }
    }
  }

  /**
   * Starts the application.<p>
   * The command line parameter specifies the definition file.
   * Read and parse the definition file, build Rules and Dictionaries and
   * create a Synhtetizer. Create and show a Synth.<p>
   * Errors are printed to the standard error.
   */
  public static void main(String[] args)
    throws Exception
  {
    Loader loader = new Loader();
    loader.load(args);
    
    Synthetizer synth = new Synthetizer(loader.getRules(), loader.getDic());
    new Synth(synth).setVisible(true);
  }
}