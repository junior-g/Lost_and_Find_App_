const mongoose = require('mongoose');

const NoteSchema = mongoose.Schema({
    message: String,
    imgpath: String,
    day: String
}, {
    timestamps: true
});

module.exports = mongoose.model('Note', NoteSchema);
