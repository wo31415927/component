package xstream;

import com.google.common.collect.Lists;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;

import java.util.List;

import xstream.converter.StringLowerConverter;

public class Person {
  private String firstname;
  private String lastname;

  @XStreamAsAttribute
  @XStreamConverter(value = StringLowerConverter.class)
  private String type;

  private transient List<PhoneNumber> phones = Lists.newArrayList();

  public Person() {
    super();
  }

  public Person(String firstname, String lastname) {
    super();
    this.firstname = firstname;
    this.lastname = lastname;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public List<PhoneNumber> getPhones() {
    return phones;
  }

  public void setPhones(List<PhoneNumber> phones) {
    this.phones = phones;
  }

  @Override
  public String toString() {
    return "Person [firstname="
        + firstname
        + ", lastname="
        + lastname
        + ", bio="
        + type
        + ", phones="
        + phones
        + "]";
  }

  public class PhoneNumber {
    private int code;
    private String number;

    public PhoneNumber(int code, String number) {
      super();
      this.code = code;
      this.number = number;
    }

    @Override
    public String toString() {
      return "PhoneNumber [code=" + code + ", number=" + number + "]";
    }
  }
}
