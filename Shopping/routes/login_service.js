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

/* GET home page. */
router.post('/', function(req, res, next) {
    pool.getConnection(function (err, connection) {
        // Use the connection
        console.log('connected as id ' + connection.threadId);

        var values = [req.body.id, req.body.pw];

        var sql = "SELECT id, pw from user where ";
        sql += "id=" + req.body.id + " and pw=" + req.body.pw;
        console.log(sql);

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