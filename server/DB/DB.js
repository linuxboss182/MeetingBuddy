/**
 * Created by jtgaulin on 2/18/18.
 */

var sqlite3 = require('sqlite3').verbose();
var db = new sqlite3.Database('./database.db');


db.init = function(){
    console.log("Loading DB");

//    db.run("CREATE TABLE IF NOT EXISTS lorem (info TEXT)", db.insertRows);

    //Tables
    db.run("CREATE TABLE IF NOT EXISTS Account (" +
                                                "accountID INTEGER PRIMARY KEY, " +
                                                "username TEXT, " +
                                                "password TEXT, " +
                                                "phoneNum TEXT, " +
                                                "firstName TEXT, " +
                                                "lastName TEXT," +
                                                "schedule TEXT" +
                                                ")");
    db.run("CREATE TABLE IF NOT EXISTS Meeting (" +
                                                "meetingID INTEGER PRIMARY KEY, " +
                                                "organizer INTEGER, " +
                                                "time TEXT, " +
                                                "date TEXT, " +
                                                "place TEXT, " +
                                                "longitude REAL, " +
                                                "latitude REAL, " +
                                                "classSize INTEGER, " +
                                                "attendance INTEGER," +
                                                "FOREIGN KEY(attendance) REFERENCES Attendance(aid)" +
                                                "FOREIGN KEY(organizer) REFERENCES Account(accountID)" +
                                                ")");


    //Relation
    db.run("CREATE TABLE IF NOT EXISTS Attendance (" +
                                                    "aid INTEGER PRIMARY KEY, " +
                                                    "accountID INTEGER, " +
                                                    "meetingID INTEGER," +
                                                    "status TEXT," +
                                                    "FOREIGN KEY(accountID) REFERENCES Account(accountID)," +
                                                    "FOREIGN KEY(meetingID) REFERENCES Meeting(meetingID)" +
                                                    ")");
};

db.insertRows = function(){
    console.log("insertRows Ipsum i");
    var stmt = db.prepare("INSERT INTO lorem VALUES (?)");

    for (var i = 0; i < 10; i++) {
        stmt.run("Ipsum " + i);
    }

    stmt.finalize(db.readAllRows);
};

db.readAllRows = function(){
    console.log("readAllRows lorem");
    db.all("SELECT rowid AS id, info FROM lorem", function(err, rows) {
        rows.forEach(function (row) {
            console.log(row.id + ": " + row.info);
        });
    });
};



module.exports = db;