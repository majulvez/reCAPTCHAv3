package com.miguelangeljulvez.recaptcha.taglib.servlet.taglib;

import com.miguelangeljulvez.recaptcha.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

public class CaptchaTag extends IncludeTag {

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setAction(String action) {
		_action = action;
	}

	public void setThreshold(String threshold) {
		_threshold = threshold;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_action = null;
		_threshold = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest request) {
		request.setAttribute("maj-captcha:recaptcha:action", _action);
		request.setAttribute("maj-captcha:recaptcha:threshold", _threshold);
	}

	private static final String _PAGE = "/recaptchaV3/page.jsp";

	private String _action;

	private String _threshold;

}