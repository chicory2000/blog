package com.snmp.wallet.pin;

import com.google.common.base.Strings;

public class Pin {
   final private String pin;
   final private Boolean isResettable;

   final public static Pin CLEAR_PIN = new Pin("");

   public Pin(String pin, Boolean isResettable) {
      this.pin = pin;
      this.isResettable = isResettable;
   }

   public Pin(String pin) {
      this.pin = pin;
      this.isResettable = true;
   }

   public boolean isSet(){
      return !Strings.isNullOrEmpty(pin);
   }

   public String getPin() {
      return pin;
   }

   public Boolean isResettable() {
      return isResettable;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Pin)) return false;

      Pin pin1 = (Pin) o;

      if (pin != null ? !pin.equals(pin1.pin) : pin1.pin != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = pin != null ? pin.hashCode() : 0;
      result = 31 * result + (isResettable != null ? isResettable.hashCode() : 0);
      return result;
   }
}

