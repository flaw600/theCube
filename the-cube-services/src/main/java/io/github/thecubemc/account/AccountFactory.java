package io.github.thecubemc.account;

import com.google.common.eventbus.EventBus;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import io.github.thecubemc.event.LoginEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Singleton
public final class AccountFactory
implements Provider<AccountStub> {
  private static final AccountStub defaultStub = new AccountStub(null);
  private final MojangAuthentication auth = new MojangAuthentication();

  private final Map<String, Account> accounts = new HashMap<>();
  private final Lock lock = new ReentrantLock();
  private Account current;

  public AccountStub login(Injector injector, String name) {
    if (this.accounts.containsKey(name)) return this.accounts.get(name)
                                                             .getStub(null);
    EventBus eventbus = injector.getInstance(EventBus.class);
    LoginEvent event = new LoginEvent(injector);
    eventbus.post(event);

    if (event.isCanceled()) {
      this.set(null);
      return this.get();
    }

    Account acc = this.auth.login(new Account(event.getUsername(), event.getPassword(), true));
    this.accounts.put(name, acc);
    this.set(acc);
    return this.get();
  }

  public void set(Account acc){
    this.lock.lock();
    try{
      this.current = acc;
    } finally{
      this.lock.unlock();
    }
  }

  @Override
  public AccountStub get() {
    this.lock.lock();
    try{
      return this.current == null ? defaultStub : this.current.getStub(null);
    } finally{
      this.lock.unlock();
    }
  }
}