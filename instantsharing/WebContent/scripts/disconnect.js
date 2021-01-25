
function disconnect(url){
	var userLang = navigator.language || navigator.userLanguage; 
	window.location.href = "Welcome?lang=" + userLang + "&fromurl=" + url;//Go to Welcome Servlet
}