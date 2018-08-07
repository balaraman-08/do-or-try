const express = require("express");
const bodyParser = require("body-parser");
const port = process.env.PORT || 8080;
const Pool = require("pg").Pool;

const app = express();

var config = {
  user: "pxyelcidwntupx",
  database: "dd9evum7thmpu0",
  host: "ec2-54-225-76-201.compute-1.amazonaws.com",
  port: "5432",
  password: "ddb6afe9f49508b09c3e54ab8b48d553445cc13be65d6c4c5ea0b83ad29ce5d4"
};

var pool = new Pool(config);

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

app.use("/todo", require("./todo"));

app.get("/", (req, res) => {
  res.send(":)");
});

app.post("/login", (req, res) => {
  var { email, password } = req.body;
  pool.query('select * from "user" where email=($1)', [email], (err, result) => {
    if (err) {
      res.send(err.toString());
    } else {
      if (result.rows.length != 0) {
        if (password == result.rows[0].password) {
          res.send("success");
        } else {
          res.send("password wrong");
        }
      } else {
        pool.query(
          'insert into "user" values ($1, $2)',
          [email, password],
          (err, result) => {
            if (err) {
              res.send("Register error");
            } else {
              res.send("User created");
            }
          }
        );
      }
    }
  });
});

app.listen(port, function() {
  console.log("Connected to port " + port);
});
