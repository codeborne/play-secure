package play.security;

import play.mvc.Scope;

import javax.inject.Singleton;

@Singleton
public class AuthenticationService {

  /**
   * Indicate if a user is currently connected
   * @return  true if the user is connected
   */
  public boolean isConnected() {
      return Scope.Session.current().contains("username");
  }
}
