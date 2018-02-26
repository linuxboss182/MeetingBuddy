/**
 * Created by jtgaulin on 2/18/18.
 */

var express = require('express');
var router = express.Router();

var db = require('../DB/DB');


//Test call
router.get('/', function(req, res, next) {
    res.send('respond with a resource');
});


//API calls
router.post('/newAccount', function(req, res, next) {
    var username = req.body.username;
    var password = req.body.password;
    var phoneNum = req.body.phoneNum;
    var firstName = req.body.firstName;
    var lastName = req.body.lastName;
    var schedule = req.body.schedule;

    //Check account is not already taken
    db.get("SELECT * FROM Account WHERE username = ?", [username], function(err,row){
        if(!err && !row){ //If no error and no username already exists
            //Insert account
            var id = null;
            var stmt = db.prepare("INSERT INTO Account VALUES (?,?,?,?,?,?,?)");
            stmt.run(id, username, password, phoneNum, firstName, lastName, schedule);
            res.json({"status": "success"});
        }else{
            res.json({"status": "Username Already Taken"});
        }
    });

});

router.post('/Login', function(req, res, next) {
    var username = req.body.username;
    var password = req.body.password;

    //Load account with given username and check for password
    db.get("SELECT * FROM Account WHERE username = ?", [username], function(err,row){
        if(err || !row){
            res.json({"status": "Username Not Found"});
        }else if(password === row.password) {
            req.session.isLoggedIn = true;
            req.session.accountID = row.accountID;
            res.json(Object.assign({"status": "success"}, row));
        }else{
            res.json({"status": "Incorrect Password"});
        }
    });
});


router.post('/Search', function(req, res, next) {
    var username = req.body.username;

    //Load account with given username and check for password
    db.all("SELECT username FROM Account WHERE username LIKE '%' || ? || '%' ", [username], function(err,rows){
        if(err){
            res.json({"status": "Error searching"});
        }else{
            res.json(rows);
        }
    });
});

router.post('/updateSchedule', requireLogin, function(req, res, next) {
    var accountID = req.session.accountID; //Logged in user

    var schedule = req.body.schedule;

    db.run("UPDATE Account SET schedule = ? WHERE accountID = ?", [schedule, accountID], function(err) {
        if(err){
            res.json({"status": "Account Not Found"});
        }else{
            res.json({"status": "success"});
        }
    });
});

router.post('/newMeeting', requireLogin, function(req, res, next) {
    var organizer = req.session.accountID; //Logged in user

    // var meetingID = req.body.meetingID; //Generated
    var time = req.body.time;
    var date = req.body.date;
    var place = req.body.place;
    var longitude = req.body.longitude;
    var latitude = req.body.latitude;
    var classSize = req.body.classSize;
    var attendance = req.body.attendance;
    var name = req.body.name;

    var aid = null;
    var mid = null;

    //Insert meeting
    var mstmt = db.prepare("INSERT INTO Meeting VALUES (?,?,?,?,?,?,?,?,?,?)", function(){
        db.get("SELECT max(meetingID) AS nextID FROM Meeting", function(err,row){

            mid = row.nextID;

            //Insert organizer attendance
            var astmt = db.prepare("INSERT INTO Attendance VALUES (?,?,?,?)");
            astmt.run(aid, organizer, mid, 'joined');


            //Insert other attendance
            for (var i = 0, len = attendance.length; i < len; i++) {
                //Find account ID for the username
                db.get("SELECT * FROM Account WHERE username = ?", [attendance[i]], function(err,row){
                    if(!err && row){ //If no error and username exists
                        var inviteID = row.accountID;
                        var aid = null;
                        var astmt = db.prepare("INSERT INTO Attendance VALUES (?,?,?,?)");
                        astmt.run(aid, inviteID, mid, 'invited');
                    }
                });

            }

        });
    });


    mstmt.run(mid, organizer, name, time, date, place, longitude, latitude, classSize, aid);
    res.json({"status": "success"});
});

router.post('/addAttendance', requireLogin, function(req, res, next) {
    var accountID = req.session.accountID; //Logged in user

    var meetingID = req.body.meetingID;
    var inviteUsername = red.body.username
//  var inviteID = req.body.accountID;

    //Find account ID for the username
    var inviteID;
    db.get("SELECT * FROM Account WHERE username = ?", [username], function(err,row){
        if(!err && row){ //If no error and username exists
            inviteID = row.accountID;
            var aid = null;
            var astmt = db.prepare("INSERT INTO Attendance VALUES (?,?,?,?)");
            astmt.run(aid, inviteID, meetingID, 'invited');

            res.json({"status": "success"});
        }else{
            res.json({"status": "Account with that username not found"});
        }
    });

});

router.post('/getSchedules', requireLogin, function(req, res, next) {
    var accountID = req.session.accountID; //Logged in user
    var attendance = req.body.attendance;

    var schedules = [];

    //Find other attendance
    for (var i = 0, len = attendance.length; i < len; i++) {
        //Find account ID for the username
        db.get("SELECT schedule FROM Account WHERE username = ?", [attendance[i]], function(err,row){
            if(!err && row){ //If no error and username exists
                schedule.add(row)
            }

            //Just finished the last select
            if(i == len-1){
                res.json(schedules)
            }
        });
    }
});

router.post('/updateAttendance', requireLogin, function(req, res, next) {
    var accountID = req.session.accountID; //Logged in user

    var meetingID = req.body.meetingID;
    var status = req.body.status;

    db.run("UPDATE Attendance SET status = ? WHERE accountID = ? AND meetingID = ?", [status, accountID, meetingID], function(err) {
        if(err){
            res.json({"status": "Account Not Found"});
        }else{
            res.json({"status": "success"});
        }
    });

});

//Gets
router.post('/getAttendance', function(req, res, next) {
    var accountID = req.session.accountID; //Logged in user

    var meetingID = req.body.meetingID;

    db.get("SELECT * FROM Attendance WHERE accountID = ? AND meetingID = ?", [accountID, meetingID], function (err, rows) {
        if(err){
            res.json({"status": "Error finding attendance"});
        }else{
            res.json(rows);
        }
    });
});

router.get('/getMeeting', function(req, res, next) {
    var accountID = req.session.accountID; //Logged in user

    var meetingID = req.body.meetingID;

    db.all("SELECT * FROM Meeting WHERE meetingID = ?", [meetingID], function (err, row) {
        if(err || !row){
            res.json({"status": "Error finding meeting"});
        }else{
            res.json(row);
        }
    });
});

router.post('/getMyMeetings', requireLogin, function(req, res, next) {
    var accountID = req.session.accountID; //Logged in user

    var meetingID = req.body.meetingID;

    db.all("SELECT * FROM Attendance, Meeting WHERE accountID = ? AND Attendance.meetingID = Meeting.meetingID", [accountID], function (err, rows) {
        if(err){
            res.json({"status": "Error finding attendance"});
        }else{
            res.json(rows);
        }
    });
});

router.post('/getAccount', function(req, res, next) {
//    var accountID = req.session.accountID; //Logged in user

    var accountID = req.body.accountID;

    db.get("SELECT * FROM Account WHERE accountID = ?", [accountID], function(err,row){
        if(err || !row){ //If no error or no account found
            res.json({"status": "Error finding Account"});
        }else{
            res.json(row);
        }
    });
});

router.post('/getSchedule', function(req, res, next) {
    var accountID = req.session.accountID; //Logged in user

    db.get("SELECT schedule FROM Account WHERE accountID = ?", [accountID], function(err,row){
        if(err || !row){ //If no error or no account found
            res.json({"status": "Error finding Account"});
        }else{
            res.json(Object.assign({"status": "success"}, row));
        }
    });

});

//Helpers
function requireLogin(req, res, next) {
    if (req.session && req.session.isLoggedIn)
        return next();
    else
        return res.sendStatus(401);
}

module.exports = router;
