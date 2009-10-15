package net.sf.jhunlang.jmorph;

import java.io.Serializable;

public interface DictEntryExtension extends Serializable
{
  String getCaseEnding();
  String getCase();
  String getPOS();
  String morphString();
  String inflexionString();
  String derivatorString();
  boolean isDerivator();
  boolean isInflexion();
}