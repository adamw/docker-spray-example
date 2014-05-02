Docker + Spray example
======================

1. Get [Docker](http://www.docker.io)
2. Get [SBT](http://www.scala-sbt.org/)
3. Run `sbt assembly` - this will create a fat-jar with the server
4. Build the docker image: `docker build .`. Note the final image id.
5. Run a container basing on the image, remapping the ports: `docker run --rm -p 9090:8080 [image id]`
6. Enjoy! Example requests:

    curl "http://localhost:9090/hello"
    curl "http://localhost:9090/counter/c1"
    curl -XPOST "http://localhost:9090/counter/c1?amount=1234"

You can also get a ready image from the [Docker index](https://index.docker.io/u/adamw/spray-example/).