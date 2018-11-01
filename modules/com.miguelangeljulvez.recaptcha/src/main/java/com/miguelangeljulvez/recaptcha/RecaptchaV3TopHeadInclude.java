package com.miguelangeljulvez.recaptcha;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.io.PrintWriter;

@Component(
        immediate = true,
        service = DynamicInclude.class
)
public class RecaptchaV3TopHeadInclude implements DynamicInclude {

    @Override
    public void include(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, String key) throws IOException {

        try {
            CaptchaConfiguration captchaConfiguration = _configurationProvider.getSystemConfiguration(CaptchaConfiguration.class);

            String scriptURL = captchaConfiguration.reCaptchaScriptURL();
            String publicCaptchaKey = captchaConfiguration.reCaptchaPublicKey();

            boolean captchaEnabled = publicCaptchaKey != null && !publicCaptchaKey.isEmpty() && captchaConfiguration.maxChallenges() >= 0;

            if (captchaEnabled && RecaptchaV3Keys.NAME.equals(captchaConfiguration.captchaEngine())) {

                PrintWriter writer = response.getWriter();
                writer.println("<script data-senna-track=\"temporary\" src='" + scriptURL + "?render=" + publicCaptchaKey + "'></script>");
                writer.println("<script data-senna-track=\"temporary\">\n" +
                        "           grecaptcha.ready(function() {\n" +
                        "               grecaptcha.execute('" + publicCaptchaKey + "', {action: 'webpage'})\n" +
                        "           });\n" +
                        "       </script>");

            }

        } catch (ConfigurationException e) {
            _log.error(e);
        }
    }

    @Override
    public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
        dynamicIncludeRegistry.register("/html/common/themes/top_head.jsp#pre");
    }

    @Reference(unbind = "-")
    protected void setConfigurationProvider(ConfigurationProvider configurationProvider) {
        _configurationProvider = configurationProvider;
    }

    private ConfigurationProvider _configurationProvider;

    private Log _log = LogFactoryUtil.getLog(RecaptchaV3TopHeadInclude.class);
}
