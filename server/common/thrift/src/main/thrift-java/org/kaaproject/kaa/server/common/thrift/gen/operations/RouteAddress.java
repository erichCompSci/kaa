/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.kaaproject.kaa.server.common.thrift.gen.operations;

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
@Generated(value = "Autogenerated by Thrift Compiler (0.9.2)", date = "2015-10-16")
public class RouteAddress implements org.apache.thrift.TBase<RouteAddress, RouteAddress._Fields>, java.io.Serializable, Cloneable, Comparable<RouteAddress> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("RouteAddress");

  private static final org.apache.thrift.protocol.TField ENDPOINT_KEY_FIELD_DESC = new org.apache.thrift.protocol.TField("endpointKey", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField APPLICATION_TOKEN_FIELD_DESC = new org.apache.thrift.protocol.TField("applicationToken", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField OPERATIONS_SERVER_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("operationsServerId", org.apache.thrift.protocol.TType.STRING, (short)3);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new RouteAddressStandardSchemeFactory());
    schemes.put(TupleScheme.class, new RouteAddressTupleSchemeFactory());
  }

  public ByteBuffer endpointKey; // required
  public String applicationToken; // required
  public String operationsServerId; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ENDPOINT_KEY((short)1, "endpointKey"),
    APPLICATION_TOKEN((short)2, "applicationToken"),
    OPERATIONS_SERVER_ID((short)3, "operationsServerId");

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
        case 1: // ENDPOINT_KEY
          return ENDPOINT_KEY;
        case 2: // APPLICATION_TOKEN
          return APPLICATION_TOKEN;
        case 3: // OPERATIONS_SERVER_ID
          return OPERATIONS_SERVER_ID;
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
    tmpMap.put(_Fields.ENDPOINT_KEY, new org.apache.thrift.meta_data.FieldMetaData("endpointKey", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , "endpoint_id")));
    tmpMap.put(_Fields.APPLICATION_TOKEN, new org.apache.thrift.meta_data.FieldMetaData("applicationToken", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , "application_token")));
    tmpMap.put(_Fields.OPERATIONS_SERVER_ID, new org.apache.thrift.meta_data.FieldMetaData("operationsServerId", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(RouteAddress.class, metaDataMap);
  }

  public RouteAddress() {
  }

  public RouteAddress(
    ByteBuffer endpointKey,
    String applicationToken,
    String operationsServerId)
  {
    this();
    this.endpointKey = org.apache.thrift.TBaseHelper.copyBinary(endpointKey);
    this.applicationToken = applicationToken;
    this.operationsServerId = operationsServerId;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public RouteAddress(RouteAddress other) {
    if (other.isSetEndpointKey()) {
      this.endpointKey = other.endpointKey;
    }
    if (other.isSetApplicationToken()) {
      this.applicationToken = other.applicationToken;
    }
    if (other.isSetOperationsServerId()) {
      this.operationsServerId = other.operationsServerId;
    }
  }

  public RouteAddress deepCopy() {
    return new RouteAddress(this);
  }

  @Override
  public void clear() {
    this.endpointKey = null;
    this.applicationToken = null;
    this.operationsServerId = null;
  }

  public byte[] getEndpointKey() {
    setEndpointKey(org.apache.thrift.TBaseHelper.rightSize(endpointKey));
    return endpointKey == null ? null : endpointKey.array();
  }

  public ByteBuffer bufferForEndpointKey() {
    return org.apache.thrift.TBaseHelper.copyBinary(endpointKey);
  }

  public RouteAddress setEndpointKey(byte[] endpointKey) {
    this.endpointKey = endpointKey == null ? (ByteBuffer)null : ByteBuffer.wrap(Arrays.copyOf(endpointKey, endpointKey.length));
    return this;
  }

  public RouteAddress setEndpointKey(ByteBuffer endpointKey) {
    this.endpointKey = org.apache.thrift.TBaseHelper.copyBinary(endpointKey);
    return this;
  }

  public void unsetEndpointKey() {
    this.endpointKey = null;
  }

  /** Returns true if field endpointKey is set (has been assigned a value) and false otherwise */
  public boolean isSetEndpointKey() {
    return this.endpointKey != null;
  }

  public void setEndpointKeyIsSet(boolean value) {
    if (!value) {
      this.endpointKey = null;
    }
  }

  public String getApplicationToken() {
    return this.applicationToken;
  }

  public RouteAddress setApplicationToken(String applicationToken) {
    this.applicationToken = applicationToken;
    return this;
  }

  public void unsetApplicationToken() {
    this.applicationToken = null;
  }

  /** Returns true if field applicationToken is set (has been assigned a value) and false otherwise */
  public boolean isSetApplicationToken() {
    return this.applicationToken != null;
  }

  public void setApplicationTokenIsSet(boolean value) {
    if (!value) {
      this.applicationToken = null;
    }
  }

  public String getOperationsServerId() {
    return this.operationsServerId;
  }

  public RouteAddress setOperationsServerId(String operationsServerId) {
    this.operationsServerId = operationsServerId;
    return this;
  }

  public void unsetOperationsServerId() {
    this.operationsServerId = null;
  }

  /** Returns true if field operationsServerId is set (has been assigned a value) and false otherwise */
  public boolean isSetOperationsServerId() {
    return this.operationsServerId != null;
  }

  public void setOperationsServerIdIsSet(boolean value) {
    if (!value) {
      this.operationsServerId = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case ENDPOINT_KEY:
      if (value == null) {
        unsetEndpointKey();
      } else {
        setEndpointKey((ByteBuffer)value);
      }
      break;

    case APPLICATION_TOKEN:
      if (value == null) {
        unsetApplicationToken();
      } else {
        setApplicationToken((String)value);
      }
      break;

    case OPERATIONS_SERVER_ID:
      if (value == null) {
        unsetOperationsServerId();
      } else {
        setOperationsServerId((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case ENDPOINT_KEY:
      return getEndpointKey();

    case APPLICATION_TOKEN:
      return getApplicationToken();

    case OPERATIONS_SERVER_ID:
      return getOperationsServerId();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case ENDPOINT_KEY:
      return isSetEndpointKey();
    case APPLICATION_TOKEN:
      return isSetApplicationToken();
    case OPERATIONS_SERVER_ID:
      return isSetOperationsServerId();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof RouteAddress)
      return this.equals((RouteAddress)that);
    return false;
  }

  public boolean equals(RouteAddress that) {
    if (that == null)
      return false;

    boolean this_present_endpointKey = true && this.isSetEndpointKey();
    boolean that_present_endpointKey = true && that.isSetEndpointKey();
    if (this_present_endpointKey || that_present_endpointKey) {
      if (!(this_present_endpointKey && that_present_endpointKey))
        return false;
      if (!this.endpointKey.equals(that.endpointKey))
        return false;
    }

    boolean this_present_applicationToken = true && this.isSetApplicationToken();
    boolean that_present_applicationToken = true && that.isSetApplicationToken();
    if (this_present_applicationToken || that_present_applicationToken) {
      if (!(this_present_applicationToken && that_present_applicationToken))
        return false;
      if (!this.applicationToken.equals(that.applicationToken))
        return false;
    }

    boolean this_present_operationsServerId = true && this.isSetOperationsServerId();
    boolean that_present_operationsServerId = true && that.isSetOperationsServerId();
    if (this_present_operationsServerId || that_present_operationsServerId) {
      if (!(this_present_operationsServerId && that_present_operationsServerId))
        return false;
      if (!this.operationsServerId.equals(that.operationsServerId))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_endpointKey = true && (isSetEndpointKey());
    list.add(present_endpointKey);
    if (present_endpointKey)
      list.add(endpointKey);

    boolean present_applicationToken = true && (isSetApplicationToken());
    list.add(present_applicationToken);
    if (present_applicationToken)
      list.add(applicationToken);

    boolean present_operationsServerId = true && (isSetOperationsServerId());
    list.add(present_operationsServerId);
    if (present_operationsServerId)
      list.add(operationsServerId);

    return list.hashCode();
  }

  @Override
  public int compareTo(RouteAddress other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetEndpointKey()).compareTo(other.isSetEndpointKey());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetEndpointKey()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.endpointKey, other.endpointKey);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetApplicationToken()).compareTo(other.isSetApplicationToken());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetApplicationToken()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.applicationToken, other.applicationToken);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetOperationsServerId()).compareTo(other.isSetOperationsServerId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetOperationsServerId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.operationsServerId, other.operationsServerId);
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
    StringBuilder sb = new StringBuilder("RouteAddress(");
    boolean first = true;

    sb.append("endpointKey:");
    if (this.endpointKey == null) {
      sb.append("null");
    } else {
      sb.append(this.endpointKey);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("applicationToken:");
    if (this.applicationToken == null) {
      sb.append("null");
    } else {
      sb.append(this.applicationToken);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("operationsServerId:");
    if (this.operationsServerId == null) {
      sb.append("null");
    } else {
      sb.append(this.operationsServerId);
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

  private static class RouteAddressStandardSchemeFactory implements SchemeFactory {
    public RouteAddressStandardScheme getScheme() {
      return new RouteAddressStandardScheme();
    }
  }

  private static class RouteAddressStandardScheme extends StandardScheme<RouteAddress> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, RouteAddress struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // ENDPOINT_KEY
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.endpointKey = iprot.readBinary();
              struct.setEndpointKeyIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // APPLICATION_TOKEN
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.applicationToken = iprot.readString();
              struct.setApplicationTokenIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // OPERATIONS_SERVER_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.operationsServerId = iprot.readString();
              struct.setOperationsServerIdIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, RouteAddress struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.endpointKey != null) {
        oprot.writeFieldBegin(ENDPOINT_KEY_FIELD_DESC);
        oprot.writeBinary(struct.endpointKey);
        oprot.writeFieldEnd();
      }
      if (struct.applicationToken != null) {
        oprot.writeFieldBegin(APPLICATION_TOKEN_FIELD_DESC);
        oprot.writeString(struct.applicationToken);
        oprot.writeFieldEnd();
      }
      if (struct.operationsServerId != null) {
        oprot.writeFieldBegin(OPERATIONS_SERVER_ID_FIELD_DESC);
        oprot.writeString(struct.operationsServerId);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class RouteAddressTupleSchemeFactory implements SchemeFactory {
    public RouteAddressTupleScheme getScheme() {
      return new RouteAddressTupleScheme();
    }
  }

  private static class RouteAddressTupleScheme extends TupleScheme<RouteAddress> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, RouteAddress struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetEndpointKey()) {
        optionals.set(0);
      }
      if (struct.isSetApplicationToken()) {
        optionals.set(1);
      }
      if (struct.isSetOperationsServerId()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetEndpointKey()) {
        oprot.writeBinary(struct.endpointKey);
      }
      if (struct.isSetApplicationToken()) {
        oprot.writeString(struct.applicationToken);
      }
      if (struct.isSetOperationsServerId()) {
        oprot.writeString(struct.operationsServerId);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, RouteAddress struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.endpointKey = iprot.readBinary();
        struct.setEndpointKeyIsSet(true);
      }
      if (incoming.get(1)) {
        struct.applicationToken = iprot.readString();
        struct.setApplicationTokenIsSet(true);
      }
      if (incoming.get(2)) {
        struct.operationsServerId = iprot.readString();
        struct.setOperationsServerIdIsSet(true);
      }
    }
  }

}

