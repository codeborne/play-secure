package play.security;

import controllers.Check;
import play.Play;
import play.mvc.Http;
import play.mvc.Scope;
import play.mvc.results.Forbidden;
import play.mvc.results.Redirect;

import javax.inject.Singleton;

@Singleton
public class AuthorizationService {
  public void checkAccess() {
    if (!Scope.Session.current().contains("username")) {
      Scope.Flash.current().put("url", "GET".equals(Http.Request.current().method) ? Http.Request.current().url : Play.ctxPath + "/"); // seems a good default
      throw new Redirect("/login");
    }
    // Checks
    Check check = Http.Request.current().invokedMethod.getAnnotation(Check.class);
    if (check != null) {
      check(check);
    }
    check = ControllerAnnotations.getForCurrentController(Check.class);
    if (check != null) {
      check(check);
    }
  }

  protected void check(Check check) {
    for (String profile : check.value()) {
      boolean hasProfile = check(profile);
      if (!hasProfile) {
        onCheckFailed(profile);
      }
    }
  }

  public boolean check(String profile) {
    return true;
  }

  protected void onCheckFailed(String profile) {
    throw new Forbidden("Access denied");
  }
}
