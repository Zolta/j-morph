package net.sf.jhunlang.jmorph.factory;

import java.util.List;
import java.util.LinkedList;

import java.io.IOException;
import java.io.File;

import java.net.MalformedURLException;
import java.net.URL;

import net.sf.jhunlang.jmorph.parser.AbstractReader;
import net.sf.jhunlang.jmorph.parser.ParseException;

public class Definition
{
  // url where this definition has been read from
  protected URL baseUrl;
  // the url this definition should read from
  protected URL url;
  // the encoding of url
  protected String encoding;
  // reader url should be read with
  protected AbstractReader reader;

  protected List extension = new LinkedList();

  public Definition(URL url, String encoding, AbstractReader reader)
  {
    this.url = url;
    this.encoding = encoding;
    this.reader = reader;
    reader.setDefinition(this);
  }
  
  public Definition(
    URL baseUrl, URL url, String encoding, AbstractReader reader)
  {
    this(url, encoding, reader);
    this.baseUrl = baseUrl;
  }
  

  public URL getBaseURL()
  {
    return baseUrl;
  }

  public URL getURL()
  {
    return url;
  }
  
  public URL getFileURL(String file)
    throws MalformedURLException
  {
    return baseUrl == null ? new File(file).toURL() : new URL(baseUrl, file);
  }
  
  public AbstractReader getReader()
  {
    return reader;
  }

  public void addExtensionLine(String line)
  {
    extension.add(line);
  }

  public List getExtensionLines()
  {
    return extension;
  }

  public Object read()
    throws IOException, ParseException
  {
    return reader.read(url, encoding);
  }

  public String toString()
  {
    return "Definition[" + url + ", " + encoding + ", " +
      reader.getClass() + "]";
  }
}