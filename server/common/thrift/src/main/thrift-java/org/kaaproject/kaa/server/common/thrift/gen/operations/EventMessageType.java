/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.kaaproject.kaa.server.common.thrift.gen.operations;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

/**
 * Defines types of messages, all pass through one interface and demultiplex by this enum
 */
public enum EventMessageType implements org.apache.thrift.TEnum {
  ROUTE_UPDATE(1),
  USER_ROUTE_INFO(2),
  EVENT(3),
  ENDPOINT_ROUTE_UPDATE(4),
  ENDPOINT_STATE_UPDATE(5);

  private final int value;

  private EventMessageType(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static EventMessageType findByValue(int value) { 
    switch (value) {
      case 1:
        return ROUTE_UPDATE;
      case 2:
        return USER_ROUTE_INFO;
      case 3:
        return EVENT;
      case 4:
        return ENDPOINT_ROUTE_UPDATE;
      case 5:
        return ENDPOINT_STATE_UPDATE;
      default:
        return null;
    }
  }
}
