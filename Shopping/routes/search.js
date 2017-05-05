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
router.get('/', function(req, res, next) {
    pool.getConnection(function (err, connection) {
        // Use the connection
        console.log('connected as id ' + connection.threadId);

        var data = req.query;
        //주소창에 search?colunm=value 하면 where colunm=value에 해당하는 값을 찾음
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