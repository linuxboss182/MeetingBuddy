/**
 * Created by jtgaulin on 2/18/18.
 */

var express = require('express');
var path = require('path');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var session = require('express-session');


var api = require('./API/API');

//Register database
var DB = require('./DB/DB');
DB.init();

//Register express
var app = express();
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
// app.use(cookieParser());
app.use(session({
    secret: '7A11-4341-112341Ea',
    resave: true,
    saveUninitialized: true
}));
app.use(express.static(path.join(__dirname, 'public')));

//Register API calls
app.use('/', api);

//Catch 404 and forward to error handler
app.use(function(req, res, next) {
    var err = new Error('Not Found');
    err.status = 404;
    next(err);
});

//Error handler
app.use(function(err, req, res, next) {
    // set locals, only providing error in development
    res.locals.message = err.message;
    res.locals.error = req.app.get('env') === 'development' ? err : {};

    // render the error page
    res.status(err.status || 500);
    res.send('Error in node server: ' +  err.message);
});

//Start server
var server = app.listen(8000, function () {
    var host = "localhost";
    var port = server.address().port;

    console.log('App listening at http://%s:%s', host, port);
});
