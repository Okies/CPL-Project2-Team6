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

router.post('/', function(req, res) {

    console.log(JSON.stringify(req.body).replace(/\\/g, ""));
    temp = JSON.stringify(req.body).replace(/\\/g, "");
    temp = temp.substr(2, temp.length-7);
    console.log(temp);
    var obj = JSON.parse(temp);
    //obj = {"item":[{"id":"144248129124149","name":"신라면","count":"1"},{"id":"080176252115111","name":"사과","count":"1"},{"id":"032064142124146","name":"참이슬","count":"1"},{"id":"016211130124061","name":"콜라","count":"1"}],"userID":"guest"}
    //console.log(obj.ids[0].id);

    // buy_list 업데이트
    pool.getConnection(function(err, connection){
        console.log('connected as id ' + connection.threadId);

        for(var i = 0 ; i < obj.items.length ; ++i)
        {
            var sql = "INSERT INTO buy_list(user_id, product_id, product_name, count) VALUES("
                + "\"" + obj.userID + "\","
                + "\"" + obj.items[i].id + "\","
                + "\"" + obj.items[i].name + "\","
                + obj.items[i].count + ")";
            //console.log(sql);
            connection.query(sql, function(err, result){
                if (err) console.error("err : " + err);
            });
        }

        connection.release();
    });

    // item 업데이트 -> Count 차감
    pool.getConnection(function(err, connection){
        console.log('connected as id ' + connection.threadId);

        for(var i = 0 ; i < obj.items.length ; ++i)
        {
            var sql = "UPDATE item SET count = count - " + obj.items[i].count + " WHERE id = \"" + obj.items[i].id + "\"";
            //console.log(sql);
            connection.query(sql, function(err, result){
                if (err) console.error("err : " + err);
            });
        }

        connection.release();
    });

    // 결제 완료 시 해당 회원 로그아웃
    pool.getConnection(function(err, connection){
        console.log('connected as id ' + connection.threadId);

        var sql = "UPDATE cart SET state = -1, member = \"\" WHERE member=" + "\"" + obj.userID + "\"";
        connection.query(sql, function(err, result){
            if (err) console.error("err : " + err);
            connection.release();
        });
    });
    res.send("결제되었습니다.");
});

module.exports = router;
