/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.kaaproject.kaa.server.common.thrift.gen.control;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.2)", date = "2015-9-21")
public class ControlThriftException extends TException implements org.apache.thrift.TBase<ControlThriftException, ControlThriftException._Fields>, java.io.Serializable, Cloneable, Comparable<ControlThriftException> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("ControlThriftException");

  private static final org.apache.thrift.protocol.TField MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("message", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField CAUSE_EXCEPTION_CLASS_FIELD_DESC = new org.apache.thrift.protocol.TField("causeExceptionClass", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField CAUSE_STACK_STRACE_FIELD_DESC = new org.apache.thrift.protocol.TField("causeStackStrace", org.apache.thrift.protocol.TType.STRING, (short)3);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new ControlThriftExceptionStandardSchemeFactory());
    schemes.put(TupleScheme.class, new ControlThriftExceptionTupleSchemeFactory());
  }

  public String message; // required
  public String causeExceptionClass; // required
  public String causeStackStrace; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    MESSAGE((short)1, "message"),
    CAUSE_EXCEPTION_CLASS((short)2, "causeExceptionClass"),
    CAUSE_STACK_STRACE((short)3, "causeStackStrace");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // MESSAGE
          return MESSAGE;
        case 2: // CAUSE_EXCEPTION_CLASS
          return CAUSE_EXCEPTION_CLASS;
        case 3: // CAUSE_STACK_STRACE
          return CAUSE_STACK_STRACE;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("message", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.CAUSE_EXCEPTION_CLASS, new org.apache.thrift.meta_data.FieldMetaData("causeExceptionClass", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.CAUSE_STACK_STRACE, new org.apache.thrift.meta_data.FieldMetaData("causeStackStrace", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(ControlThriftException.class, metaDataMap);
  }

  public ControlThriftException() {
  }

  public ControlThriftException(
    String message,
    String causeExceptionClass,
    String causeStackStrace)
  {
    this();
    this.message = message;
    this.causeExceptionClass = causeExceptionClass;
    this.causeStackStrace = causeStackStrace;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ControlThriftException(ControlThriftException other) {
    if (other.isSetMessage()) {
      this.message = other.message;
    }
    if (other.isSetCauseExceptionClass()) {
      this.causeExceptionClass = other.causeExceptionClass;
    }
    if (other.isSetCauseStackStrace()) {
      this.causeStackStrace = other.causeStackStrace;
    }
  }

  public ControlThriftException deepCopy() {
    return new ControlThriftException(this);
  }

  @Override
  public void clear() {
    this.message = null;
    this.causeExceptionClass = null;
    this.causeStackStrace = null;
  }

  public String getMessage() {
    return this.message;
  }

  public ControlThriftException setMessage(String message) {
    this.message = message;
    return this;
  }

  public void unsetMessage() {
    this.message = null;
  }

  /** Returns true if field message is set (has been assigned a value) and false otherwise */
  public boolean isSetMessage() {
    return this.message != null;
  }

  public void setMessageIsSet(boolean value) {
    if (!value) {
      this.message = null;
    }
  }

  public String getCauseExceptionClass() {
    return this.causeExceptionClass;
  }

  public ControlThriftException setCauseExceptionClass(String causeExceptionClass) {
    this.causeExceptionClass = causeExceptionClass;
    return this;
  }

  public void unsetCauseExceptionClass() {
    this.causeExceptionClass = null;
  }

  /** Returns true if field causeExceptionClass is set (has been assigned a value) and false otherwise */
  public boolean isSetCauseExceptionClass() {
    return this.causeExceptionClass != null;
  }

  public void setCauseExceptionClassIsSet(boolean value) {
    if (!value) {
      this.causeExceptionClass = null;
    }
  }

  public String getCauseStackStrace() {
    return this.causeStackStrace;
  }

  public ControlThriftException setCauseStackStrace(String causeStackStrace) {
    this.causeStackStrace = causeStackStrace;
    return this;
  }

  public void unsetCauseStackStrace() {
    this.causeStackStrace = null;
  }

  /** Returns true if field causeStackStrace is set (has been assigned a value) and false otherwise */
  public boolean isSetCauseStackStrace() {
    return this.causeStackStrace != null;
  }

  public void setCauseStackStraceIsSet(boolean value) {
    if (!value) {
      this.causeStackStrace = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case MESSAGE:
      if (value == null) {
        unsetMessage();
      } else {
        setMessage((String)value);
      }
      break;

    case CAUSE_EXCEPTION_CLASS:
      if (value == null) {
        unsetCauseExceptionClass();
      } else {
        setCauseExceptionClass((String)value);
      }
      break;

    case CAUSE_STACK_STRACE:
      if (value == null) {
        unsetCauseStackStrace();
      } else {
        setCauseStackStrace((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case MESSAGE:
      return getMessage();

    case CAUSE_EXCEPTION_CLASS:
      return getCauseExceptionClass();

    case CAUSE_STACK_STRACE:
      return getCauseStackStrace();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case MESSAGE:
      return isSetMessage();
    case CAUSE_EXCEPTION_CLASS:
      return isSetCauseExceptionClass();
    case CAUSE_STACK_STRACE:
      return isSetCauseStackStrace();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof ControlThriftException)
      return this.equals((ControlThriftException)that);
    return false;
  }

  public boolean equals(ControlThriftException that) {
    if (that == null)
      return false;

    boolean this_present_message = true && this.isSetMessage();
    boolean that_present_message = true && that.isSetMessage();
    if (this_present_message || that_present_message) {
      if (!(this_present_message && that_present_message))
        return false;
      if (!this.message.equals(that.message))
        return false;
    }

    boolean this_present_causeExceptionClass = true && this.isSetCauseExceptionClass();
    boolean that_present_causeExceptionClass = true && that.isSetCauseExceptionClass();
    if (this_present_causeExceptionClass || that_present_causeExceptionClass) {
      if (!(this_present_causeExceptionClass && that_present_causeExceptionClass))
        return false;
      if (!this.causeExceptionClass.equals(that.causeExceptionClass))
        return false;
    }

    boolean this_present_causeStackStrace = true && this.isSetCauseStackStrace();
    boolean that_present_causeStackStrace = true && that.isSetCauseStackStrace();
    if (this_present_causeStackStrace || that_present_causeStackStrace) {
      if (!(this_present_causeStackStrace && that_present_causeStackStrace))
        return false;
      if (!this.causeStackStrace.equals(that.causeStackStrace))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_message = true && (isSetMessage());
    list.add(present_message);
    if (present_message)
      list.add(message);

    boolean present_causeExceptionClass = true && (isSetCauseExceptionClass());
    list.add(present_causeExceptionClass);
    if (present_causeExceptionClass)
      list.add(causeExceptionClass);

    boolean present_causeStackStrace = true && (isSetCauseStackStrace());
    list.add(present_causeStackStrace);
    if (present_causeStackStrace)
      list.add(causeStackStrace);

    return list.hashCode();
  }

  @Override
  public int compareTo(ControlThriftException other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetMessage()).compareTo(other.isSetMessage());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMessage()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.message, other.message);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetCauseExceptionClass()).compareTo(other.isSetCauseExceptionClass());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetCauseExceptionClass()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.causeExceptionClass, other.causeExceptionClass);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetCauseStackStrace()).compareTo(other.isSetCauseStackStrace());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetCauseStackStrace()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.causeStackStrace, other.causeStackStrace);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("ControlThriftException(");
    boolean first = true;

    sb.append("message:");
    if (this.message == null) {
      sb.append("null");
    } else {
      sb.append(this.message);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("causeExceptionClass:");
    if (this.causeExceptionClass == null) {
      sb.append("null");
    } else {
      sb.append(this.causeExceptionClass);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("causeStackStrace:");
    if (this.causeStackStrace == null) {
      sb.append("null");
    } else {
      sb.append(this.causeStackStrace);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class ControlThriftExceptionStandardSchemeFactory implements SchemeFactory {
    public ControlThriftExceptionStandardScheme getScheme() {
      return new ControlThriftExceptionStandardScheme();
    }
  }

  private static class ControlThriftExceptionStandardScheme extends StandardScheme<ControlThriftException> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, ControlThriftException struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // MESSAGE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.message = iprot.readString();
              struct.setMessageIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // CAUSE_EXCEPTION_CLASS
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.causeExceptionClass = iprot.readString();
              struct.setCauseExceptionClassIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // CAUSE_STACK_STRACE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.causeStackStrace = iprot.readString();
              struct.setCauseStackStraceIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, ControlThriftException struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.message != null) {
        oprot.writeFieldBegin(MESSAGE_FIELD_DESC);
        oprot.writeString(struct.message);
        oprot.writeFieldEnd();
      }
      if (struct.causeExceptionClass != null) {
        oprot.writeFieldBegin(CAUSE_EXCEPTION_CLASS_FIELD_DESC);
        oprot.writeString(struct.causeExceptionClass);
        oprot.writeFieldEnd();
      }
      if (struct.causeStackStrace != null) {
        oprot.writeFieldBegin(CAUSE_STACK_STRACE_FIELD_DESC);
        oprot.writeString(struct.causeStackStrace);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class ControlThriftExceptionTupleSchemeFactory implements SchemeFactory {
    public ControlThriftExceptionTupleScheme getScheme() {
      return new ControlThriftExceptionTupleScheme();
    }
  }

  private static class ControlThriftExceptionTupleScheme extends TupleScheme<ControlThriftException> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, ControlThriftException struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetMessage()) {
        optionals.set(0);
      }
      if (struct.isSetCauseExceptionClass()) {
        optionals.set(1);
      }
      if (struct.isSetCauseStackStrace()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetMessage()) {
        oprot.writeString(struct.message);
      }
      if (struct.isSetCauseExceptionClass()) {
        oprot.writeString(struct.causeExceptionClass);
      }
      if (struct.isSetCauseStackStrace()) {
        oprot.writeString(struct.causeStackStrace);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, ControlThriftException struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.message = iprot.readString();
        struct.setMessageIsSet(true);
      }
      if (incoming.get(1)) {
        struct.causeExceptionClass = iprot.readString();
        struct.setCauseExceptionClassIsSet(true);
      }
      if (incoming.get(2)) {
        struct.causeStackStrace = iprot.readString();
        struct.setCauseStackStraceIsSet(true);
      }
    }
  }

}

