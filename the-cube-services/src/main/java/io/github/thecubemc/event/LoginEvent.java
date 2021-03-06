package io.github.thecubemc.event;

import com.google.inject.Injector;

public final class LoginEvent{
  public final Injector injector;
  private boolean canceled = false;
  private String username;
  private String password;

  public LoginEvent(Injector injector) {this.injector = injector;}

  public String getPassword() {
    return password;
  }

  public LoginEvent setPassword(String password) {
    this.password = password;
    return this;
  }

  public String getUsername() {
    return username;
  }

  public LoginEvent setUsername(String username) {
    this.username = username;
    return this;
  }

  public boolean isCanceled(){
    return this.canceled;
  }

  public void cancel(){
    this.canceled = true;
  }
}