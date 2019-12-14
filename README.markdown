# Recaptcha V3

Este module configura Liferay 7.2 con una nueva opción para poder usar reCAPTCHAv3

##Para compilar:

./gradlew clean build

##Instalación

- Copiar con el tomcat parado com.miguelangeljulvez.recaptcha/recaptcha-api/build/libs/com.liferay.captcha.api.jar a bundles/osgi/marketplace/override/ y borrar bundles/osgi/state/
- Copiar com.miguelangeljulvez.recaptcha/recaptcha-impl/build/libs/com.miguelangeljulvez.recaptcha.impl.jar a bundles/osgi/modules/
- Copiar com.miguelangeljulvez.recaptcha/recaptcha-taglbi/build/libs/com.miguelangeljulvez.recaptcha.taglib.jar a bundles/osgi/modules/


##Uso del taglib en los jsp
```java
<%@ taglib prefix="maj" uri="http://miguelangeljulvez.com/tld/recaptcha" %>
...
<liferay-ui:error exception="<%= CaptchaTextException.class %>" message="text-verification-failed" />
<liferay-ui:error exception="<%= CaptchaConfigurationException.class %>" message="a-captcha-error-occurred-please-contact-an-administrator" />
<liferay-ui:error exception="<%= CaptchaException.class %>" message="a-captcha-error-occurred-please-contact-an-administrator" />
...
<aui:form>
...
<maj:recaptchaV3 action="<NombreDeLaAcción>" />
...
</aui:form>
```
##Validación del taglib en el portlet
```java
try {
    CaptchaUtil.check(actionRequest);
} catch (CaptchaConfigurationException e) {
    e.printStackTrace();
    SessionErrors.add(actionRequest, CaptchaConfigurationException.class.getName());
    return;
} catch (CaptchaTextException e) {
    e.printStackTrace();
    SessionErrors.add(actionRequest, CaptchaTextException.class.getName());
    return;
} catch (CaptchaException e) {
    e.printStackTrace();
    SessionErrors.add(actionRequest, CaptchaException.class.getName());
    return;
}
```

Más información en: https://www.miguelangeljulvez.com
