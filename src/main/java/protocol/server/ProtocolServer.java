package protocol.server;

import com.google.common.util.concurrent.AbstractService;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 1/30/13
 * Time: 1:41 PM
 */
public class ProtocolServer extends AbstractService {

    private static final org.slf4j.Logger log =
            LoggerFactory.getLogger(ProtocolServer.class);

    private Timer channelIdelTimer = new HashedWheelTimer();

    private ChannelGroup allChannels;
    private ChannelFactory channelFactory;

    private ChannelPipelineFactory channelPipelineFactory =
            new ProtocolServerPipelineFactory(channelIdelTimer);

    private String serverName;
    private String host;
    private int port;

    public ProtocolServer(String serverName, String host, int port) {
        // TODO validate arguments
        this.serverName = serverName;
        this.host = host;
        this.port = port;
    }

    @Override
    protected void doStart() {

        try {
            log.info("Starting " + this.serverName);
            this.allChannels = new DefaultChannelGroup(this.serverName);
            this.channelFactory = new NioServerSocketChannelFactory(
                    Executors.newCachedThreadPool(),
                    Executors.newCachedThreadPool());

            ServerBootstrap bootstrap =
                    new ServerBootstrap(this.channelFactory);
            bootstrap.setPipelineFactory(this.channelPipelineFactory);
            bootstrap.setOption("child.keepAlive", false);
            bootstrap.setOption("connectTimeoutMillis", 5000);

            Channel channel =
                    bootstrap.bind(new InetSocketAddress(this.host, this.port));
            this.allChannels.add(channel);

            notifyStarted();
            log.info("Listening on " + port + " ...");

        } catch (Exception e) {
            notifyFailed(e);
            log.error(e.getMessage());
        }

    }

    @Override
    protected void doStop() {
        log.info("Shutting down " + this.serverName);
        if (this.allChannels != null) {
            ChannelGroupFuture future = allChannels.close();
            future.awaitUninterruptibly();
        }
        if (this.channelFactory != null) {
            this.channelFactory.releaseExternalResources();
        }
        if (this.channelIdelTimer != null) {
            this.channelIdelTimer.stop();
        }
        notifyStopped();
    }


}
