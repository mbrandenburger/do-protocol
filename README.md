do-protocol
===========

Simple DO protocol server, very similar to the HTTP protocol.


TODOs
===========

- Make some unit tests (done)
- Test on different machines (done)
- Test with different request messages (done)


Specification:
===========

   * The server and clients communicate using TCP.

   * All connections with the server are single-shot, that is, once server
   gets request and sends the response, it should close the connection.

   * Server expects the request in text format (as opposed to binary format).
   The structure of the request is as follows:

        1. Line terminator is character 0x0a (newline character in most
        of UNIX systems).

        2. The first line starts with literal text 'DO' (without quotes),
        followed by one or more spaces (character 0x20), then a string (called
        parameter) consisting of characters in range [a-zA-Z0-9/_-] (upper and
        lower english characters, slash, underscore, and a dash). The line ends
        with the line terminator.

        3. Subsequent lines represents the header. Each header line starts with
        a header name, that consists of string of characters in range
        [a-zA-Z0-9-] (upper and lower english characters and a dash). The header
        name is followed by literal ':', and one or more spaces, and a header
        value, that is in the same format as the header name. Each header line
        is terminated by the line terminator.

        4. Header ends with an empty line, that is a line consisting solely of a
        line terminator.

   * Server responds with literal text 'DONE' (without quotes), one or more
   spaces, followed by the MD5 hash of the parameter it received from the
   client, and a line terminator. The server may send header (in the same format
   as described for request). The response ends with an empty line.


Run server:
===========

    git clone https://github.com/mbrandenburger/do-protocol.git
    cd do-protocol
    mvn package
    java -jar target/do-protocol-server-1.0-SNAPSHOT-jar-with-dependencies.jar

* or use host and port parameter

    java -jar target/do-protocol-server-1.0-SNAPSHOT-jar-with-dependencies.jar --port=$port$ --host=$host$


Test using netcat:
===========

For simple testing using netcat: http://en.wikipedia.org/wiki/Netcat

    nc localhost 1337
    nc -c localhost 1337 for line terminator (\r\n) testing

Some example:

    [01:32 AM] do-protocol$ echo -ne "DO hallo\n\n" | nc localhost 1337
    DONE 911b33ba90678bee998f75bb510757d1

    [01:32 AM] do-protocol$ echo -ne "DO hallo\r\n" | nc localhost 1337
    BAD Invalid protocol exception

    [01:32 AM] do-protocol$ echo -ne "do hallo\n\n" | nc localhost 1337
    BAD Invalid protocol exception

    [01:33 AM] do-protocol$ echo -ne "DO hallo\nkey: value\n\n" | nc localhost 1337
    DONE 911b33ba90678bee998f75bb510757d1

    [01:36 AM] do-protocol$ echo -ne "" | nc localhost 1337

    [01:36 AM] do-protocol$ echo -ne "\n" | nc localhost 1337
    BAD Invalid protocol exception

    [01:36 AM] do-protocol$ echo -ne "\n\n" | nc localhost 1337
    BAD Invalid protocol exception

    [03:59 PM] do-protocol$ cat TooLongData | nc localhost 1337
    BAD TooLongFrameException

    [03:59 PM] do-protocol$ echo -n X | tr X '\0000' | nc localhost 1337
    [03:59 PM] do-protocol$

    [05:17 PM] do-protocol$ echo -ne "DO hallo\n\n" | nc cloud2.ibr.cs.tu-bs.de 8037
    DONE 911b33ba90678bee998f75bb510757d1

Stress test:

    Server runs on cloud2.ibr.cs.tu-bs.de:8037. The start_clients.sh script
    starts a lot of clients. After starting this script on three machines, the
    server slows down but still alive =)
    We can observe that simple request on a local machine needs more time to
    process.

    Starting some clients on three different machines:
    bin/start_clients.sh

    In that scenario, a request needs a little more time
    echo -ne "DO hallo\n\n" | nc cloud2.ibr.cs.tu-bs.de 8037

    See screen shot in bin/resources


Used frameworks
===========

- Netty http://static.netty.io/
    Well known framework for network applications. Deals easily with different
    error scenarios e.g. Client timeouts, maximum amount of data. It handles all
    connections in a given thread pool.

- Google guava-library https://code.google.com/p/guava-libraries/
    Provides several hashing functions such as MD5 or SHA-1.

- Apache Commons CLI http://commons.apache.org/cli/
    Simplifies command line argument parsing.

- Simple Logging Facade for Java http://www.slf4j.org/
    For flexible logging.

- JUnit http://junit.sourceforge.net/
    Used for testing regular expressions and for the protocol message object.



Resources
===========

http://www.ibr.cs.tu-bs.de/courses/ss12/vs/index.html
https://netty.io/Documentation/WebHome
http://static.netty.io/3.6/guide/
http://static.netty.io/3.6/api/
http://seeallhearall.blogspot.de/2012/05/netty-tutorial-part-1-introduction-to.html
http://static.netty.io/3.6/api/org/jboss/netty/handler/codec/frame/DelimiterBasedFrameDecoder.html
http://static.netty.io/3.6/api/org/jboss/netty/channel/ChannelFutureListener.html
http://static.netty.io/3.6/api/org/jboss/netty/handler/timeout/IdleStateAwareChannelHandler.html
http://static.netty.io/3.6/api/org/jboss/netty/channel/SimpleChannelHandler.html
http://static.netty.io/3.6/api/org/jboss/netty/handler/timeout/IdleStateHandler.html
http://static.netty.io/3.6/api/org/jboss/netty/bootstrap/ServerBootstrap.html
http://docs.oracle.com/javase/6/docs/api/java/lang/Runtime.html#addShutdownHook(java.lang.Thread)
http://commons.apache.org/cli/usage.html
http://www.oracle.com/technetwork/java/javase/documentation/index-137868.html
http://docs.oracle.com/javase/6/docs/api/java/util/Collections.html#unmodifiableMap(java.util.Map)
http://www.dzone.com/tutorials/java/log4j/sample-log4j-properties-file-configuration-1.html
http://www.vogella.com/articles/JavaRegularExpressions/article.html
http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html
http://www.regexplanet.com/advanced/java/index.html
https://code.google.com/p/guava-libraries/wiki/HashingExplained
http://docs.guava-libraries.googlecode.com/git-history/release/javadoc/com/google/common/util/concurrent/AbstractService.html
http://maven.apache.org/surefire/maven-surefire-plugin/examples/junit.html