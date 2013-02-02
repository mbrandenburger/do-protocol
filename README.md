do-protocol
===========

Simple DO protocol server, very similar to the HTTP protocol.


TODOs
===========

    * Add documentation
    * Make some unit tests
    * Test on different machines


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