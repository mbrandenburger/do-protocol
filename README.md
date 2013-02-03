do-protocol
===========

Simple DO protocol server, very similar to the HTTP protocol.


TODOs
===========

- Test on different machines
- Test with different request messages


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
        lower english characters, backslash, underscore, and a dash). The line
        ends with the line terminator.

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
    java -jar target/do-protocol-server-1.0-SNAPSHOT

* or use host and port parameter

    java -jar target/do-protocol-server-1.0-SNAPSHOT --port=$port$ --host=$host$


Test using netcat:
===========

For simple testing using netcat.

    nc localhost 1337
    nc -c localhost 1337 for line terminator (\r\n) testing


Used frameworks
===========

- Netty http://static.netty.io/
- Google guava-library https://code.google.com/p/guava-libraries/
- Apache Commons CLI http://commons.apache.org/cli/
- Simple Logging Facade for Java http://www.slf4j.org/


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