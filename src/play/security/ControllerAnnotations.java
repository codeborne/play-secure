package play.security;

import play.mvc.Http;

import java.lang.annotation.Annotation;

public class ControllerAnnotations {
  public static <T extends Annotation> T getForCurrentController(Class<T> annotationClass) {
    return getForCurrentController(annotationClass, Http.Request.current().controllerClass);
  }

  private static <T extends Annotation> T getForCurrentController(Class<T> annotationClass, Class<?> controller) {
    return controller.equals(Object.class) ? null :
        controller.isAnnotationPresent(annotationClass) ? controller.getAnnotation(annotationClass) :
            getForCurrentController(annotationClass, controller.getSuperclass());
  }
}
