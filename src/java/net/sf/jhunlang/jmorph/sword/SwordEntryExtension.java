package net.sf.jhunlang.jmorph.sword;

import java.util.List;

public class SwordEntryExtension extends SwordExtension
{
  protected POSName pos = POSName.empty;

  public SwordEntryExtension()
  {
    super();
  }

  public SwordEntryExtension(List cases, POSName pos)
  {
    this(cases);
    this.pos = pos;
  }

  public SwordEntryExtension(SwordExtension ext)
  {
    super(ext);
    pos = ext.getPOSName();
  }

  public void setPos(POSName pos)
  {
    this.pos = pos;
  }
  
  protected SwordEntryExtension(List cases)
  {
    super(cases);
  }
  
  public POSName getPOSName()
  {
    return pos == null ? super.getPOSName() : pos;
  }

  public void setPOSName(POSName pos)
  {
    this.pos = pos;
  }
  
  public String toString()
  {
    return getPOSName() + super.toString();
  }
}