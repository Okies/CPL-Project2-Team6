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
    {
        pool.getConnection(function (err, connection) {
            // Use the connection
            console.log('connected as id ' + connection.threadId);

            connection.query('SELECT * FROM member where level = 1', function (err, result) {
                if (err) console.error("err : " + err);
                console.log("result : " + JSON.stringify(result));

                res.render('member_list', {rows : result});
                connection.release();

                // Don't use the connection here, it has been returned to the pool.
            });
        });
    }
});

module.exports = router;
