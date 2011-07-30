
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body>
<script type="text/javascript">
window.domain=location.hostname.toString();
window.onError = null;
//extract_xss_domain();  //domainparts[domainparts.length-2]+"."+domainparts[domainparts.length-1];
parent.Meteor.register(this);
var streamreq;
var byteoffset;
var newdata;



function extract_xss_domain() {
    if (document.domain.match(/^(\d{1,3}\.){3}\d{1,3}$/)) {
    	document.domain=document.domain;
        }
    else{
    domain_pieces = document.domain.split('.');
    document.domain = domain_pieces.slice(-2, domain_pieces.length).join(".");
    }

    }

function abort() {
        streamreq.abort();
}

function newXmlHttp() {
        var xhr = null;
        if(window.ActiveXObject){
	        try { xhr = new ActiveXObject("Msxml2.XMLHTTP"); } 
	        catch (e) { try { xhr = new ActiveXObject("Microsoft.XMLHTTP"); } 
	        			catch (e) {return false;}
	        }
        }
        if (window.XMLHttpRequest) {
        	try { xhr = new XMLHttpRequest(); } catch(e) { return false; }
        }
        return xhr;
}

function startstream() {
        streamreq = newXmlHttp();
        byteoffset = 0;
        newdata = "";
        var url = parent.Meteor.getSubsUrl();
        streamreq.open("GET", url, true);
        streamreq.onreadystatechange = function() {
                if (typeof streamreq == "undefined") return;
                if (streamreq.readyState == 3) {
                        extractEvents(streamreq.responseText);
                } else if (streamreq.readyState == 4) {
                        extractEvents(streamreq.responseText);
                        delete streamreq;
                        if (typeof(r)=="function") {
                                r();
                        }
                }
        }
        streamreq.send(null);
        parent.Meteor.lastrequest = parent.Meteor.time();
}

function extractEvents(responsestr) {
        newdata += responsestr.substring(byteoffset);
        byteoffset = responsestr.length;
        while (1) {
                var x = newdata.indexOf("<s"+"cript>");
                if (x != -1) {
                        y = newdata.indexOf("</"+"script>", x);
                        if (y != -1) {
                                eval(newdata.substring((x+8),y));
                                if (typeof newdata == 'undefined') break;  // If message was eof() then we're now in a freed script
                                newdata = newdata.substring(y+9);
                        } else {

                                // Last message is incomplete.  Ignore it and it will be processed next time
                                break;
                        }
                } else {

                        // No more messages
                        break;
                }
        }
}

window.onload = function() {
        if (parent.Meteor.status == 4) startstream();
}
</script>
</body>
</html>
