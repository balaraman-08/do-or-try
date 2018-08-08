var express = require("express");
const bodyParser = require("body-parser");
var todo = express.Router();
const Pool = require("pg").Pool;

const app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

var config = {
  user: "pxyelcidwntupx",
  database: "dd9evum7thmpu0",
  host: "ec2-54-225-76-201.compute-1.amazonaws.com",
  port: "5432",
  password: "ddb6afe9f49508b09c3e54ab8b48d553445cc13be65d6c4c5ea0b83ad29ce5d4"
};

var pool = new Pool(config);

todo.post("/list", (req, res) => {
  var email = req.body.email;
  pool.query("select * from list where email=($1)", [email], (err, result) => {
    if (err) {
      res.send("Select " + err.toString());
    } else if (result.rows.length == 0) {
      res.send("No list");
    } else {
      res.send(result.rows);
    }
  });
});

todo.post("/add", (req, res) => {
  var { email, title, desc } = req.body;
  pool.query(
    "insert into list (email,title,description,upvote,downvote) values($1,$2,$3,$4,$5,$6)",
    [email, title, desc, 0, 0],
    (err, result) => {
      if (err) {
        res.send("Insert " + err.toString());
      } else {
        res.send("Success");
      }
    }
  );
});

todo.put("/edit", (req, res) => {
  var { id, upvote, downvote } = req.body;
  pool.query(
    "update list set upvote=($2), downvote=($3) where id=($1)",
    [id, upvote, downvote],
    (err, result) => {
      if (err) {
        res.send("Updation err" + err);
      } else {
        res.send(result.toString());
      }
    }
  );
});

todo.post("/delete", (req, res) => {
  var { id } = req.body;
  pool.query(
    "delete from list where id=($1)",
    [id],
    (err, result) => {
      if (err) {
        res.send("delete " + err.toString());
      } else {
        res.send("success");
      }
    }
  );
});

module.exports = todo;
