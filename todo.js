var express = require('express')
const bodyParser = require("body-parser");
var todo = express.Router();
const Pool = require("pg").Pool;

const app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

var config = {
  user: "postgres",
  database: "do-or-try",
  host: "localhost",
  port: "5433",
  password: "1234"
};

var pool = new Pool(config);


todo.post('/list', (req, res) =>{
  var id = req.body.username;
  pool.query('select * from list where username=($1)', [id], (err, result) => {
    if (err){
      res.send("Select " + err.toString())
    }
    else if (result.rows.length == 0){
      res.send("No list")
    } else {
      res.send(result.rows)
    }
  })
})

todo.post('/add', (req, res) => {
  var {username, title, desc} = req.body;
  var time = Date.now();
  pool.query('insert into list values($1,$2,$3,$4,$5,$6)', [username, title, desc, 0, 0, time], (err, result) => {
    if (err){
      res.send("Insert " + err.toString())
    }
    else {
      res.send("Success")
    }
  })
})

todo.put('/edit', (req, res) => {
  var {username, title, desc, upvote, downvote} = req.body;
  pool.query('select * from list where username=($1) and title=($2)', [username, title], (err, result) => {
    if (err){
      res.send("Select " + err.toString())
    }
    else {
      if (result.rows.length == 0){
        res.send("No records found")
      } else {
        pool.query('update list set upvote=($3), downvote=($4) where username=($1) and title=($2)', [username, title, upvote, downvote], (err, result) => {
          if (err) {
            res.send("Updation err" + err)
          } else {
            res.send(result.toString())
          }
        })
      }
    }
  })
})

todo.delete('/delete', (req, res) => {
  var {username, title} = req.body;
  console.log(username + " " + title);
  pool.query('delete from list where username=($1) and title=($2)', [username, title], (err, result) => {
    if (err){
      res.send("Select " + err.toString())
    }
    else {
      res.send("success")
    }
  })
})

module.exports = todo;