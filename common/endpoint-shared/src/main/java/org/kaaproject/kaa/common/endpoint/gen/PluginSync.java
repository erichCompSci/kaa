/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package org.kaaproject.kaa.common.endpoint.gen;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class PluginSync extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"PluginSync\",\"namespace\":\"org.kaaproject.kaa.common.endpoint.gen\",\"fields\":[{\"name\":\"pluginId\",\"type\":\"int\"},{\"name\":\"payload\",\"type\":\"bytes\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
   private int pluginId;
   private java.nio.ByteBuffer payload;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use {@link \#newBuilder()}. 
   */
  public PluginSync() {}

  /**
   * All-args constructor.
   */
  public PluginSync(java.lang.Integer pluginId, java.nio.ByteBuffer payload) {
    this.pluginId = pluginId;
    this.payload = payload;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return pluginId;
    case 1: return payload;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: pluginId = (java.lang.Integer)value$; break;
    case 1: payload = (java.nio.ByteBuffer)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'pluginId' field.
   */
  public java.lang.Integer getPluginId() {
    return pluginId;
  }

  /**
   * Sets the value of the 'pluginId' field.
   * @param value the value to set.
   */
  public void setPluginId(java.lang.Integer value) {
    this.pluginId = value;
  }

  /**
   * Gets the value of the 'payload' field.
   */
  public java.nio.ByteBuffer getPayload() {
    return payload;
  }

  /**
   * Sets the value of the 'payload' field.
   * @param value the value to set.
   */
  public void setPayload(java.nio.ByteBuffer value) {
    this.payload = value;
  }

  /** Creates a new PluginSync RecordBuilder */
  public static org.kaaproject.kaa.common.endpoint.gen.PluginSync.Builder newBuilder() {
    return new org.kaaproject.kaa.common.endpoint.gen.PluginSync.Builder();
  }
  
  /** Creates a new PluginSync RecordBuilder by copying an existing Builder */
  public static org.kaaproject.kaa.common.endpoint.gen.PluginSync.Builder newBuilder(org.kaaproject.kaa.common.endpoint.gen.PluginSync.Builder other) {
    return new org.kaaproject.kaa.common.endpoint.gen.PluginSync.Builder(other);
  }
  
  /** Creates a new PluginSync RecordBuilder by copying an existing PluginSync instance */
  public static org.kaaproject.kaa.common.endpoint.gen.PluginSync.Builder newBuilder(org.kaaproject.kaa.common.endpoint.gen.PluginSync other) {
    return new org.kaaproject.kaa.common.endpoint.gen.PluginSync.Builder(other);
  }
  
  /**
   * RecordBuilder for PluginSync instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<PluginSync>
    implements org.apache.avro.data.RecordBuilder<PluginSync> {

    private int pluginId;
    private java.nio.ByteBuffer payload;

    /** Creates a new Builder */
    private Builder() {
      super(org.kaaproject.kaa.common.endpoint.gen.PluginSync.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(org.kaaproject.kaa.common.endpoint.gen.PluginSync.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.pluginId)) {
        this.pluginId = data().deepCopy(fields()[0].schema(), other.pluginId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.payload)) {
        this.payload = data().deepCopy(fields()[1].schema(), other.payload);
        fieldSetFlags()[1] = true;
      }
    }
    
    /** Creates a Builder by copying an existing PluginSync instance */
    private Builder(org.kaaproject.kaa.common.endpoint.gen.PluginSync other) {
            super(org.kaaproject.kaa.common.endpoint.gen.PluginSync.SCHEMA$);
      if (isValidValue(fields()[0], other.pluginId)) {
        this.pluginId = data().deepCopy(fields()[0].schema(), other.pluginId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.payload)) {
        this.payload = data().deepCopy(fields()[1].schema(), other.payload);
        fieldSetFlags()[1] = true;
      }
    }

    /** Gets the value of the 'pluginId' field */
    public java.lang.Integer getPluginId() {
      return pluginId;
    }
    
    /** Sets the value of the 'pluginId' field */
    public org.kaaproject.kaa.common.endpoint.gen.PluginSync.Builder setPluginId(int value) {
      validate(fields()[0], value);
      this.pluginId = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'pluginId' field has been set */
    public boolean hasPluginId() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'pluginId' field */
    public org.kaaproject.kaa.common.endpoint.gen.PluginSync.Builder clearPluginId() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'payload' field */
    public java.nio.ByteBuffer getPayload() {
      return payload;
    }
    
    /** Sets the value of the 'payload' field */
    public org.kaaproject.kaa.common.endpoint.gen.PluginSync.Builder setPayload(java.nio.ByteBuffer value) {
      validate(fields()[1], value);
      this.payload = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'payload' field has been set */
    public boolean hasPayload() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'payload' field */
    public org.kaaproject.kaa.common.endpoint.gen.PluginSync.Builder clearPayload() {
      payload = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    public PluginSync build() {
      try {
        PluginSync record = new PluginSync();
        record.pluginId = fieldSetFlags()[0] ? this.pluginId : (java.lang.Integer) defaultValue(fields()[0]);
        record.payload = fieldSetFlags()[1] ? this.payload : (java.nio.ByteBuffer) defaultValue(fields()[1]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}