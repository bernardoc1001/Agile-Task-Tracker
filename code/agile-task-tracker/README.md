# Agile Task Tracker
The system, written in Clojure(Script), is a RESTful web-application
designed to assist in the tracking of tasks in an Agile Software Development
Environment, based on a SCRUM like approach.

See the technical specification in the technical manual for an
installation guide.


## Development Mode

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

## Production Build

```
lein clean
lein cljsbuild once min
```
