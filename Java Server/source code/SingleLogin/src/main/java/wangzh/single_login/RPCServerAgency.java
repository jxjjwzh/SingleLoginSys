package wangzh.single_login;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import single_login.android.wangzh.proto.InParam;
import single_login.android.wangzh.proto.OutParam;
import single_login.android.wangzh.proto.SingleLoginGrpc;
import wangzh.single_login.bean.ResultVo;
import wangzh.single_login.functions.IFunction;
import wangzh.single_login.functions.FunctionFactory;
import wangzh.single_login.dao.DaoFactory;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 前端接入代理类、服务端程序入口
 * Announcements：
 *
 * @author 王志鸿
 * @corporation Thinkive
 * @date 2018/6/29
 */
public class RPCServerAgency {

    private static final Logger logger = Logger.getLogger(RPCServerAgency.class.getName());

    /**
     * 底层的服务器容器
     */
    private Server server;

    /**
     * 服务端程序启动的端口。
     */
    private static final int PORT = 50052;


    /**
     * 服务器启动
     */
    private void start() {
        try {
            // 初始化懒汉式单例的Dao工场
            DaoFactory.getInstance();

            server = ServerBuilder.forPort(PORT)
                    .addService(new SingleLoginImpl())
                    .build()
                    .start();
            logger.info("Server started, listening on " + PORT);
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.WARNING, "服务器启动出错！原因是：" + e.getMessage());
        }
    }

    /**
     * 服务器主线程阻塞，保持启动状态
     */
    private void blockUntilShutdown() {
        if (server != null) {
            try {
                server.awaitTermination();
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.log(Level.WARNING, "服务器线程阻塞出错！原因是：" + e.getMessage());
            }
        }
    }

    /**
     * 接收前端接口调用的实现类
     */
    class SingleLoginImpl extends SingleLoginGrpc.SingleLoginImplBase {

        /**
         * 调用服务端接口的公共入口方法。通过入参来区别各个业务
         *
         * @param responseObserver 用于回调前端返回值的观察者对象
         * @return 前端传入数据的观察者
         */
        @Override
        public StreamObserver<InParam> serviceFunc(final StreamObserver<OutParam> responseObserver) {
            // 构造并返回
            return new StreamObserver<InParam>() {
                @Override
                public void onNext(InParam request) {
                    logger.info("收到前端调用，当前线程ID：" + Thread.currentThread().getId() + "，当前对象哈希值：" + this.hashCode());
                    IFunction service = FunctionFactory.makeService(request.getFuncNo());
                    OutParam.Builder outPutBuilder = OutParam.newBuilder();
                    if (service == null) { // 找不到功能号对应的业务类
                        outPutBuilder
                                .setErrorNo(-1)
                                .setErrorInfo("功能号未定义！");
                    } else {
                        try {
                            ResultVo resultVo = service.invoke(request.getParamsMap());
                            outPutBuilder
                                    .setErrorNo(resultVo.getError_no())
                                    .setErrorInfo(resultVo.getError_info())
                                    .putAllResultMap(resultVo.getOutPutsMap());
                        } catch (Exception e) { // 业务类执行失败
                            outPutBuilder
                                    .setErrorNo(-1)
                                    .setErrorInfo("调用出错！原因是：" + e.getMessage());
                        }
                    }
                    responseObserver.onNext(outPutBuilder.build());
                    if (outPutBuilder.getErrorNo() == 100) { // 登录成功或注册成功，要保持登录状态
                        SingleLoginManager.getInstance().onHoldLogin(request, responseObserver);
                    } else if (outPutBuilder.getErrorNo() < 100 && outPutBuilder.getErrorNo() > -100) {
                        responseObserver.onCompleted(); // 错误号为-100~100之间就关闭连接
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    logger.warning("接口调用出错！原因是" + throwable.getMessage());
                    throwable.printStackTrace();
                }

                @Override
                public void onCompleted() {
                    logger.info("客户端关闭连接。");
                }
            };
        }
    }

    /**
     * 程序入口。服务端程序从这里启动
     */
    public static void main(String[] args) {
        final RPCServerAgency server = new RPCServerAgency();
        server.start();
        server.blockUntilShutdown();
    }
}
