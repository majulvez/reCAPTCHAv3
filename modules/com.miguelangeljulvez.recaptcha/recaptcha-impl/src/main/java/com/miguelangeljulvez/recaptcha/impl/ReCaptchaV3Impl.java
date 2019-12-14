package com.miguelangeljulvez.recaptcha.impl;

import com.liferay.captcha.configuration.CaptchaConfiguration;
import com.liferay.captcha.simplecaptcha.SimpleCaptchaImpl;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.captcha.Captcha;
import com.liferay.portal.kernel.captcha.CaptchaConfigurationException;
import com.liferay.portal.kernel.captcha.CaptchaException;
import com.liferay.portal.kernel.captcha.CaptchaTextException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import com.liferay.captcha.configuration.RecaptchaV3Keys;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

@Component(
		configurationPid = "com.liferay.captcha.configuration.CaptchaConfiguration",
		immediate = true,
		property = "captcha.engine.impl=" + RecaptchaV3Keys.NAME,
		service = Captcha.class
)
public class ReCaptchaV3Impl extends SimpleCaptchaImpl {

	@Override
	public String getTaglibPath() {
		return _TAGLIB_PATH;
	}

	@Override
	public void serveImage(
			HttpServletRequest request, HttpServletResponse response) {

		throw new UnsupportedOperationException();
	}

	@Override
	public void serveImage(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse) {

		throw new UnsupportedOperationException();
	}

	@Activate
	@Modified
	@Override
	protected void activate(Map<String, Object> properties) {
		_captchaConfiguration = ConfigurableUtil.createConfigurable(CaptchaConfiguration.class, properties);

		setCaptchaConfiguration(_captchaConfiguration);
	}

	@Override
	protected boolean validateChallenge(HttpServletRequest request)
			throws CaptchaException {

		String reCaptchaResponse = ParamUtil.getString(request, "g-recaptcha-response");

		String reCaptchaResponseAction = ParamUtil.getString(request, "g-recaptcha-response-action");

		while (Validator.isBlank(reCaptchaResponse) &&
				(request instanceof HttpServletRequestWrapper)) {

			HttpServletRequestWrapper httpServletRequestWrapper = (HttpServletRequestWrapper)request;

			request = (HttpServletRequest)httpServletRequestWrapper.getRequest();

			reCaptchaResponse = ParamUtil.getString(request, "g-recaptcha-response");

			reCaptchaResponseAction = ParamUtil.getString(request, "g-recaptcha-response-action");
		}

		if (Validator.isBlank(reCaptchaResponse) || Validator.isBlank(reCaptchaResponseAction)) {
			_log.error(
					"CAPTCHA text is null. User " + request.getRemoteUser() +
							" may be trying to circumvent the CAPTCHA.");

			throw new CaptchaTextException();
		}

		Http.Options options = new Http.Options();

		options.setLocation(_captchaConfiguration.reCaptchaVerifyURL());

		try {
			options.addPart("secret", _captchaConfiguration.reCaptchaPrivateKey());
		}
		catch (SystemException se) {
			_log.error(se, se);
		}

		options.addPart("remoteip", request.getRemoteAddr());
		options.addPart("response", reCaptchaResponse);
		options.setPost(true);

		String content = null;

		try {
			content = HttpUtil.URLtoString(options);
		}
		catch (IOException ioe) {
			_log.error(ioe, ioe);

			throw new CaptchaConfigurationException();
		}

		if (content == null) {
			_log.error("reCAPTCHA did not return a result");

			throw new CaptchaConfigurationException();
		}

		try {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(content);

			String success = jsonObject.getString("success");
			double score = jsonObject.getDouble("score");
			String action = jsonObject.getString("action");

			_log.info("success: " + success);
			_log.info("score: " + score);
			_log.info("action: " + action);

			if (!StringUtil.equalsIgnoreCase(success, "true")) {
				_log.info("La operación no ha sido correcta");
				throw new CaptchaTextException();
			}

			if (!StringUtil.equalsIgnoreCase(action, reCaptchaResponseAction)) {
				_log.info("El action no coincide");
				throw new CaptchaTextException();
			}

			//Se podría hacer que para cada action, fuera un score mínimo diferente
			if (score > 0.1) {
				return true;
			}

			JSONArray jsonArray = jsonObject.getJSONArray("error-codes");

			if ((jsonArray == null) || (jsonArray.length() == 0)) {
				_log.error("reCAPTCHA encountered an error");

				throw new CaptchaConfigurationException();
			}

			StringBundler sb = new StringBundler(jsonArray.length() * 2 - 1);

			for (int i = 0; i < jsonArray.length(); i++) {
				sb.append(jsonArray.getString(i));

				if (i < (jsonArray.length() - 1)) {
					sb.append(StringPool.COMMA_AND_SPACE);
				}
			}

			_log.error("reCAPTCHA encountered an error: " + sb.toString());

			throw new CaptchaConfigurationException();
		}
		catch (JSONException jsone) {
			_log.error(
					"reCAPTCHA did not return a valid result: " + content, jsone);

			throw new CaptchaConfigurationException();
		}
	}

	@Override
	protected boolean validateChallenge(PortletRequest portletRequest)
			throws CaptchaException {

		HttpServletRequest request = PortalUtil.getHttpServletRequest(
				portletRequest);

		return validateChallenge(request);
	}

	private static final String _TAGLIB_PATH = "/recaptchaV3/recaptchaV3.jsp";

	private final Log _log = LogFactoryUtil.getLog(this.getClass());

	private volatile CaptchaConfiguration _captchaConfiguration;

}