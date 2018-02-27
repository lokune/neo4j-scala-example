This project is based on the Lightbend's Akka Http toolkit.

This repo was specifically created from my company repo I was working on to help fix gremlin-scala issues I was having. Company specific code was removed.

See https://github.com/mpollmeier/gremlin-scala/issues/233

`make test` from the project root to run tests.

`make run-local` from the project root to run the application locally. `sbt` and `docker` MUST be installed on your box.

Once the service is running locally, you can access the installed Neo4j server browser at http://localhost:7474/browser

