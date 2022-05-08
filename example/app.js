/* add
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
*/
const win = Ti.UI.createWindow();
const compass = require("ti.compass");

win.addEventListener("open", e => {
	Ti.Geolocation.requestLocationPermissions(Ti.Geolocation.AUTHORIZATION_WHEN_IN_USE, function(e) {
		if (e.success) {
			run();
		}
	});
})


function run() {
	compass.init();
}
win.open();
