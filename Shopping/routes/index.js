var express = require('express');
var router = express.Router();
var mysql = require('mysql');

var pool = mysql.createPool({
    connectionLimit: 5,
    host: '222.104.202.90',
    user: 'Shopping_Admin',
    database: 'Shopping',
    password: '1234'
});

/* GET home page. */
router.get('/', function(req, res) {
    if(req.session.user_id == null)
        res.render('login');
    else
        res.render('index');
});

module.exports = router;
