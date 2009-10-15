package net.sf.jhunlang.jmorph.util.app;

import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.LinkedList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.jhunlang.jmorph.Affix;
import net.sf.jhunlang.jmorph.AffixEntry;
import net.sf.jhunlang.jmorph.AffixEntryExtension;
import net.sf.jhunlang.jmorph.Rules;
import net.sf.jhunlang.jmorph.factory.Loader;

/**
 * AffixViewer is an application showing affix rules.
 */
public class AffixViewer extends JFrame implements ListSelectionListener
{
  /**
   * The morph viewer for suffixes
   */
  protected Component suffixMorph;
  /**
   * The morph viewer for prefixes
   */
  protected Component prefixMorph;
  /**
   * The viewer for suffixes
   */
  protected Component suffixes;
  /**
   * The viewer for prefixes
   */
  protected Component prefixes;

  protected Searcher searchPane;
  
  public AffixViewer(String title, Rules rules)
  {
    this(title, rules, false);
  }
  /**
   * Create an <code>AffixViewer<code> for the affix rules of
   * <code>rules</code>.
   * @param rules the rules to show
   */
  public AffixViewer(String title, Rules rules, boolean exitOnClose)
  {
    this(title, rules.getPrefixes(), rules.getSuffixes(), exitOnClose);
  }
  
  public AffixViewer(String title, Collection prefix, Collection suffix)
  {
    this(title, prefix, suffix, false);    
  }

  public AffixViewer(
    String title, Collection prefix, Collection suffix, boolean exitOnClose)
  {
    super(title);
    setDefaultCloseOperation(exitOnClose ? EXIT_ON_CLOSE : HIDE_ON_CLOSE);
    
    prefixes = createViewer("Prefixes", prefix);
    suffixes = createViewer("Suffixes", suffix);
    prefixMorph = createMorphViewer("Prefix morph", prefix);
    suffixMorph = createMorphViewer("Suffix morph", suffix);
    
    JSplitPane listPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
    JSplitPane morphPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
    
    listPane.setLeftComponent(prefixes);
    listPane.setRightComponent(suffixes);
    
    morphPane.setLeftComponent(prefixMorph);
    morphPane.setRightComponent(suffixMorph);
    
    JSplitPane mainPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
    mainPane.setLeftComponent(listPane);
    mainPane.setRightComponent(morphPane);
    
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());
    
    contentPane.add(mainPane, BorderLayout.CENTER);
    
    searchPane = new Searcher();
    contentPane.add(searchPane, BorderLayout.SOUTH);
    
    pack();
  }

  /**
   * Create viewer for <code>affixes</code> with <code>title</code>
   * @param title the title of the viewer 
   * @param affixes the {@link Affix} instances to show in the viewer
   * @return the viewer
   */
  protected Component createViewer(String title, Collection affixes)
  {
    DefaultListModel listModel = new DefaultListModel();
    JList list = new JList(listModel);
    
    list.addListSelectionListener(this);
    
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane scroll = new JScrollPane(list);
    
    int numEntries = 0;
    int size = 0;
    if (affixes != null)
    {
      size = affixes.size();
      Iterator it = affixes.iterator();
      while (it.hasNext())
      {
        Object o = it.next();
        if (o instanceof Affix)
        {
          Affix affix = (Affix)o;
          listModel.addElement(affix.toString());
          AffixEntry[] entries = affix.getEntries();
          for(int i = 0; i < entries.length; i++)
          {
            if (entries[i] != null)
            {
              listModel.addElement("  " + entries[i].toString());
            }
          }
          numEntries += entries.length;
        }
        else if (o instanceof Collection)
        {
          Iterator eit = ((Collection)o).iterator();
          while (eit.hasNext())
          {
            listModel.addElement("  " + eit.next());
            numEntries++;
          }
        }
      }
    }
    title = title + "(" + size + ", " + numEntries + ")";
    TitledBorder border = new TitledBorder(new LineBorder(Color.gray), title);
    scroll.setBorder(border);     
    return scroll;
  }

  /**
   * Create viewer showing <code>affixes</code> by their morphological
   * description.
   * @param title the title of the viewer 
   * @param affixes the {@link Affix} instances to show in the viewer
   * @return the viewer
   */
  protected Component createMorphViewer(String title, Collection affixes)
  {
    Map morphMap = new TreeMap();
    if (affixes != null)
    {
      Iterator it = affixes.iterator();
      while (it.hasNext())
      {
        Object o = it.next();
        if (o instanceof Affix)
        {
          Affix affix = (Affix)o;
          AffixEntry[] entries = affix.getEntries();
          for(int i = 0; i < entries.length; i++)
          {
            if (entries[i] != null)
            {
              morph(morphMap, entries[i]);
            }
          }
        }
        else if (o instanceof Collection)
        {
          Iterator eit = ((Collection)o).iterator();
          while (eit.hasNext())
          {
            morph(morphMap, (AffixEntry)eit.next());
          }
        }
      }
    }
    
    DefaultListModel listModel = new DefaultListModel();
    JList list = new JList(listModel);
    list.addListSelectionListener(this);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane scroll = new JScrollPane(list);
    TitledBorder border = new TitledBorder(
      new LineBorder(Color.gray), title + "(" + morphMap.size() + ")");
    scroll.setBorder(border);
       
    Iterator it = morphMap.entrySet().iterator();
    while (it.hasNext())
    {
      Map.Entry entry = (Map.Entry)it.next();
      Collection c = (Collection)entry.getValue();
      listModel.addElement(entry.getKey() + ": " + c.size());
    }

    return scroll;
  }
  
  protected void morph(Map morphMap, AffixEntry entry)
  {
    AffixEntryExtension extension = entry.getExtension();
    String extensionString =
      extension == null ? "" : extension.morphString();
    Collection c = (Collection)morphMap.get(extensionString);
    if (c == null)
    {
      c = new LinkedList();
      morphMap.put(extensionString, c);
    }
    c.add(entry.toString());
  }

  /**
   * Show all four viewers.
   */
  public void showup()
  {
    setVisible(true);
  }
  
  public void valueChanged(ListSelectionEvent e)
  {
    searchPane.setList((JList)e.getSource());
  }

  /**
   * Starts the application.<p>
   * The command line parameter specifies the definition
   * file for the affix file.
   */
  public static void main(String[] args)
    throws Exception
  {
    Loader loader = new Loader();
    loader.loadRules(args);
    new AffixViewer("Affixes", loader.getRules().getSubRules(), true).showup();
  }
}