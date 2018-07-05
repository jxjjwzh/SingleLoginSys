package wangzh.single_login;

import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.StringUtils;
import single_login.android.wangzh.proto.InParam;
import single_login.android.wangzh.proto.OutParam;
import wangzh.single_login.constants.ParamConstants;
import wangzh.single_login.utils.RSAUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 实现单点登录业务
 * Announcements：
 *
 * @author 王志鸿
 * @corporation Thinkive
 * @date 2018/7/4
 */
public class SingleLoginManager {

    // ************************ 单例模式，开始 ************************
    private SingleLoginManager() {
        mObserverMap = new HashMap<String, ClientInfo>();
        mRSAMap = new HashMap<String, RSAUtil.RSAInfo>();
    }

    private static final class Holder {
        private static final SingleLoginManager sInstance = new SingleLoginManager();
    }

    public static synchronized SingleLoginManager getInstance() {
        return Holder.sInstance;
    }
    // ************************ 单例模式，结束 ************************

    private HashMap<String, ClientInfo> mObserverMap;

    private HashMap<String, RSAUtil.RSAInfo> mRSAMap;

    /**
     * 实现单点登录、踢之前登录同账号客户端下线逻辑的方法
     * 在开始进入登录状态的接口中执行此方法
     *
     * @param inParam          要维持登录状态的客户端调用的入参
     * @param responseObserver 服务器通知客户端用的观察者对象
     */
    public void onHoldLogin(InParam inParam, StreamObserver<OutParam> responseObserver) {
        Map<String, String> params = inParam.getParamsMap();
        String account = params.get(ParamConstants.ACCOUNT);
        String device_id = params.get(ParamConstants.DEVICE_ID);
        if (StringUtils.isNotEmpty(account)) {
            if (mObserverMap.containsKey(account)) { // 此时说明有客户端正在登录这个账户
                // 通知这个客户端下线
                ClientInfo clientInfo = mObserverMap.get(account);
                if (clientInfo != null && !device_id.equals(clientInfo.device_id)) {
                    clientInfo.responseObserver.onNext(OutParam.newBuilder().setErrorNo(2).setErrorInfo(
                            "账号[" + account + "]已在其他设备上登录，您已被强制下线！。").build());
                    clientInfo.responseObserver.onCompleted(); // 关闭与客户端的连接
                }

            }
            // 添加现在登录的客户端的观察者对象进入Map
            ClientInfo clientInfo = new ClientInfo();
            clientInfo.responseObserver = responseObserver;
            clientInfo.device_id = device_id;
            mObserverMap.put(account, clientInfo);
        }
    }

    /**
     * 生成RSA信息，并保存在Map中
     */
    public RSAUtil.RSAInfo generateRSAInfo(String device_id) {
        RSAUtil.RSAInfo rsaInfo = RSAUtil.generateRSAInfo();
        mRSAMap.put(device_id, rsaInfo);
        return rsaInfo;
    }

    /**
     * 检查调用账号校验相关接口时，是否已经申请了RSA
     */
    public boolean checkContainsRSA(String device_id) {
        return mRSAMap.containsKey(device_id);
    }

    /**
     * 销毁某个客户端设备对应的RSA参数信息
     *
     * @param device_id 要销毁的是哪个设备的RSA信息
     */
    public void clearRSAInfo(String device_id) {
        mRSAMap.remove(device_id);
    }

    /**
     * 获取某个客户端设备对应的RSA参数信息
     *
     * @param device_id 要获取的是哪个设备的RSA信息
     */
    public String getRSAPrivate(String device_id) {
        return mRSAMap.get(device_id).privateKey;
    }

    /**
     * 保存客户端回调接口和设备唯一标识
     */
    private class ClientInfo {
        String device_id;
        StreamObserver<OutParam> responseObserver;
    }

}
