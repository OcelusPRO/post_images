const express = require('express');
const router = express.Router();
const bdd = require('../knex')
const fs = require('fs');
const path = require('path');
const dns = require('dns');
const config = require('./../config.json')

let map = new Map;
router.post(/\/(\w+)?/, async function(req, res, next){
    let credentials = req.header("user")!=="" && req.header("password")!=="";
    if (!credentials && (!req.session.userid || !req.session.password) && (!req.body.user || !req.body.password)){
        res.status(403);
        return res.json({error:"403", message:"Forbidden"});
    }
    let login = req.header("user")!==""?req.header('user'):undefined;
    if (!login)login = req.session.userid || req.body.user[0]
    let password = req.header("password")!==""?req.header('password'):undefined;
    if (!password)password = req.session.password || req.body.user[1]
    const base = await bdd('staff').select().where('staff_id', login);
    if (!base[0] || base[0].password !== password){
        res.status(403);
        return res.json({error:"403", message:"Forbidden"});
    }
    req.session.baseId = base[0].id;
    req.session.userid = login;
    req.session.password = password;
    let adress = req.headers['x-forwarded-for'] || req.connection.remoteAddress;
    map.set(adress, req.session.baseId)
    next();
})


/* GET home page. */
router.get('/', async function(req, res, next) {
    res.render('index', {title:config.name, login:req.session.userid});
});

router.get(/\/i\/\w+/, async function(req, res, next) {
    let image = req.url.replace('/i/','');

    let ip = req.headers['x-forwarded-for'] || req.connection.remoteAddress;
    dns.reverse(ip, async (err, hostnames) => {
        if (req.header('USER-AGENT').toLowerCase().includes('discordbot') || hostnames?hostnames[0].includes('google'):false)return res.sendFile(path.join(__dirname, '../uploads/' + image))
        let staffId = image.split('.')[0].split('_')[0]
        let timestamp = image.split('.')[0].split('_')[1];
        let date = new Date(Number.parseInt(timestamp))
        let dateToSend = ('0' + date.getDate()).slice(-2) + '/' + ('0'+(date.getMonth()+1)).slice(-2) + '/' + date.getFullYear() + ' - ' + ('0' + date.getHours()).slice(-2) + 'h' + ('0' + date.getMinutes()).slice(-2)
        let staffObj = {name:"N/A",id:"N/A",grade:"N/A",statut:"N/A"}
        let base = await bdd('staff').select().where('id', staffId);
        if (base[0]){
            staffObj.name = base[0].pseudo;
            staffObj.id = base[0].staff_id;
            staffObj.grade = base[0].rank_name;
            staffObj.statut = base[0].service;
        }
        return res.render('viewer', {title : config.name, image:'/' + image, staff:staffObj, date:date?dateToSend:"N/A", fileName:image })
    })
});


router.get(/\w+/, function(req, res, next) {
    let image = req.url.replace('/','');
    fs.readdir("./uploads/", (error, f) =>
    {
        if(error) console.log(error);
        let images = f.filter(f => f===image);
        if (!images[0])return next();
        res.status(200);
        return res.sendFile( path.join(__dirname, '../uploads/' + images[0]))
    });
});


router.post('/', async function(req, res, next){
    res.render('index', {title:config.name, login: req.session.userid});
});

router.post('/upload', async function(req, res){
    if (!req.files){
        res.status(400)
        return res.send({
            status: 400,
            message: 'No file uploaded'
        });
    }
    let file = req.files.image;
    let baseid = req.headers['x-forwarded-for'] || req.connection.remoteAddress;
    let id = req.session.baseId || map.get(baseid)
    let fileType = file.name.split('.').pop();
    let fileName =id  + '_' + Date.now() + '.' + fileType
    file.mv('./uploads/' + fileName);
    if (!req.header('nav'))return res.send(req.protocol + '://' + req.get('host') + '/i/' + fileName)
    res.redirect(req.protocol + '://' + req.get('host') + '/i/' + fileName);
});

module.exports = router;


