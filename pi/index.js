var express = require('express');
var app = express();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var path = require('path');
var fs = require('fs');
var Gpio = require('onoff').Gpio,
  led = new Gpio(16, 'out'),
  button = new Gpio(21, 'in', 'both');

console.log('init camera');
var dslr;
var gphoto2 = require('gphoto2');
var GPhoto = new gphoto2.GPhoto2();
GPhoto.list(function (list) {
  console.log('list length ' + list.length);
  if (list.length === 0) return;
  dslr = list[0];
  console.log('Found camera :', dslr.model);
});

app.use('/stream', express.static(path.join(__dirname, 'stream')));
app.use('/static', express.static(path.join(__dirname, 'static')));

app.get('/', function(req, res) {
  res.sendFile(__dirname + '/index.html');
});

var sockets = {};
var isTakingPicture = false;

io.on('connection', function(socket) {

  sockets[socket.id] = socket;
  console.log("Total clients connected : ", Object.keys(sockets).length);

  socket.on('disconnect', function() {
    delete sockets[socket.id];
  });

  socket.on('start-takePictures', function() {
    countdown(3);
  });

});

http.listen(3000, function() {
  console.log('listening on *:3000');
});

function emitPicture(picturePath) {
  console.log('Emit picture');
  io.sockets.emit('picture', picturePath);
}

function reset() {
  console.log('emit reset ');
  io.sockets.emit('reset');
}

button.watch(function(err, value) {
  console.log('button ' + value)
  led.writeSync(value);
  if(value == 0 && !isTakingPicture) {
    isTakingPicture  = true;
    countdown(3);
  }
});

function countdown(count) {
  console.log('emit countDown ' + count);
  io.sockets.emit('countDown', count);
  if (count == 0) {
    takeDslrPhoto();
  } else {
    setTimeout(countdown, 1000, count -1);
  }
}

function takeDslrPhoto() {
  console.log('taking DSLR Picture...');
  // Take picture with camera object obtained from list()
  dslr.takePicture({download: true}, function (er, data) {
    console.log('took DSLR Picture...' + er);
    var pictureName = '/stream/' + createPictureName();
    fs.writeFileSync(__dirname + pictureName, data);
    console.log('wrote Picture...');
    emitPicture(pictureName);
    isTakingPicture = false;
  });
}

function createPictureName() {
  return 'picture_' + (new Date()).getTime() + '.jpg';
}
