import com.github.steveice10.mc.auth.service.AuthenticationService;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import com.sasha.reminecraft.ReMinecraft;
import com.sasha.reminecraft.api.RePluginLoader;
import com.sasha.reminecraft.client.ReClient;
import com.sasha.reminecraft.logging.ILogger;
import com.sasha.reminecraft.logging.LoggerBuilder;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.UnknownHostException;

public class ReconnectManager {

    private static ILogger logger = LoggerBuilder.buildProperLogger("ReconnectManager");

    public static void reconnect(){

        reconnect(0);
    }

    public static void disconnect(){


        setReMinecraftIsRelaunching(true);


        RePluginLoader.disablePlugins();
        if (ReMinecraft.INSTANCE.minecraftClient != null && ReMinecraft.INSTANCE.minecraftClient.getSession().isConnected()) {
            ReMinecraft.INSTANCE.minecraftClient.getSession().disconnect("RE:Minecraft is restarting!");
        }
        logger.log("disconnected");
    }

    public static void setReMinecraftIsRelaunching(boolean isRelaunching){
        try {
            Class reflectedClass = ReMinecraft.class;

            Field f = reflectedClass.getDeclaredField("isRelaunching");
            f.setAccessible(true);
            f.set(ReMinecraft.INSTANCE, isRelaunching);
            logger.log("injected ReMinecraft/isRelaunching");

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public static void connect(){
        ReMinecraft thi = ReMinecraft.INSTANCE;

        setReMinecraftIsRelaunching(false);

        Proxy proxy = Proxy.NO_PROXY;
        if (thi.MAIN_CONFIG.var_socksProxy != null && !thi.MAIN_CONFIG.var_socksProxy.equalsIgnoreCase("[no default]") && thi.MAIN_CONFIG.var_socksPort != -1) {
            try {
                proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(InetAddress.getByName(thi.MAIN_CONFIG.var_socksProxy), thi.MAIN_CONFIG.var_socksPort));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }



        if (!thi.canPing()) {
            thi.reLaunch();
            return;
        }

        AuthenticationService service = thi.authenticate(thi.MAIN_CONFIG.var_authWithoutProxy ? Proxy.NO_PROXY : proxy);
        if (service != null) {
            thi.minecraftClient = new Client(thi.MAIN_CONFIG.var_remoteServerIp, thi.MAIN_CONFIG.var_remoteServerPort, thi.protocol, new TcpSessionFactory(proxy));
            thi.minecraftClient.getSession().addListener(new ReClient());
            logger.log("Connecting...");
            thi.minecraftClient.getSession().connect(true);
            logger.log("Connected!");
            RePluginLoader.enablePlugins();
        }
    }


    public static void reconnect(long millis){
        // TODO make disconnect and connect usable (can be used but the local server still disconnects and then a bug occurs when you join it)
        restart();
        /*disconnect();
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        connect();*/
    }


    public static void restart(){
        ReMinecraft.INSTANCE.reLaunch();
    }

    public static void shutDown(){
        System.exit(0);
    }

}
