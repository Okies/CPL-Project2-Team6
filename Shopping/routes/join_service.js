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
router.post('/', function(req, res) {
    pool.getConnection(function (err, connection) {
        // Use the connection
        console.log('connected as id ' + connection.threadId);
        console.log(req.body.ID);
        console.log(req.body.PW);
        //console.log(req.body.Addr);
        //console.log(req.body.Phone);
        var values = [req.body.ID, req.body.PW];

        var sql = "INSERT INTO member (ID, PW) VALUES (?)";

        connection.query(sql, [values], function (err, result) {
            if (err) console.error("err : " + err);
            console.log("result : " + JSON.stringify(result));

            //res.json(result);

            connection.release();
            res.send("OK");
            // Don't use the connection here, it has been returned to the pool.
        });
    });
});

module.exports = router;