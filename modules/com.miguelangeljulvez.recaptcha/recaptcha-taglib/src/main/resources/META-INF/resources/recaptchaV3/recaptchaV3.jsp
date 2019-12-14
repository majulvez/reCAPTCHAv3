<%@ include file="init.jsp" %>

<c:if test="<%= captchaEnabled %>">

	<div id="g-recaptcha" data-sitekey="<%=captchaConfiguration.reCaptchaPublicKey()%>"></div>

	<input type="hidden" name="g-recaptcha-response" id="g-recaptcha-response" value="" />
	<input type="hidden" name="g-recaptcha-response-action" id="g-recaptcha-response-action" value="<%=action%>" />

	<script data-senna-track="temporary">
		grecaptcha.ready(function() {
			grecaptcha.execute('<%= captchaConfiguration.reCaptchaPublicKey() %>', {action: '<%=action%>'}).then(function(token) {
				$('#g-recaptcha-response').val(token);
			});
		});
	</script>

</c:if>