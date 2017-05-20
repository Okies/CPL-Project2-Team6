var express = require('express');
var router = express.Router();
var mysql=require('mysql');

var pool=mysql.createPool({
    connectionLimit: 5,
    host: '222.104.202.90',
    user: 'Shopping_Admin',
    database: 'Shopping',
    password: '1234'
});

router.post('/', function(req, res){
    pool.getConnection(function (err,connection) {
        console.log(req.body.Tag);
        console.log(req.body.Name);
        console.log(req.body.Price);
        console.log(req.body.Amount);
        console.log(req.body.Position);
        console.log(req.body.Category);
        var values= [req.body.Tag, req.body.Name, req.body.Price, req.body.Amount, req.body.Position, req.body.Category];
        var sql= "INSERT INTO item (id, name, price, count, place, category) VALUES (?)";
        connection.query(sql, [values], function (err, result) {
            if (err) console.error("err : " + err);
            console.log("result : " + JSON.stringify(result));

          //  res.json(result);

            connection.release();


            res.send('<script>alert("추가 완료!"); location.href="/item_list"</script>');

        });
    });

});

module.exports = router;