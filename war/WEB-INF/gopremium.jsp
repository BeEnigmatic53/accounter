<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i18n" uri="/WEB-INF/i18n.tld"%>
<%@page pageEncoding="UTF-8" %>
<html>
<head>
<title>Accounter | Subscription </title>
<link type="text/css" href="../../css/ss.css" rel="stylesheet" />
<%
String info =(String) request.getAttribute("info");
%>
</head>
<body>
<table id="commanContainer">
<tr>
<td>
<c:if test="${info!=null}"> 
<div id="login_success" class="common-box">
	<span>${info}</span>
</div>
</c:if>
<div class="form-box">
<form id="subscription_form" action=" https://www.paypal.com/cgi-bin/webscr " method="post">
<input type="hidden" name="cmd" value="_s-xclick">
<input type="hidden" name="return" value="http://192.168.0.1:80/main/subscription/thankyou">
<input type="hidden" name="rm" value="2">
<input type="hidden" name="cancel_return" value="http://local.accounterlive.com/main/subscription/cancel">
<input type="hidden" name="hosted_button_id" value="WU3NJFQJCM25G">
<table>
<tr><td><input type="hidden" name="on0" value="Subscription options">Subscription options</td></tr><tr><td><select name="os0">
    <option value="One user monthly">One user : $5.00USD - monthly</option>
    <option value="One user yearly">One user : $50.00USD - yearly</option>
    <option value="2 users monthly">2 users : $10.00USD - monthly</option>
    <option value="2 users yearly">2 users : $100.00USD - yearly</option>
    <option value="5 users monthly">5 users : $25.00USD - monthly</option>
    <option value="5 users yearly">5 users : $250.00USD - yearly</option>
    <option value="Unlimited Users monthly">Unlimited Users : $100.00USD - monthly</option>
    <option value="Unlimited Users yearly">Unlimited Users : $1,000.00USD - yearly</option>
</select> </td></tr>
</table>
<input type="hidden" name="currency_code" value="USD">
<div id="subscribe_button">
<input type="image" src="https://www.paypalobjects.com/en_GB/i/btn/btn_subscribeCC_LG.gif" border="0" name="submit" alt="PayPal � The safer, easier way to pay online.">
<img alt="" border="0" src="https://www.paypalobjects.com/en_GB/i/scr/pixel.gif" width="1" height="1">
<div>
</form>
</div>
</td>
</tr>
</table>
</body>
</html>