#!/usr/bin/env node

if ((process.version.split('.')[1]|0) < 10) {
	console.log('Please, upgrade your node version to 0.10+');
	process.exit();
}

var net = require('net');
var util = require('util');
var crypto = require('crypto');
var readline = require('readline');

var options = {
	'port': 6969,
	'host': '54.83.207.90',
}

const SECRET_MESSAGE = 'PATATA';

var KEYPHRASE;
var data;
var clientDh, clientSecret, clientState = 0;
var serverDh, serverSecret, serverState = 0;

var socket = net.connect(options);

var rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout
});

rl.on('line', function(line) {
    KEYPHRASE = line;
});

socket.on('data', function(msg) {
	msg = msg.toString().trim().split(':');
	if (msg[0] == 'CLIENT->SERVER') {
		if (clientState == 0) {
			socket.write('hello?');
			clientState++;
		} else if (clientState == 1 && data[0] == 'hello!') {
			clientDh = crypto.createDiffieHellman(256);
			clientDh.generateKeys();
			socket.write(util.format('key|%s|%s\n', clientDh.getPrime('hex'), clientDh.getPublicKey('hex')));
			clientState++;
		} else if (clientState == 2 && data[0] == 'key') {
			clientSecret = clientDh.computeSecret(data[1], 'hex');
			var cipher = crypto.createCipheriv('aes-256-ecb', clientSecret, '');
			var keyphrase = cipher.update(KEYPHRASE, 'utf8', 'hex') + cipher.final('hex');
			socket.write(util.format('keyphrase|%s\n', keyphrase));
			clientState++;
		} else {
			console.log('Error');
			socket.end();
		}
	} else if (msg[0] == 'SERVER->CLIENT') {
		if (serverState == 0 && data[0] == 'hello?') {
			socket.write('hello!\n');
			serverState++;
		} else if (serverState == 1 && data[0] == 'key') {
			serverDh = crypto.createDiffieHellman(data[1], 'hex');
			serverDh.generateKeys();
			serverSecret = serverDh.computeSecret(data[2], 'hex');
			socket.write(util.format('key|%s\n', serverDh.getPublicKey('hex')));
			serverState++;
		} else if (serverState == 2 && data[0] == 'keyphrase') {
			var decipher = crypto.createDecipheriv('aes-256-ecb', serverSecret, '');
			var keyphrase = decipher.update(data[1], 'hex', 'utf8') + decipher.final('utf8');
			var cipher = crypto.createCipheriv('aes-256-ecb', serverSecret, '');
			var result = cipher.update(SECRET_MESSAGE, 'utf8', 'hex') + cipher.final('hex');
			socket.write(util.format('result|%s\n', result));
			socket.end();
			var decipher = crypto.createDecipheriv('aes-256-ecb', clientSecret, '');
			var message = decipher.update(msg[1].split('|')[1], 'hex', 'utf8') + decipher.final('utf8');
			console.log(message);
		} else {
			socket.write('Error\n');
			socket.end();
		}
	} else {
		console.log('Error');
		socket.end();
	}
	if (msg[1]) data = msg[1].split('|');
});
