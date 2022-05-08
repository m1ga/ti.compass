/* add
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
*/
const win = Ti.UI.createWindow();
const compass = require("ti.compass");
const compassStage = compass.createStage({
	backgroundColor: "red"
});

win.addEventListener("open", e => {
	Ti.Geolocation.requestLocationPermissions(Ti.Geolocation.AUTHORIZATION_WHEN_IN_USE, function(e) {
		if (e.success) {
			run();
		}
	});

	compassStage.createInfoBox({
		text: "test"
	});
})

win.add(compassStage);

function run() {
	compassStage.init();
}
win.open();
