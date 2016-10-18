	
	var vids = {};
	var connections = [];
	var priorityStream = null;
	
	var UpdatePage = function() {
	
		for (var key in vids) {
			if (!vids.hasOwnProperty(key)) continue;

			var vid = vids[key];
	
			if(vid.paused || vid.ended) {
				if(vid.childNodes[0].getAttribute("src") == "") {
					console.log("Forcing load from update...");
					vid.childNodes[0].setAttribute("src","http://"+vid.getAttribute("url"));
					vid.load();
					vid.setAttribute("delay",-1);
				} else if(vid.getAttribute("delay") == -1) {
					vid.setAttribute("delay",0);
				}
			} else {
				vid.style.display = "block";
				vid.setAttribute("delay",-1);
			}
			var c = parseInt(vid.getAttribute("delay"));
			if(c >= 0 && c < 60) {
				c += 1;
				if(c == 60) {
					vid.style.display = "none";
					vid.pause();
					vid.childNodes[0].setAttribute("src","");
					vid.load(); 
				}
				vid.setAttribute("delay",c);
			}
					
				
		}
		
		var time = (new Date()).getTime();
		
		for(var i = 0; i < connections.length; ++i) {
			if(connections[i].received + 15*1000 <= time) {
				var tmpSource = connections[i].sourceIP;
				connections.splice(i,1);
				if(vids.hasOwnProperty(tmpSource)) {
					var vid = vids[tmpSource];
					vid.removeChild(vid.childNodes[0]);
					vid.parentNode.removeChild(vid);
					
					delete vids[tmpSource];
				}
			}
		}
		
		CheckServers();
		console.log(JSON.stringify(connections));
		console.log(JSON.stringify(vids));
	}
	
	var GetMap = function() {
		if(connections.length > 0) {
			
			var str = "";
			var lat = 0;
			var lng = 0;
			
			var viewRad = 0.01;
			var viewAngle = 0.5235987755982988; // 60 degrees
			
			if(priorityStream != null) {
				var conn = connections.find(function(cnc) {return cnc.sourceIP == priorityStream;});
				lat = conn.latitude;
				lng = conn.longitude;
			}
			
			for(var i = 0; i < connections.length; ++i) {
			
				var aLat = (parseFloat(connections[i].latitude) + viewRad * (Math.cos(parseFloat(connections[i].viewDir) * 0.0174543293 + (viewAngle/2)))).toString();
				var aLng = (parseFloat(connections[i].longitude) + viewRad * (Math.sin(parseFloat(connections[i].viewDir) * 0.0174543293 + (viewAngle/2)))).toString();
				var bLat = (parseFloat(connections[i].latitude) + viewRad * (Math.cos(parseFloat(connections[i].viewDir) * 0.0174543293 - (viewAngle/2)))).toString();
				var bLng = (parseFloat(connections[i].longitude) + viewRad * (Math.sin(parseFloat(connections[i].viewDir) * 0.0174543293 - (viewAngle/2)))).toString();
			
				console.log("A: " +aLat + ", " + aLng + "  B: " + bLat + ", " + bLng);
			
				str += '&markers=color:' + (connections[i].sourceIP == priorityStream ? "red" : "blue") +
						'%7Clabel:' + String.fromCharCode(65 + i) +
						'%7C' + connections[i].latitude + ',' + connections[i].longitude;
						
				str += '&path=color:0x00000000%7Cweight:5%7Cfillcolor:0x00FF0044' +
						'%7C' + bLat + ',' + bLng +
						'%7C' + connections[i].latitude + ',' + connections[i].longitude +
						'%7C' + aLat + ',' + aLng +
						'%7C' + bLat + ',' + bLng;
						
				if(priorityStream == null) {
					lat += parseFloat(connections[i].latitude);
					lng += parseFloat(connections[i].longitude);
				}
			}
			
			if(priorityStream == null) {
					lat /= connections.length;
					lng /= connections.length;
			}
		
			str = 'https://maps.googleapis.com/maps/api/staticmap?' +
					'center=' + lat + ',' + lng +
					'&zoom=13&size=600x1000&maptype=roadmap' + str +
					'&key=AIzaSyBHPBBQZ7nW8MXEukpFI-51fGOrCTpFou0';
			
			return str;
		}
		
		return "";
	}
	
	function BuildStream(streamInfo) {
		var data = JSON.parse(streamInfo);
		
		for(var i = 0; i < data.length; ++i) {
			var tmpData = data[i];
			
			if(!vids.hasOwnProperty(tmpData.sourceIP)) {
				var vid = document.createElement("video");
				var vidSrc = document.createElement("source");
				
				vid.setAttribute("autoplay","autoplay");
				//vid.setAttribute("preload","none");
				vid.setAttribute("delay",-1);
				vid.setAttribute("id", tmpData.sourceIP);
				vid.setAttribute("url", tmpData.serverIP + ":" + tmpData.path);
				vid.setAttribute("class","streamObj");
				vidSrc.setAttribute("id","src"+i);
				vidSrc.setAttribute("src","http://" + tmpData.serverIP + ":" + tmpData.path);
				vidSrc.setAttribute("type","video/ogg");
				
				document.getElementById("streamContainer").appendChild(vid);
				vid.appendChild(vidSrc);
		
				vid.style.display = "none";
		
				vid.addEventListener("loadeddata", function (e) {
					console.log("Trying to load data...");
					this.style.display = "block";
					if(this.childNodes[0].getAttribute("src") == "") {
						this.childNodes[0].setAttribute("src","http://" + this.getAttribute("url"));
						this.load();
					} else {
						this.play();
					}
				});

				vid.addEventListener("stalled", function (e) {
					console.log("Stream stalled...");
					this.setAttribute("delay",0);
				});
		
				vid.onclick = function() {
					// prioritization logic
					var xhttp = new XMLHttpRequest({mozSystem: true});
					xhttp.timeout = 2500;
					xhttp.open("POST", "http://" + vid.getAttribute("id") + ":8080", true);
					xhttp.send();
					return xhttp;
				};
		
				vids[tmpData.sourceIP] = vid;	
				tmpData.received = (new Date()).getTime();
				connections.push(tmpData);
				
			} else {
				var vid = document.getElementById(tmpData.sourceIP);
				if(vid.getAttribute("url") != tmpData.serverIP + ":" + tmpData.path) {
					vid.setAttribute("url",tmpData.serverIP + ":" + tmpData.path);
					vid.pause();
					vid.childNodes[0].setAttribute("src","");
					vid.load();
				}
				var connInd = connections.findIndex(function(cnc) {return cnc.sourceIP == tmpData.sourceIP;});
				tmpData.received = (new Date()).getTime();
				connections[connInd] = tmpData;
			}
		}
	}
	
	function CheckServers() {
		var xhttp = new XMLHttpRequest({mozSystem: true});
		xhttp.onreadystatechange = function() {
			if (xhttp.readyState == 4 && xhttp.status == 200) {
				BuildStream(xhttp.responseText);
			}
		};
		xhttp.timeout = 5000;
		xhttp.open("POST", "http://192.168.100.20:6543/gps_coords", true);
		xhttp.send();

		var xhttp2 = new XMLHttpRequest({mozSystem: true});
		xhttp.onreadystatechange = function() {
			if (xhttp.readyState == 4 && xhttp.status == 200) {
				BuildStream(xhttp.responseText);
			}
		};
		var xhttp2 = new XMLHttpRequest({mozSystem: true});
		xhttp2.onreadystatechange = function() {
			if (xhttp2.readyState == 4 && xhttp2.status == 200) {
				BuildStream(xhttp2.responseText);
			}
		};
		xhttp2.timeout = 5000;
		xhttp2.open("POST", "http://192.168.100.21:6543/gps_coords", true);
		xhttp2.send();
	}
		
	window.setInterval(UpdatePage, 1000);
	
	window.setInterval(function() {
		document.getElementById("map").setAttribute("src",GetMap());
	}, 4000)