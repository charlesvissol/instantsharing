//Ajax query status
var READY_STATE_UNINITIALIZED = 0;
var READY_STATE_LOADING = 1;
var READY_STATE_LOADED = 2;
var READY_STATE_INTERACTIVE = 3;
var READY_STATE_COMPLETE = 4;
var WAIT_STATE_MSG = '<span>Loading...</span>';
var WAIT_TIMEOUT = 1000;

/**
 * XMLHttpRequest initialization
 **/
function initXMLHttpRequest() {
    var xRequest = null;
    if (window.XMLHttpRequest) { //Firefox
        xRequest = new XMLHttpRequest();
    }
    if (window.ActiveXObject) { //Internet Explorer
        xRequest = new ActiveXObject("Microsoft.XMLHTTP");
    }
    return xRequest;
}


//Deprecated!
function dropHandler(ev) {

	  // Prevent default behavior (Prevent file from being opened)
	  ev.preventDefault();
	  

	  if (ev.dataTransfer.items) {
	    // Use DataTransferItemList interface to access the file(s)
	    for (var i = 0; i < ev.dataTransfer.items.length; i++) {
	      // If dropped items aren't files, reject them
	      if (ev.dataTransfer.items[i].kind === 'file') {
	        var file = ev.dataTransfer.items[i].getAsFile();
	        
	        var linode = document.createElement("li");//Item of list
	        //var filenamenode = document.createElement("span");//Filename
	        //var progressnode = document.createElement("span");//Progress
	        
	        linode.setAttribute("id", i);
	        //linode.innerHTML = file.name + ": 0%";
	        
	        //linode.appendChild(filenamenode);
	        //linode.appendChild(progressnode);
	        
	        var textfilenamenode = document.createTextNode(file.name);
	        
	        linode.appendChild(textfilenamenode);
	        //linode.appendChild(progressnode);
	        
	        
	        document.getElementById("filelist").appendChild(linode);//Add to list

	        
          var formData = new FormData();
          formData.append("file", file);

          var xhr = new XMLHttpRequest();
          
          xhr.upload.onprogress = function (event) {
          	var progress = Math.round(event.loaded / event.total * 100);
          	
          	//linode.innerHTML = linode.innerHTML + ":" + progress + "%";
          	//textfilenamenode.textContent = file.name + ":" + progress + "%"; 
          	
          	document.getElementById('"'+i+'"').innerHTML = file.name + ":" + progress + "%";
          	//document.getElementById(file.name).innerHTML = file.name + ":" + progress + "%";
          };
          

          /*
          xhr.onload = function (event) {
          	document.getElementById(file.name).innerHTML = file.name + ":100%";
          };*/
          
          
          xhr.open("POST", "Uploader", true); // If async=false, then you'll miss progress bar support.
          xhr.send(formData);
          //Follow process
	        
	        
	        
	      }
	    }
	  } else {
	    // Use DataTransfer interface to access the file(s)
	    for (var i = 0; i < ev.dataTransfer.files.length; i++) {
	    	
	        var node = document.createElement("li");
	        var textnode = document.createTextNode(ev.dataTransfer.files[i].name);
	        node.appendChild(textnode);
	        document.getElementById("filelist").appendChild(node);
	    	
	    	
	    	
	      //console.log('... file[' + i + '].name = ' + ev.dataTransfer.files[i].name);
	    }
	  }
}	

//Deprecated!
function dragOverHandler(ev) {
	  //console.log('File(s) in drop zone'); 

	  // Prevent default behavior (Prevent file from being opened)
	  ev.preventDefault();
}	

function sleep(milliseconds) {
	  var start = new Date().getTime();
	  for (var i = 0; i < 1e7; i++) {
	    if ((new Date().getTime() - start) > milliseconds){
	      break;
	    }
	  }
}


function selectItem(item,selection,input,list){
	var itemValue = item.textContent;
	var itemId = item.getAttribute("id");
	
	
	var selectedUser = document.createElement("li");
	
	selectedUser.setAttribute("id", itemId);
	selectedUser.setAttribute("class", "w3-display-container");
	selectedUser.setAttribute("style", "display: block;");
	selectedUser.textContent = itemValue;
	
	var deleteUser = document.createElement("span");
	deleteUser.setAttribute("class", "w3-button w3-display-right");
	//deleteUser.setAttribute("onclick", "this.parentElement.style.display='none'");
	deleteUser.setAttribute("onclick", "this.parentElement.remove()");
	deleteUser.innerHTML = "&times;";
	
	selectedUser.appendChild(deleteUser);
	
	document.getElementById(list).appendChild(selectedUser);

	document.getElementById(selection).innerHTML = "";
	document.getElementById(selection).style.display = "none";
	
	document.getElementById(input).value = "";
	
}


function getlistofusers(event,selection,input,list){

	var key = document.getElementById(input).value;
	
	
	try{
	
		//alert(event.keyCode);
		if(key.length == 0){
			document.getElementById(selection).style.display = "none";
			document.getElementById(selection).innerHTML = "";

		} else if(event.keyCode == 27){//ESC key
			
			document.getElementById(selection).style.display = "none";
			document.getElementById(selection).innerHTML = "";
		} else if(event.keyCode == 40){

			document.getElementById(selection).focus();
			var list = document.getElementById(selection).getElementsByTagName("li");
			
			list[0].className = "w3-display-container selected";
			
		} else {
			
			
			//selection

			document.getElementById(selection).style.display = "block";
			document.getElementById(selection).innerHTML = "";
			
			
		    var xmlReq = initXMLHttpRequest(); //initialize XMLHttpRequest
		    
		    if (xmlReq) {
		        xmlReq.onreadystatechange = function() {
		            var data;
		            if (xmlReq.readyState == READY_STATE_COMPLETE) { //Verify status of the query
		                data = xmlReq.responseText; //Read response
		                
		                document.getElementById(selection).innerHTML = data;
		        
		                var userList = document.getElementById(selection).getElementsByTagName("li");
		                
		                for(i = 0; i < userList.length; i++){
		                	userList[i].setAttribute("onclick", "selectItem(this,'"+selection+"','"+input+"','"+list+"')");
		                }
		                
		            } 
		            
		        };
		        
		        xmlReq.open("POST", "SearchUsers", true); //Call URL 
				xmlReq.setRequestHeader("Content-type","application/x-www-form-urlencoded;charset=UTF-8"); 
		        xmlReq.send("query=" + key); //Parameters
		    }
			
		}
	
	} catch(err){
		
	}		
}


function createlist(){
	

	var team = document.getElementById("recipients-new-list").innerHTML;
	var listname = document.getElementById("input-list-name").value;
	
	
	var uri = encodeURI("team=" + team + "&listname=" + listname);
	
    var xmlReq = initXMLHttpRequest(); //initialize XMLHttpRequest
    
    if (xmlReq) {
        xmlReq.onreadystatechange = function() {
            
            if (xmlReq.readyState == READY_STATE_COMPLETE) { //Verify status of the query
            	
                var data = xmlReq.responseText; //Read response
                
                //class="w3-panel w3-pale-blue w3-leftbar w3-rightbar w3-border-blue"
                //document.getElementById("msg-new-list-created").style.display = "block";
                document.getElementById("msg-new-list-created").innerHTML = data;
                
                //sleep(1000);
                
                //document.getElementById("msg-new-list-created").style.display = "none";
                
                document.getElementById("recipients-new-list").innerHTML = "";
                document.getElementById("input-list-name").value = "";
                
            } 
            
        };
        
        xmlReq.open("POST", "CreateList", true); //Call URL 
		xmlReq.setRequestHeader("Content-type","application/x-www-form-urlencoded;charset=UTF-8"); 
        xmlReq.send(uri);//"team=" + team + "&listname=" + listname); //Parameters
    }
	
	
	
}

function showPopup(content) {
  // Get the snackbar DIV
  var x = document.getElementById("snackbar");
  x.innerText = content;
  // Add the "show" class to DIV
  x.className = "show";

  // After 3 seconds, remove the show class from DIV
  setTimeout(function(){ x.className = x.className.replace("show", ""); }, 3000);
} 








