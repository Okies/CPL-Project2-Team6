var express = require('express');
var router = express.Router();
var mysql = require('mysql');

var pool = mysql.createPool({
    connectionLimit: 5,
    host: 'localhost',
    user: 'testuser',
    database: 'testdb',
    password: 'computer'
});

/* GET home page. */
router.post('/', function(req, res) {
    pool.getConnection(function (err, connection) {
        // Use the connection
        console.log('connected as id ' + connection.threadId);

        var values = [req.body.ID, req.body.PW, req.body.Addr, req.body.Phone];

        var sql = "INSERT INTO member (ID, PW, Addr, Phone) VALUES (?)";

        connection.query(sql, [values], function (err, result) {
            if (err) console.error("err : " + err);
            console.log("result : " + JSON.stringify(result));

            res.json(result);

            connection.release();

            // Don't use the connection here, it has been returned to the pool.
        });
    });
});

module.exports = router;