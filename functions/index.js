var functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.addMarker = functions.https.onRequest((req, res) => {
  // Grab the text parameter.
  const lat = req.query.lat;
  const long = req.query.longtitude;
  // Push it into the Realtime Database then send a response
  admin.database().ref('/markers').push({
    latitude: lat,
    longtitude: long
  }).then(snapshot => {
    res.status(200).json({status : 'OK'});
  });
});

exports.getMarkers = functions.https.onRequest((req, res) =>{
  admin.database().ref("markers").once('value', function(snapshot){
      res.send(snapshot.val());
    });
  });
