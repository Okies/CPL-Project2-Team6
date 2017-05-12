/**
 * Created by USER on 2017-05-12.
 */
var express = require('express');
var router = express.Router();

router.get('/', function(req, res){
    res.render('security');
});

module.exports = router;