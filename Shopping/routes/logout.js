/**
 * Created by redge on 2017-05-11.
 */
var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res) {
    req.session.destroy(function () {
        req.session;
    });
    res.send('<script>alert("로그아웃 되었습니다!"); location.href="/"</script>');
});

module.exports = router;