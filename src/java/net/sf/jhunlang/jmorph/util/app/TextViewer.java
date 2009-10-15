package net.sf.jhunlang.jmorph.util.app;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class TextViewer extends JFrame
{
  protected JList list;
  protected DefaultListModel listModel;
  protected JScrollPane listScroll;

  protected JTextField lineField;

  protected boolean go;
  protected JButton goButton;
  
  protected boolean search;
  protected Searcher searchPane;  

  public TextViewer()
  {
    this(true, true);
  }

  public TextViewer(Collection c)
  {
    this(c, true, true);
  }

  public TextViewer(Collection c, boolean go, boolean search)
  {
    this(go, search);
    Iterator it = c.iterator();
    while (it.hasNext())
    {
      add((String)it.next());
    }
  }

  public TextViewer(boolean go, boolean search)
  {
    this.go = go;
    this.search= search;

    Container c = getContentPane();
    c.setLayout(new BorderLayout());

    Container main = new JPanel(new BorderLayout());

    c.add(main, BorderLayout.CENTER);

    listModel = new DefaultListModel();
    list = new JList(listModel);
    listScroll = new JScrollPane(list);

    main.add(listScroll, BorderLayout.CENTER);

    if (go || search)
    {
      Container control = new JPanel(new FlowLayout());
      c.add(control, BorderLayout.SOUTH);

      if (go)
      {
        lineField = new JTextField(16);
        goButton = new JButton("goto");
        goButton.setDefaultCapable(true);
        getRootPane().setDefaultButton(goButton);

        lineField.addCaretListener(new CaretListener()
        {
          public void caretUpdate(CaretEvent ce)
          {
            getRootPane().setDefaultButton(goButton);
          }
        });

        control.add(lineField);
        control.add(goButton);

        goButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ev)
          {
            int lineNumber = 0;
            try
            {
              lineNumber = Math.min(
                Integer.parseInt(lineField.getText()), listModel.getSize());
              lineNumber = Math.max(0, lineNumber - 1);
              list.setSelectedIndex(lineNumber);
              list.ensureIndexIsVisible(lineNumber);
            }
            catch (Throwable t)
            {
              System.out.println(lineField.getText() + " is not number");
            }
          }
        });
      }

      if (search)
      {
        searchPane = new Searcher();
        searchPane.setList(list);
        
        searchPane.getSearchField().addCaretListener(new CaretListener()
        {
          public void caretUpdate(CaretEvent ce)
          {
            getRootPane().setDefaultButton(searchPane.getFind());
          }
        });

        if (!go)
        {
          getRootPane().setDefaultButton(searchPane.getFind());
        }

        control.add(searchPane);
      }
    }
  }

  public void add(String line)
  {
    listModel.addElement(line);
  }

  public static void main(String[] args)
    throws Exception
  {
    if (args.length < 2)
    {
      System.out.println("TextViewer file encoding");
      System.exit(2);
    }

    BufferedReader r = new BufferedReader(new InputStreamReader(
      new FileInputStream(args[0]), args[1]));

    Collection l = new LinkedList();
    String line;
    while ((line = r.readLine()) != null)
    {
      l.add(line);
    }
    r.close();

    TextViewer tr = new TextViewer(l);
    tr.setDefaultCloseOperation(EXIT_ON_CLOSE);

    tr.setTitle(new File(args[0]).getAbsolutePath() + " - " + l.size());

    tr.pack();
    tr.setVisible(true);
  }
}