package net.sf.jhunlang.jmorph.cl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import net.sf.jhunlang.jmorph.factory.Loader;

public abstract class Cl extends Loader
{    
  /**
   * Property name for setting the character encoding used for reading and
   * writing the standard input and output respectively. 
   */
  public final static String ENCODING = "encoding";
  /**
   * Define ISO-8859-2 as the default character encoding used for reading
   * and writing the standard input and output respectively. 
   */
  public final static String DEFAULT_ENCODING = "ISO-8859-2";
  /**
   * Property name for the input file. 
   */
  public final static String INPUT = "input";
  /**
   * Property name for the output file. 
   */
  public final static String OUTPUT = "output";
  
  /**
   * @return return the {@link #DEFAULT_ENCODING}.
   */
  public static String getDefaultEcoding()
  {
    return DEFAULT_ENCODING;
  }
  
  /**
   * The character encoding used for reading and writing the standard
   * input and output respectively.
   */
  protected String encoding = System.getProperty(ENCODING, DEFAULT_ENCODING);
  
  protected int count;
  /**
   * Read words from <code>reader</code>, one per line and call process them
   * by calling {@link #process(String, PrintWriter)}. 
   * @param reader the reader to read lines from 
   * @param p the print writer to pass to {@link #process(String, PrintWriter)}
   * @throws IOException
   */
  public void run(BufferedReader reader, PrintWriter p)
    throws IOException
  {
    String line;
    while((line = reader.readLine()) != null)
    {
      line = line.trim();
      if (line.length() > 0)
      {
        count++;
        process(line, p);
      }
    }
  }
  
  /**
   * Create reader and writer for {@link #run(BufferedReader, PrintWriter)}.
   * Reader reads the standard input unless the system property {@link #INPUT}
   * specifies the file to read from. Writer writes to the standard output
   * unless the system property {@link #OUTPUT} specifies the file to write to.
   * Both reader and writer use the character encoding specified by the system
   * property {@link #ENCODING}. The default is {@link #DEFAULT_ENCODING}. 
   * @throws IOException
   * */
  public void run()
    throws IOException
  {
    String input = System.getProperty(INPUT);
    String output = System.getProperty(OUTPUT);

    BufferedReader reader;
    PrintWriter writer;
    
    if (input == null)
    {
      reader = new BufferedReader(new InputStreamReader(System.in, encoding)); 
    }
    else
    {
      reader = new BufferedReader(
        new InputStreamReader(new FileInputStream(input), encoding));
    }
    
    if (output == null)
    {
      writer =
        new PrintWriter(new OutputStreamWriter(System.out, encoding), true); 
    }
    else
    {
      writer = new PrintWriter(
        new OutputStreamWriter(new FileOutputStream(output), encoding), true);       
    }
long start = System.currentTimeMillis();    
    run(reader, writer);
long now = System.currentTimeMillis();
//System.out.println(
//    "analysing " + count + " words took " + (now - start) + " millis");
    
    reader.close();
    writer.close();
  }
  
  /**
   * @return return the character encoding.
   */
  public String getEncoding()
  {
    return encoding;
  }
  
  /**
   * Process <code>line</code> and write the result to <code>p</code>.
   * @param line the line to process
   * @param p the writer to the result to
   */
  protected abstract void process(String line, PrintWriter p)
    throws IOException;
}
