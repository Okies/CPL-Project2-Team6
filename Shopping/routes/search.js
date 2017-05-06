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
    pool.getConnection(function (err, connection) {
        // Use the connection
        console.log('connected as id ' + connection.threadId);

        var data = req.query;

        for(var x in data ) {
            var sql = "SELECT * from item where ";
            sql += x + '=' + data[x];
            console.log(sql);
            connection.query(sql, function (err, result) {
                if (err) console.error("err : " + err);
                console.log("result : " + JSON.stringify(result));

                //res.render('index', {title: 'item test', rows: result});
                var json = new Object();
                json.result = result;
                console.log("result : " + JSON.stringify(json));

                res.json(json);

                connection.release();

                // Don't use the connection here, it has been returned to the pool.
            });
        }
    });

});

module.exports = router;