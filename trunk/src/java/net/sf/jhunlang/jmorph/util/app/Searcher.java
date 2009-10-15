package net.sf.jhunlang.jmorph.util.app;

import java.util.Iterator;
import java.util.LinkedList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import java.awt.FlowLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class Searcher extends JPanel
{
  protected JCheckBox regexp;
  protected JTextField searchField;

  protected JButton find;
  protected JButton next;
  
  protected JList list;
  protected ListModel listModel;
  
  protected LinkedList nextIndex;
  protected Iterator nextIterator;

  public Searcher()
  {
    super(new FlowLayout());
    
    regexp = new JCheckBox("Regexp");
    searchField = new JTextField(16);    
    find = new JButton("find");    
    next = new JButton("next");
  
    add(regexp);
    add(searchField);
    add(find);
    add(next);
      
    find.setDefaultCapable(true);
    next.setDefaultCapable(true);
    
    searchField.addCaretListener(new CaretListener()
    {
      public void caretUpdate(CaretEvent ce)
      {
        getRootPane().setDefaultButton(find);
      }
    });
      
    find.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent ev)
      {        
        if (list == null)
        {
          return;
        }
        getRootPane().setDefaultButton(next);
        int index = Math.max(0, list.getSelectedIndex());
        try
        {
          search(index);
        }
        catch (PatternSyntaxException psx)
        {
          JOptionPane.showMessageDialog(Searcher.this, psx.getMessage(),
            "Pattern error", JOptionPane.INFORMATION_MESSAGE);
        }
      }
    });
  
    next.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent ev)
      {        
        getRootPane().setDefaultButton(next);
        next();
      }
    });
  }
  
  public void setList(JList list)
  {
    if (this.list != list)
    {
      this.list = list;
      listModel = list.getModel();
      nextIndex = null;
      nextIterator = null;
    }
  }
  
  public void search(int index)
  {
    if (list == null)
    {
      return;
    }
    
    String text = searchField.getText();
    boolean reg = regexp.isSelected();
    
    Pattern pattern = reg ? Pattern.compile(text) : null;
    nextIndex = new LinkedList(); 
    
    for(int i = 0; i < listModel.getSize(); i++)
    {
      String s = (String)listModel.getElementAt(i);
      if (reg)
      {
        Matcher m = pattern.matcher(s);
        if (m.find())
        {
          nextIndex.add(new Integer(i));
        }
      }
      else if (s.indexOf(text) != - 1)
      {
        nextIndex.add(new Integer(i));
      }
    }
    nextIterator = nextIndex.iterator();
    next();
  }
  
  public void next()
  {
    if (nextIterator != null)
    {
      if (!nextIterator.hasNext())
      {
        nextIterator = nextIndex.iterator();
      }
      if (nextIterator.hasNext())
      {
        int index = ((Integer)nextIterator.next()).intValue();
        list.setSelectedIndex(index);
        list.ensureIndexIsVisible(index);
      }
    }
  }
  /**
   * @return return the find.
   */
  public JButton getFind()
  {
    return find;
  }
  /**
   * @return return the list.
   */
  public JList getList()
  {
    return list;
  }
  /**
   * @return return the listModel.
   */
  public ListModel getListModel()
  {
    return listModel;
  }
  /**
   * @return return the next.
   */
  public JButton getNext()
  {
    return next;
  }
  /**
   * @return return the regexp.
   */
  public JCheckBox getRegexp()
  {
    return regexp;
  }
  /**
   * @return return the searchField.
   */
  public JTextField getSearchField()
  {
    return searchField;
  }
}
