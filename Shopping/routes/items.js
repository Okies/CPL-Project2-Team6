var express = require('express');
var router = express.Router();
var mysql = require('mysql');

//DB정보 팀장님 서버 정보로 바꾸면 됨
var pool = mysql.createPool({
    connectionLimit: 5,
    host: 'localhost',
    user: 'testuser',
    database: 'testdb',
    password: 'computer'
});

router.get('/', function(req, res, next) {
    pool.getConnection(function (err, connection) {
        // Use the connection
        console.log('connected as id ' + connection.threadId);

        connection.query('SELECT * FROM item', function (err, rows) {
            if (err) console.error("err : " + err);
            console.log("rows : " + JSON.stringify(rows));

            var json = new Object();
            json.result = rows;
            console.log("result : " + JSON.stringify(json));

            res.json(json);

            connection.release();

            // Don't use the connection here, it has been returned to the pool.
        });
    });
});

module.exports = router;
