<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.portal.kernel.util.JavaConstants" %>
<%@ page import="com.liferay.captcha.configuration.CaptchaConfiguration" %>
<%@ page import="com.liferay.captcha.util.CaptchaUtil" %>
<%@ page import="com.liferay.portal.kernel.module.configuration.ConfigurationProviderUtil" %>
<%@ page import="javax.portlet.PortletRequest" %>

<liferay-theme:defineObjects />

<%
PortletRequest portletRequest = (PortletRequest)request.getAttribute(JavaConstants.JAVAX_PORTLET_REQUEST);
CaptchaConfiguration captchaConfiguration = ConfigurationProviderUtil.getSystemConfiguration(CaptchaConfiguration.class);

String publicCaptchaKey = captchaConfiguration.reCaptchaPublicKey();

boolean captchaEnabled = publicCaptchaKey != null && !publicCaptchaKey.isEmpty();

if (portletRequest != null) {
	captchaEnabled = captchaEnabled && CaptchaUtil.isEnabled(portletRequest);
}
else {
	captchaEnabled = captchaEnabled && CaptchaUtil.isEnabled(request);
}

String action = (String)request.getAttribute("maj-captcha:recaptcha:action");
%>