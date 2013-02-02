package protocol.server;

import org.apache.commons.cli.*;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 1/30/13
 * Time: 1:41 PM
 */
public class ServerLauncher {

    private static final String APP_NAME = "Simple Protocol Server";
    private static final int DEFAULT_PORT = 1337;
    private static final String DEFAULT_HOST_ADDR = "localhost";

    private static final org.slf4j.Logger log =
            LoggerFactory.getLogger(ServerLauncher.class);


    public static void main(String[] args) {

        int port = DEFAULT_PORT;
        String host = DEFAULT_HOST_ADDR;

        final CommandLine cmd;
        final CommandLineParser cmdLineParser = new GnuParser();
        final Options options = new Options();
        options.addOption("help", false, "print this message");
        options.addOption("host", true, "set servers host address");
        options.addOption("port", true, "set servers port");

        try {
            cmd = cmdLineParser.parse(options, args);

            if (cmd.hasOption("help")) {
                showUsage(options);
            }

            if (cmd.hasOption("host")) {
                host = cmd.getOptionValue("port");
            }

            if (cmd.hasOption("port")) {
                port = Integer.parseInt(cmd.getOptionValue("port"));
            }

        } catch (ParseException e) {
            showUsage(options);
        }

        // ProtocolServer could be replaced with generic server and easily
        // customized using dependency injection
        final ProtocolServer server = new ProtocolServer(APP_NAME, host, port);
        server.start();
        if (!server.isRunning()) {
            log.error("Server could not be started");
            System.exit(1);
        }
        Thread shutdownHook = new Thread() {
            @Override
            public void run() {
                server.stop();
                log.info("Done!");
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownHook);

    }

    static void showUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(APP_NAME, options);
        System.exit(1);
    }


}
