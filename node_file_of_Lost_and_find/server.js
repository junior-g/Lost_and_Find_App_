const express = require('express');
const bodyParser = require('body-parser');


// create express app
const app = express();

// parse requests of content-type - application/x-www-form-urlencoded
app.use(bodyParser.urlencoded({ extended: true }))

// parse requests of content-type - application/json
app.use(bodyParser.json())

// Configuring the database
const dbConfig = require('./config/database.config.js');
const mongoose = require('mongoose');

mongoose.Promise = global.Promise;

// Connecting to the database
mongoose.connect(dbConfig.url)
.then(() => {
    console.log("Successfully connected to the database");
}).catch(err => {
    console.log('Could not connect to the database. Exiting now...');
    process.exit();
});

// define a simple route
app.get('/', (req, res) => {
    res.json({"message": "Welcome "});
});

// Require Notes routes
require('./app/routes/note.routes.js')(app);


//upload image

const router = express.Router()


const multer = require('multer')
const fileType = require('file-type')
const fs = require('fs')

const upload = multer({
    dest:'images/',
    limits: {fileSize: 10000000, files: 1},
    fileFilter:  (req, file, callback) => {

        if (!file.originalname.match(/\.(jpg|jpeg)$/)) {

            return callback(new Error('Only Images are allowed !'), false)
        }

        callback(null, true);
    }
}).single('image')
const Note = require('./app/models/note.model.js');
router.post('/images/upload', (req, res) => {

    upload(req, res, function (err) {

        if (err) {

            res.status(400).json({message: err.message})

        } else {

            let path = `${req.file.filename}`
            res.status(200).json({message: 'Image Uploaded Successfully !', path: path})
            var date = new Date();
            // Create a Note
            const note = new Note({
                message: "69Image",
                imgpath: path,
                day: date.toLocaleString(undefined, {
              weekday: 'long'
            })
            });

            // Save Note in the database
            note.save();
        }
    })
})

router.get('/images/:imagename', (req, res) => {

    let imagename = req.params.imagename
    let imagepath = __dirname + "/images/" + imagename
    let image = fs.readFileSync(imagepath)
    let mime = fileType(image).mime


	res.writeHead(200, {'Content-Type': mime })
	res.end(image, 'binary')
})

router.delete('/images/:imagename', (req, res)=>{
  let imagename = req.params.imagename
  let imagepath = __dirname + "/images/" + imagename
  fs.unlink(imagepath, (err) => {
          if (err) return err;
      })
      let mime = fileType(image).mime
      res.writeHead(200, {'Content-Type': mime })
      res.send("Deleted successfully");
})

app.use('/', router)

app.use((err, req, res, next) => {

    if (err.code == 'ENOENT') {

        res.status(404).json({message: 'Image Not Found !'})

    } else {

        res.status(500).json({message:err.message})
    }
})

// listen for requests
app.listen(3000, () => {
    console.log("Server is listening on port 3000");
});
