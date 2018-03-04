// Ubuntu 17.10 with node.js v6.11 works with Javascript6
// to run just execute: node gcloud-node.js
// gcloud tool with needed permissions is also in a GCE instance automatically

// Also can run gcloud inside a pod on Kubernetes, also works and has read access to gcr.io Docker registry
// kubectl run gcloud --image=google/cloud-sdk:alpine -it bash
// and adding nodejs of recent version v8.9 is easy too in Alpine Linux:
// apk add --update nodejs
var http = require('http');
var url = require('url');

console.log("listening http")
http.createServer(function (req, res) {

    let image = 'nethunt-site'
    let command = 'gcloud container images list-tags gcr.io/steel-binder-91509/'+image+' --format json'
    var exec = require('child_process').exec;
    var result = '';
    var child = exec(command);
    child.stdout.on('data', function(data) {
        result += data;
    });
    child.on('close', function() {
        //console.log('done');
//        console.log(result);
      var tags = JSON.parse(result)

      res.writeHead(200, {'Content-Type': 'text/html'});
      var q = url.parse(req.url, true).query;
      //var txt = "year and month query params:" + q.year + " " + q.month;
      var html = "<h1>Available images of: "+image+"</h1>"
      html += "<h5>Used command: "+command+"</h5>"
      for( let tag of tags ){
        html += "Image at "+tag.timestamp.datetime + " tags "+tag.tags+" <a href=''>Deploy to test</a> <a href=''>Deploy to prod</a><br/>"
      }
      res.end(html);

    });

}).listen(80);
