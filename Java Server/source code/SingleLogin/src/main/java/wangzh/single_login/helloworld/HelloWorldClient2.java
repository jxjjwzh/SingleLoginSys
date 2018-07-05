//package wangzh.single_login.helloworld;
//
//import io.grpc.ManagedChannel;
//import io.grpc.ManagedChannelBuilder;
//import io.grpc.StatusRuntimeException;
//import io.grpc.examples.helloworld.GreeterGrpc;
//import io.grpc.examples.helloworld.HelloReply;
//import io.grpc.examples.helloworld.HelloRequest;
//import single_login.android.wangzh.proto.InParam;
//import single_login.android.wangzh.proto.OutParam;
//import single_login.android.wangzh.proto.SingleLoginGrpc;
//
//import java.util.concurrent.TimeUnit;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// * 相关注释待本类代码基本稳定后完善，敬请期待！
// * <p>
// * Announcements：
// *
// * @author 王志鸿
// * @corporation Thinkive
// * @date 2018/7/3
// */
//public class HelloWorldClient2 {
//
//    private static final Logger logger = Logger.getLogger(HelloWorldClient.class.getName());
//
//    private final ManagedChannel channel;
//    private final SingleLoginGrpc.SingleLoginBlockingStub blockingStub;
//
//    public HelloWorldClient2(String host, int port) {
//        this.channel = ManagedChannelBuilder.forAddress(host, port)
//                .usePlaintext()
//                .build();
//    }
//
//    /**
//     * Say hello to server.
//     */
//    public void greet(String name) {
//        logger.info("Will try to greet " + name + " ...");
//        InParam request = InParam.newBuilder().setFuncNo(100).build();
//        OutParam response;
//        try {
//            response = blockingStub.serviceFunc(request);
//        } catch (StatusRuntimeException e) {
//            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
//            return;
//        }
//        logger.info("Greeting: " + response.());
//    }
//
//    public void shutdown() throws InterruptedException {
//        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
//    }
//
//    /**
//     * Greet server. If provided, the first element of {@code args} is the name to use in the
//     * greeting.
//     */
//    public static void main(String[] args) throws Exception {
//        HelloWorldClient2 client = new HelloWorldClient2("localhost", 50051);
//        try {
//      /* Access a service running on the local machine on port 50051 */
//            String user = "world 2222";
//            if (args.length > 0) {
//                user = args[0]; /* Use the arg as the name to greet if provided */
//            }
//            client.greet(user);
//        } finally {
//            client.shutdown();
//        }
//    }
//}
