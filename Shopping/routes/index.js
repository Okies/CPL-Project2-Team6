var express = require('express');
var router = express.Router();
var mysql = require('mysql');
var app = require('../app');

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
    {
        setInterval(function () {
            pool.getConnection(function (err, connection) {
                connection.query("Select * from cart where state = 1", function(err, result){
                    if (err) console.error("err : " + err);
                    app.c = result.length;
                    res.emit({alarm : app.c});
                });
                connection.release();
            });
        } ,3000);
        res.render('index', {alarm : app.c});
    }
});

module.exports = router;
