const express = require("express");
const bodyParser = require("body-parser");
const port = process.env.PORT || 8080;
const Pool = require("pg").Pool;
const crypto = require("crypto");

const app = express();

var pool = new Pool(new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: true
}));

//Hashing the password
function hash(input, salt){
  var hashedString = crypto.pbkdf2Sync(input, salt, 10000, 512, 'sha512');
  return ["pbkdf2S", "10000", salt, hashedString.toString('hex')].join('$');
}

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

app.use("/todo", require("./todo"));

app.get("/", (req, res) => {
  res.send(":)");
});

app.get("/dl", (req, res) => {
  var file = __dirname + "/app/do_or_try.apk";
  res.download(file);
});


app.post("/login", (req, res) => {
  var { email, password } = req.body;
  pool.query(
    'select * from "user" where email=($1)',
    [email],
    (err, result) => {
      if (err) {
        console.log("Login " + err.toString());
        res.send("Something went wrong. Try again later");
      } else {
        if (result.rows.length != 0) {
          var actualPassword = result.rows[0].password;
          var salt = actualPassword.split('$')[2];
          var hashedPassword = hash(password,salt)
          if (hashedPassword == actualPassword) {
            res.send("success");
          } else {
            res.send("password wrong");
          }
        } else {
          res.send("user not found");
        }
      }
    }
  );
});

app.post("/signin", (req, res) => {
  var { email, password } = req.body;
  pool.query(
    'select * from "user" where email=($1)',
    [email],
    (err, result) => {
      if (err) {
        console.log("Sign in" + err.toString);
        res.send("Something is wrong. Try again later");
      } else {
        if (result.rows.length == 0) {
          var salt = crypto.randomBytes(128).toString('hex');
          var hashedPassword = hash(password,salt)
          pool.query(
            'insert into "user" values ($1, $2)',
            [email, hashedPassword],
            (err, result) => {
              if (err) {
                res.send("sign in error");
              } else {
                res.send("success");
              }
            }
          );
        } else {
          res.send("user already exists");
        }
      }
    }
  );
});

app.listen(port, function() {
  console.log("Connected to port " + port);
});