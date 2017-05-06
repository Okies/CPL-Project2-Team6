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

        var values = [req.body.id, req.body.name,req.body.price*1, req.body.count*1, req.body.place, req.body.category];

        var sql = "INSERT INTO item (id, name, price, count, place, category) VALUES (?)";

        connection.query(sql, [values], function (err, result) {
            if (err) console.error("err : " + err);
            console.log("rows : " + JSON.stringify(result));

            res.json(result);

            connection.release();

            // Don't use the connection here, it has been returned to the pool.
        });
    });
});

module.exports = router;