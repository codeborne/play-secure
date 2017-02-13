package play.security;

import controllers.Check;
import play.Play;
import play.exceptions.UnexpectedException;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Scope;
import play.mvc.results.Forbidden;
import play.mvc.results.Redirect;

import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Singleton
public class AuthorizationService {
  public void checkAccess() throws Throwable {
    if (!Scope.Session.current().contains("username")) {
      Scope.Flash.current().put("url", "GET".equals(Http.Request.current().method) ? Http.Request.current().url : Play.ctxPath + "/"); // seems a good default
      throw new Redirect("/login");
    }
    // Checks
    Check check = Http.Request.current().invokedMethod.getAnnotation(Check.class);
    if (check != null) {
      check(check);
    }
    check = getControllerInheritedAnnotation(Check.class);
    if (check != null) {
      check(check);
    }
  }

  protected void check(Check check) throws Throwable {
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

  private static <T extends Annotation> T getControllerInheritedAnnotation(Class<T> annotationClass) {
    try {
      Method method = Controller.class.getDeclaredMethod("getControllerInheritedAnnotation", Class.class);
      method.setAccessible(true);
      return (T) method.invoke(null, annotationClass);
    }
    catch (RuntimeException e) {
      throw e;
    }
    catch (Exception e) {
      throw new UnexpectedException(e);
    }
  }
}
