package com.ecnu.pizzaexpressapplication.register.request;

import lombok.Data;

/**
 * Created by yerunjie on 2019-03-13
 *
 * @author yerunjie
 */
@Data
public class RegisterRequest {

  private String account;
  private String password;
  private String phone;
  private String address;

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }
}
