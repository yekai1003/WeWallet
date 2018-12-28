var app = require('../app');
var debug = require('debug')('letao:server');
var http = require('http');
var file=require("../module/file");
const schedule = require('node-schedule');

/**
 * Get port from environment and store in Express.
 */
const port = normalizePort(process.env.PORT || '80');
app.set('port', port);const server = http.createServer(app);const hostname = '0.0.0.0';
server.listen(port, hostname, () => {console.log(`Server running at http://${hostname}:${port}/`);});
server.on('error', onError);server.on('listening', onListening);
file.deleteAll("NodeJs/public/up/");
const scheduleCronstyle = ()=>{
    schedule.scheduleJob('* * 4 * * *',()=>{
        file.deleteAll("NodeJs/public/up/")
    }); 
}
scheduleCronstyle();
/**file.searchImg("./public/img/");
 * Normalize a port into a number, string, or false.
 */
function normalizePort(val) {
    var port = parseInt(val, 10);
    if (isNaN(port)) {
        // named pipe
        return val;
    }
    if (port >= 0) {
        // port number
        return port;
    }
    return false;
}
/**
 * Event listener for HTTP server "error" event.
 */
function onError(error) {
    if (error.syscall !== 'listen') {
        throw error;
    }
    var bind = typeof port === 'string'
        ? 'Pipe ' + port
        : 'Port ' + port;
    // handle specific listen errors with friendly messages
    switch (error.code) {
        case 'EACCES':
            console.error(bind + ' requires elevated privileges');
            process.exit(1);
            break;
        case 'EADDRINUSE':
            console.error(bind + ' is already in use');
            process.exit(1);
            break;
        default:
            throw error;
    }
}
function onListening() {
    var addr = server.address();
    var bind = typeof addr === 'string'
        ? 'pipe ' + addr
        : 'port ' + addr.port;
    debug('Listening on ' + bind);
}
