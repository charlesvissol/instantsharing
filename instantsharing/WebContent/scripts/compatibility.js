function ApplicationCacheEnabled(){
if (Modernizr.applicationcache) {
  return true;
} else {
  return false;
}
	
}

function jsonSupported(){
	if (Modernizr.json) {
	  return true;
	} else {
	  return false;
	}	
}

function svgSupported(){
	if (Modernizr.svg) {
	  return true;
	} else {
	  return false;
	}	
}

function websocketSupported(){
	if (Modernizr.websockets) {
	  return true;
	} else {
	  return false;
	}	
}

function xhr2Supported(){
	if (Modernizr.xhr2) {
	  return true;
	} else {
	  return false;
	}	
}

function sessionStorageEnabled(){
	if (Modernizr.sessionstorage) {
	  return true;
	} else {
	  return false;
	}	
}

function localStorageEnabled(){
	if (Modernizr.localstorage) {
	  return true;
	} else {
	  return false;
	}	
}

