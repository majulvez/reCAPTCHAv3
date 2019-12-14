# Recaptcha V3

Este module configura Liferay 7.2 con una nueva opción para poder usar reCAPTCHAv3

##Para compilar:

./gradlew clean build

##Instalación

- Copiar con el tomcat parado com.miguelangeljulvez.recaptcha/recaptcha-api/build/libs/com.liferay.captcha.api.jar a bundles/osgi/marketplace/override/ y borrar bundles/osgi/state/
- Copiar com.miguelangeljulvez.recaptcha/recaptcha-impl/build/libs/com.miguelangeljulvez.recaptcha.impl.jar a bundles/osgi/modules/
- Copiar com.miguelangeljulvez.recaptcha/recaptcha-taglbi/build/libs/com.miguelangeljulvez.recaptcha.taglib.jar a bundles/osgi/modules/


##Uso del taglib en los jsp

<%@ taglib prefix="maj" uri="http://miguelangeljulvez.com/tld/recaptcha" %>

<maj:recaptchaV3 action="action" />

Más información en: https://www.miguelangeljulvez.com