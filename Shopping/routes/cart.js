/**
 * Created by redge on 2017-05-28.
 */

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

router.get('/', function(req, res){
    pool.getConnection(function(err, connection){
        console.log('connected as id ' + connection.threadId);
        var data = req.query;
        var sql = "UPDATE cart SET state=" + data.state+  " WHERE number=" + data.number;
        console.log(sql);
        connection.query(sql, function(err, result){
            if(err) console.error("err : "  + err);
            connection.release();
        })
    });
});

module.exports = router;
