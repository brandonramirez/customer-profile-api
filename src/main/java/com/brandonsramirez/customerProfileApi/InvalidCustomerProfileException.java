package com.brandonsramirez.customerProfileApi;

public class InvalidCustomerProfileException extends Exception {
  public InvalidCustomerProfileException() {
    super();
  }

  public InvalidCustomerProfileException(String msg) {
    super(msg);
  }

  public InvalidCustomerProfileException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public InvalidCustomerProfileException(Throwable cause) {
    super(cause);
  }
}