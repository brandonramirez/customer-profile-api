package com.brandonsramirez.customerProfileApi;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class SearchFilter {
  @Getter @Setter private String firstName;
  @Getter @Setter private String lastName;
  @Getter @Setter private String email;

  public boolean hasFirstName() {
    return hasValue(firstName);
  }

  public boolean hasLastName() {
    return hasValue(lastName);
  }

  public boolean hasEmail() {
    return hasValue(email);
  }

  public boolean isBlank() {
    return !hasFirstName() && !hasLastName() && !hasEmail();
  }

  private boolean hasValue(String value) {
    return value != null && !"".equals(value.trim());
  }

  public static SearchFilter blank() {
    return new SearchFilter();
  }

  public static SearchFilter firstName(String firstName) {
    SearchFilter filter = new SearchFilter();
    filter.firstName = firstName;
    return filter;
  }

  public static SearchFilter lastName(String lastName) {
    SearchFilter filter = new SearchFilter();
    filter.lastName = lastName;
    return filter;
  }

  public static SearchFilter name(String firstName, String lastName) {
    SearchFilter filter = new SearchFilter();
    filter.firstName = firstName;
    filter.lastName = lastName;
    return filter;
  }

  public static SearchFilter email(String email) {
    SearchFilter filter = new SearchFilter();
    filter.email = email;
    return filter;
  }
}
