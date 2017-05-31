var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var session = require('express-session');

/* 현재 구현 페이지 자바스크립트 */
var index = require('./routes/index');
var item_list = require('./routes/item_list');
var member_list = require('./routes/member_list');
var admin_list = require('./routes/admin_list');
var login = require('./routes/login');
var login_service = require('./routes/login_service');
var logout = require('./routes/logout');

/* 안드로이드 데이터 전송 자바스크립트 */
var items = require('./routes/items');
var insert = require('./routes/insert');
var search = require('./routes/search');
var security = require('./routes/security');
var cart = require('./routes/cart');
var pay = require('./routes/pay');

var join = require('./routes/join');
var join_service = require('./routes/join_service');

var app = express();

var c = 1;

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));
app.use(session({
    secret: 'secret',
    resave: false,
    saveUninitialized: true
}));

app.use('/', index);
app.use('/login', login);
app.use('/item_list', item_list);
app.use('/member_list', member_list);
app.use('/admin_list', admin_list);
app.use('/login_service', login_service);
app.use('/logout', logout);
app.use('/security', security);

app.use('/items', items);
app.use('/insert', insert);
app.use('/search', search);
app.use('/cart', cart);
app.use('/pay', pay);

app.use('/join', join);
app.use('/join_service', join_service);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;