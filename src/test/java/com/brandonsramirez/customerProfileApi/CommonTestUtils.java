package com.brandonsramirez.customerProfileApi;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

public class CommonTestUtils {
  static Customer makeCustomer(int customerId, String firstName, String lastName) {
    Customer c = makeCustomer(firstName, lastName);
    c.setCustomerId(customerId);
    return c;
  }

  static Customer makeCustomer(int customerId, String firstName, String lastName, String email) {
    Customer c = makeCustomer(firstName, lastName, email);
    c.setCustomerId(customerId);
    return c;
  }

  static Customer makeCustomer(String firstName, String lastName) {
    Customer c = new Customer();
    c.setFirstName(firstName);
    c.setLastName(lastName);
    return c;
  }

  static Customer makeCustomer(String firstName, String lastName, String email) {
    Customer c = makeCustomer(firstName, lastName);
    c.setEmail(email);
    return c;
  }

  static CustomerProfileService makeCustomerProfileService(final Customer... initialCustomers) {
    return new CustomerProfileService(new MemoryCustomerDao() {
      // Over-ride the in-memory map with our own map so that we have some control
      // over the underlying data store.
      private Map<Integer, Customer> customers = new HashMap<Integer, Customer>() {{
        for (Customer c : initialCustomers) {
          put(c.getCustomerId(), c);
        }
      }};

      @Override
      protected Map<Integer, Customer> getCustomerMap() {
        return customers;
      }
    });
  }

  static CustomerResource makeCustomerResourceWithService(final CustomerProfileService service) {
    return new CustomerResource() {
      @Override
      protected CustomerProfileService getCustomerProfileService(ServletContext ctx) {
        return service;
      }
    };
  }
}
