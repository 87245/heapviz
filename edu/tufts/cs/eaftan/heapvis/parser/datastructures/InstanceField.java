package edu.tufts.cs.eaftan.heapvis.parser.datastructures;

public class InstanceField {

  public long fieldNameStringId;
  public Type type;

  public InstanceField(long fieldNameStringId, Type type) {
    this.fieldNameStringId = fieldNameStringId;
    this.type = type;
  }

}
