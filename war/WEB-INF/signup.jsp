<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!-- The HTML 4.01 Transitional DOCTYPE declaration-->
<!-- above set at the top of the file will set     -->
<!-- the browser's rendering engine into           -->
<!-- "Quirks Mode". Replacing this declaration     -->
<!-- with a "Standards Mode" doctype is supported, -->
<!-- but may lead to some differences in layout.   -->


<html>
<head>
<title> Sign up & Trial free | Accounter
</title>
<meta content="IE=100" http-equiv="X-UA-Compatible">
<% String version = application.getInitParameter("version"); %>
<link rel="shortcut icon" href="../images/favicon.ico" />
<link type="text/css" href="../css/ss.css?version=<%= version%>" rel="stylesheet">
<link type="text/css" href="../css/cmxform.css?version=<%= version%>" rel="stylesheet">\

<script type="text/javascript" src="/jscripts/jquery-1.6.2.js"></script>
<script src="/jscripts/jquery.validate.js" type="text/javascript"></script>
<script type="text/javascript" charset="utf-8">
	var is_ssl = ("https:" == document.location.protocol);
	var asset_host = is_ssl ? "https://s3.amazonaws.com/getsatisfaction.com/" : "http://s3.amazonaws.com/getsatisfaction.com/";
	document.write(unescape("%3Cscript src='" + asset_host + "javascripts/feedback-v2.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript" charset="utf-8">
	var feedback_widget_options = {};

	feedback_widget_options.display = "overlay";  
  	feedback_widget_options.company = "vimukti";
	feedback_widget_options.placement = "left";
	feedback_widget_options.color = "#222";
	feedback_widget_options.style = "idea";
	
	var feedback_widget = new GSFN.feedback_widget(feedback_widget_options);
</script>
<script  type="text/javascript" >
$.validator.setDefaults({
	submitHandler: function() {
		$.blockUI({ message: $("#hiddenLoaderDiv"), css: {height: '80px', width: '300px', display: 'block'} });
		$('#accounterForm').submit();
	}
});
$.validator.addMethod("no_special_characters", function(value, element) {
    return this.optional(element) ||
        value.match(/^[a-zA-Z0-9_]*[a-zA-Z][a-zA-Z0-9_]*$/);
}, "Company ID shouldn't contain special characters");

	$(document).ready(function() {
	    var is_opera = navigator.userAgent.toLowerCase().indexOf('opera') > -1;
	    $('#select-box').keydown(function(event){
		    if(event.which == 9){
		       $('#checkbox').focus();
		    } 
	    });
	   
	    $('#mid-box2').click(function(){
	    $('.indication-box').remove();
	        $('#email_id_box').append('<div class="indication-box"><div class="left-arrow"></div><div class="box-data">Enter a valid email address. A mail will be sent to this email to confirm your account and also in case you forgot your password</div></div>');
	    }).blur(function() {
	        $('.indication-box').remove();
	    });
	   
		$('#submitButton').click(function() {
			$("#accounterForm").validate({
		rules: {
			firstName: "required",
			lastName: "required",
			companyName: {
				required: true,
				no_special_characters: true,
				maxlength: 64
			},
			companyFullName: "required",
			password: {
				required: true,
				minlength: 6
			},
			confirmPassword: {
				required: true,
				minlength: 6,
				equalTo: "#mid-box4"
			},
			emailId: {
				required: true,
				email: true
			},
			agree: "required"
		},
		messages: {
			firstName: "Please enter your first name",
			lastName: "Please enter your last name",
			companyName: "Please enter company ID",
			companyName: {
				required: "Please enter company ID",
				no_special_characters: "Company ID should contain atleast one letter and no special characters",
				maxlength: "Company ID exceeded the maximum length"
			},
			companyFullName: "Please enter company name",
			password: {
				required: "Please provide a password",
				minlength: "Your password must be at least 6 characters long"
			},
			confirmPassword: {
				required: "Please provide a password",
				minlength: "Your password must be at least 6 characters long",
				equalTo: "Please enter the same password as above"
			},
			emailId: "Please enter a valid email address",
			agree: "Please accept Terms of use"
		}
	});
			 <!--$.blockUI({ message: $('#hiddenLoaderDiv'), css: {height: '80px', width: '300px'} });--> 
			<!--document.getElementById("hiddenLoaderDiv").style.display="block";-->
    		<!--document.getElementById("signup").style.display="none";-->
		});
	});
</script>

<%
   String app = request.getHeader( "Nativeapp" );
   boolean isNative = ( app != null && !app.equals(""));
   if( isNative ){ %>
   <link type="text/css" rel="stylesheet" href="../css/nativeLogin.css?version=<%= version%>">
   <% } %>
   
   

</head>
<body>
	<div class ="body-container">
	<%@ include file="./header.jsp" %>
		<div class="middle-part" id="cen">
			<div class="middle-signup-box" id="mid">
			  <div class="back-signup-box">
				<c:if test="${successmessage==null}">
				<h2>Sign up</h2>
				</c:if>
					
							<c:if test="${successmessage!=null}">
							   <div class="success_box"><b style="color: #3299A4; font-size: 17px;">${successmessage}</b></div>
							   <div><a href="/activation">Click here to enter activation code</a></div>
							</c:if>
							<div id="hiddenLoaderDiv" class="hiddenDiv">
								<img src="/images/icons/loading-indicator.gif" height="50" width="50">
								<span style="position: absolute; margin: 10px;">Please Wait...</span>
							</div>
							<c:if test="${successmessage==null}">
							<div id="signup" class="left-side-box">
								<form id="accounterForm" method="post" action="/signup"">
								<fieldset class="fieldset"">
                           <table id="signup-fields" class="signup-fields-box" width="100%">
								
								<tr>
									
									<td class="signup-box">
									  <div class="admin_details_header" ><h3 style="float:left">Admin Details</h3><span class="mandatory">All fields are mandatory</span></div>
									<table>
										<tr>
									 <td style="width : 155px;padding-bottom: 9px"><span>First Name</span>  </td>
									 <td style="padding-bottom: 9px">
										<input id="mid-box"  type="text" tabindex="4" name="firstName">					
									 </td>
								  </tr>
								  <tr>
									<td style="width : 155px"><span>Last Name </span> </td>
									<td>
										 <input id="mid-box1"  type="text" tabindex="5" name="lastName">
										</td>
								  </tr>
								  <tr>
									 <td style="width : 155px"><span>Email ID </span> </td>
									 <td style="padding: 9px 0">
										<input id="mid-box2"  type="text" tabindex="6" name="emailId">
									 </td>
									 <td id="email_id_box">
									 </td>
								  </tr>
								  <tr>
									 <td style="width : 155px"><span>Password</span>  </td>
									 <td>
										 <input id="mid-box4"  type="password" tabindex="7" name="password">
									 </td>
								  </tr>
								  <tr>
									 <td style="width : 155px">Confirm Password  </td>
									 <td>
										 <input id="mid-box5"  type="password" tabindex="8" name="confirmPassword">
									 </td>
								  </tr>
								  <tr>
									 <td style="width : 155px"><span>Phone Number</span> </td>
									 <td>
										 <input id="mid-box6"  type="text" tabindex="9" name="phoneNumber">
										</td>
								  </tr>
								  <tr>
									 <td style="width : 155px;padding-top: 11px"><span>Country</span> </td>
									 <td style="padding-top: 5px;">
										 <select id="select-box" tabindex="10" name="country">
										 	<option value="United Kingdom">United Kingdom</option>
											<option value="United States">United States</option>
											<option value="">---------------------</option>
											<option value="Afghanistan">Afghanistan</option>
											<option value="Albania">Albania</option>
											<option value="Algeria">Algeria</option>
											<option value="American Samoa">American Samoa</option>
											<option value="Andorra">Andorra</option>
											<option value="Angola">Angola</option>
											<option value="Anguilla">Anguilla</option>
											<option value="Antarctica">Antarctica</option>
											<option value="Antigua and Barbuda">Antigua and Barbuda</option>
											<option value="Argentina">Argentina</option>
											<option value="Armenia">Armenia</option>
											<option value="Aruba">Aruba</option>
											<option value="Austria">Austria</option>
											<option value="Australia">Australia</option>
											<option value="Azerbaijan">Azerbaijan</option>
											<option value="Bahamas, The">Bahamas, The</option>
											<option value="Bahrain">Bahrain</option>
											<option value="Bangladesh">Bangladesh</option>
											<option value="Barbados">Barbados</option>
											<option value="Belarus">Belarus</option>
											<option value="Belgium">Belgium</option>
											<option value="Belize">Belize</option>
											<option value="Benin">Benin</option>
											<option value="Bermuda">Bermuda</option>
											<option value="Bhutan">Bhutan</option>
											<option value="Bolivia">Bolivia</option>
											<option value="Bosnia and Herzegovina">Bosnia and Herzegovina</option>
											<option value="Botswana">Botswana</option>
											<option value="Bouvet Island">Bouvet Island</option>
											<option value="Brazil">Brazil</option>
											<option value="British Indian Ocean Territory">British Indian Ocean Territory</option>
											<option value="Brunei">Brunei</option>
											<option value="Bulgaria">Bulgaria</option>
											<option value="Burkina Faso">Burkina Faso</option>
											<option value="Burundi">Burundi</option>
											<option value="Cambodia">Cambodia</option>
											<option value="Cameroon">Cameroon</option>
											<option value="Canada">Canada</option>
											<option value="Cape Verde">Cape Verde</option>
											<option value="Cayman Islands">Cayman Islands</option>
											<option value="Central African Republic">Central African Republic</option>
											<option value="Chad">Chad</option>
											<option value="Chile">Chile</option>
											<option value="China">China</option>
											<option value="Christmas Island">Christmas Island</option>
											<option value="Cocos (Keeling) Islands">Cocos (Keeling) Islands</option>
											<option value="Colombia">Colombia</option>
											<option value="Comoros">Comoros</option>
											<option value="Congo, Democratic Republic of the">Congo, Democratic Republic of the</option>
											<option value="Congo, Republic of the">Congo, Republic of the</option>
											<option value="Cook Islands">Cook Islands</option>
											<option value="Costa Rica">Costa Rica</option>
											<option value="Cote d'Ivoire">Cote d'Ivoire</option>
											<option value="Croatia">Croatia</option>
											<option value="Cuba">Cuba</option>
											<option value="Cyprus">Cyprus</option>
											<option value="Czech Republic">Czech Republic</option>
											<option value="Denmark">Denmark</option>
											<option value="Djibouti">Djibouti</option>
											<option value="Dominica">Dominica</option>
											<option value="Dominican Republic">Dominican Republic</option>
											<option value="Ecuador">Ecuador</option>
											<option value="Egypt">Egypt</option>
											<option value="El Salvador">El Salvador</option>
											<option value="Equatorial Guinea">Equatorial Guinea</option>
											<option value="Eritrea">Eritrea</option>
											<option value="Estonia">Estonia</option>
											<option value="Ethiopia">Ethiopia</option>
											<option value="Falkland Islands (Islas Malvinas)">Falkland Islands (Islas Malvinas)</option>
											<option value="Faroe Islands">Faroe Islands</option>
											<option value="Fiji">Fiji</option>
											<option value="Finland">Finland</option>
											<option value="France">France</option>
											<option value="French Guiana">French Guiana</option>
											<option value="French Polynesia">French Polynesia</option>
											<option value="French Southern and Antarctic Lands">French Southern and Antarctic Lands</option>
											<option value="Gabon">Gabon</option>
											<option value="Gambia, The">Gambia, The</option>
											<option value="Georgia">Georgia</option>
											<option value="Germany">Germany</option>
											<option value="Ghana">Ghana</option>
											<option value="Gibraltar">Gibraltar</option>
											<option value="Greece">Greece</option>
											<option value="Greenland">Greenland</option>
											<option value="Grenada">Grenada</option>
											<option value="Guadeloupe">Guadeloupe</option>
											<option value="Guam">Guam</option>
											<option value="Guatemala">Guatemala</option>
											<option value="Guinea">Guinea</option>
											<option value="Guinea-Bissau">Guinea-Bissau</option>
											<option value="Guyana">Guyana</option>
											<option value="Haiti">Haiti</option>
											<option value="Heard Island and McDonald Islands">Heard Island and McDonald Islands</option>
											<option value="Holy See (Vatican City)">Holy See (Vatican City)</option>
											<option value="Honduras">Honduras</option>
											<option value="Hong Kong">Hong Kong</option>
											<option value="Hungary">Hungary</option>
											<option value="Iceland">Iceland</option>
											<option value="India">India</option>
											<option value="Indonesia">Indonesia</option>
											<option value="Iran">Iran</option>
											<option value="Iraq">Iraq</option>
											<option value="Ireland">Ireland</option>
											<option value="Isle of Man">Isle of Man</option>
											<option value="Israel">Israel</option>
											<option value="Italy">Italy</option>
											<option value="Jamaica">Jamaica</option>
											<option value="Jan Mayen">Jan Mayen</option>
											<option value="Japan">Japan</option>
											<option value="Jordan">Jordan</option>
											<option value="Kazakhstan">Kazakhstan</option>
											<option value="Kenya">Kenya</option>
											<option value="Kiribati">Kiribati</option>
											<option value="Korea, North">Korea, North</option>
											<option value="Korea, South">Korea, South</option>
											<option value="Kuwait">Kuwait</option>
											<option value="Kyrgyzstan">Kyrgyzstan</option>
											<option value="Laos">Laos</option>
											<option value="Latvia">Latvia</option>
											<option value="Lebanon">Lebanon</option>
											<option value="Lesotho">Lesotho</option>
											<option value="Liberia">Liberia</option>
											<option value="Libya">Libya</option>
											<option value="Liechtenstein">Liechtenstein</option>
											<option value="Lithuania">Lithuania</option>
											<option value="Luxembourg">Luxembourg</option>
											<option value="Macau">Macau</option>
											<option value="Macedonia">Macedonia</option>
											<option value="Madagascar">Madagascar</option>
											<option value="Malawi">Malawi</option>
											<option value="Malaysia">Malaysia</option>
											<option value="Maldives">Maldives</option>
											<option value="Mali">Mali</option>
											<option value="Malta">Malta</option>
											<option value="Marshall Islands">Marshall Islands</option>
											<option value="Martinique">Martinique</option>
											<option value="Mauritania">Mauritania</option>
											<option value="Mauritius">Mauritius</option>
											<option value="Mayotte">Mayotte</option>
											<option value="Mexico">Mexico</option>
											<option value="Micronesia, Federated States of">Micronesia, Federated States of</option>
											<option value="Moldova">Moldova</option>
											<option value="Monaco">Monaco</option>
											<option value="Mongolia">Mongolia</option>
											<option value="Montserrat">Montserrat</option>
											<option value="Morocco">Morocco</option>
											<option value="Mozambique">Mozambique</option>
											<option value="Namibia">Namibia</option>
											<option value="Nauru">Nauru</option>
											<option value="Nepal">Nepal</option>
											<option value="Netherlands">Netherlands</option>
											<option value="Netherlands Antilles">Netherlands Antilles</option>
											<option value="New Caledonia">New Caledonia</option>
											<option value="New Zealand">New Zealand</option>
											<option value="Nicaragua">Nicaragua</option>
											<option value="Niger">Niger</option>
											<option value="Nigeria">Nigeria</option>
											<option value="Niue">Niue</option>
											<option value="Norfolk Island">Norfolk Island</option>
											<option value="Northern Mariana Islands">Northern Mariana Islands</option>
											<option value="Norway">Norway</option>
											<option value="Oman">Oman</option>
											<option value="Pakistan">Pakistan</option>
											<option value="Palau">Palau</option>
											<option value="Panama">Panama</option>
											<option value="Papua New Guinea">Papua New Guinea</option>
											<option value="Paraguay">Paraguay</option>
											<option value="Peru">Peru</option>
											<option value="Philippines">Philippines</option>
											<option value="Pitcairn Islands">Pitcairn Islands</option>
											<option value="Poland">Poland</option>
											<option value="Portugal">Portugal</option>
											<option value="Puerto Rico">Puerto Rico</option>
											<option value="Qatar">Qatar</option>
											<option value="Reunion">Reunion</option>
											<option value="Romania">Romania</option>
											<option value="Russia">Russia</option>
											<option value="Rwanda">Rwanda</option>
											<option value="Saint Helena">Saint Helena</option>
											<option value="Saint Kitts and Nevis">Saint Kitts and Nevis</option>
											<option value="Saint Lucia">Saint Lucia</option>
											<option value="Saint Pierre and Miquelon">Saint Pierre and Miquelon</option>
											<option value="Saint Vincent and the Grenadines">Saint Vincent and the Grenadines</option>
											<option value="Samoa">Samoa</option>
											<option value="San Marino">San Marino</option>
											<option value="Sao Tome and Principe">Sao Tome and Principe</option>
											<option value="Saudi Arabia">Saudi Arabia</option>
											<option value="Senegal">Senegal</option>
											<option value="Serbia and Montenegro">Serbia and Montenegro</option>
											<option value="Seychelles">Seychelles</option>
											<option value="Sierra Leone">Sierra Leone</option>
											<option value="Singapore">Singapore</option>
											<option value="Slovakia">Slovakia</option>
											<option value="Slovenia">Slovenia</option>
											<option value="Solomon Islands">Solomon Islands</option>
											<option value="Somalia">Somalia</option>
											<option value="South Africa">South Africa</option>
											<option value="South Georgia and the South Sandwich Islands">South Georgia and the South Sandwich Islands</option>
											<option value="Spain">Spain</option>
											<option value="Sri Lanka">Sri Lanka</option>
											<option value="Sudan">Sudan</option>
											<option value="Suriname">Suriname</option>
											<option value="Svalbard">Svalbard</option>
											<option value="Swaziland">Swaziland</option>
											<option value="Sweden">Sweden</option>
											<option value="Switzerland">Switzerland</option>
											<option value="Syria">Syria</option>
											<option value="Taiwan">Taiwan</option>
											<option value="Tajikistan">Tajikistan</option>
											<option value="Tanzania">Tanzania</option>
											<option value="Thailand">Thailand</option>
											<option value="Timor-Leste">Timor-Leste</option>
											<option value="Togo">Togo</option>
											<option value="Tokelau">Tokelau</option>
											<option value="Tonga">Tonga</option>
											<option value="Trinidad and Tobago">Trinidad and Tobago</option>
											<option value="Tunisia">Tunisia</option>
											<option value="Turkey">Turkey</option>
											<option value="Turkmenistan">Turkmenistan</option>
											<option value="Turks and Caicos Islands">Turks and Caicos Islands</option>
											<option value="Tuvalu">Tuvalu</option>
											<option value="Uganda">Uganda</option>
											<option value="Ukraine">Ukraine</option>
											<option value="United Arab Emirates">United Arab Emirates</option>
											<option value="Uruguay">Uruguay</option>
											<option value="Uzbekistan">Uzbekistan</option>
											<option value="Vanuatu">Vanuatu</option>
											<option value="Venezuela">Venezuela</option>
											<option value="Vietnam">Vietnam</option>
											<option value="Virgin Islands">Virgin Islands</option>
											<option value="Wallis and Futuna">Wallis and Futuna</option>
											<option value="Western Sahara">Western Sahara</option>
											<option value="Yemen">Yemen</option>
											<option value="Zambia">Zambia</option>
											<option value="Zimbabwe">Zimbabwe</option>
										</select>
									 </td>
								  </tr>
								  	</table>
								  	<div class="signup-box">
								  	  <h3 style="border-bottom:1px solid #DBDBDB">Terms of Service</h3>
								  	  <table>
								  	      <tr>
								     <td width="155px"></td>
								     <td>
								        <div class="terms-error">
								          <span style="margin-left:13px;color:#000">I have read and I accept the</span> <a href="/site/termsandconditions" tabindex="12">Terms of Use</a><input id="checkbox" type="checkbox" name="agree" tabindex="11" ></input>
							            </div>
								     </td>
								  </tr>
								   <tr>
								     <td width="155px"></td>
								     <td>
								        <div class="newsletter"><input id="newsletter" type="checkbox" name="newsletter">Yes, Subscribe me to Accounter Newsletter </input><b>(Optional)</b></div>
								     </td>
								  </tr>
								  <tr>
								     <td width="155px"></td>
								     <td>
								        <ul class="getstarted-submit-button">
										     <li><span class="signup-submit-left"></span></li>
										     <li><input id="submitButton" type="submit" class="signup-submit-mid" name="getstarted" value="Sign Up" tabindex="13" ></input></li>
										     <li><span class="signup-submit-right"></span></li>
										 </ul>
								     </td>
								  </tr>
								  	  </table>
								  	</div>
									</td>
									
								</tr>
								
								</table>
					
								</table>
								</fieldset>
								</form>
								
							</div>
							</c:if>
					
				<div class="signup-box-left-bot"></div>
				</div>
			</div><div class="box-shadow"></div>
		</div>
		
		<div class="down-test" id="down"></div>	
		<%@ include file="./footer.jsp" %>
		</div>
		<script  type="text/javascript" >
			if(${successmessage!=null}) {
				document.getElementById("right-side-options").style.marginTop = "15px";
				document.getElementById("vertical-line").style.marginTop = "30px";
			}
		</script>
		
<script type="text/javascript">

var _gaq = _gaq || [];
_gaq.push(['_setAccount', 'UA-24502570-1']);
_gaq.push(['_trackPageview']);

(function() {
var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
})();

</script>

</body>
</html>