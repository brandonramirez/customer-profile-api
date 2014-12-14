package com.brandonsramirez.customerProfileApi;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@ToString(includeFieldNames=true)
public class Customer implements Serializable, Cloneable {
  @Getter @Setter private int customerId;
  @Getter @Setter private String firstName;
  @Getter @Setter private String lastName;

  @Override
  public Object clone() {
    Customer clone = new Customer();
    clone.setCustomerId(this.getCustomerId());
    clone.setFirstName(this.getFirstName());
    clone.setLastName(this.getLastName());
    return clone;
  }
}
