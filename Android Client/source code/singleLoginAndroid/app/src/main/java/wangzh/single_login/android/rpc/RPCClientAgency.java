/*
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wangzh.single_login.android.rpc;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import single_login.android.wangzh.proto.InParam;
import single_login.android.wangzh.proto.OutParam;
import single_login.android.wangzh.proto.SingleLoginGrpc;
import wangzh.single_login.android.bean.ResultVo;
import wangzh.single_login.android.utils.LogUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class RPCClientAgency {

    private RPCClientAgency() {
    }

    private static final class Holder {
        private static final RPCClientAgency sInstance = new RPCClientAgency();
    }

    public static synchronized RPCClientAgency getInstance() {
        return Holder.sInstance;
    }

    private ManagedChannel mChannel;

    private SingleLoginGrpc.SingleLoginStub mStub;

    private ICallBack mICallBack;

    /**
     * 当前与服务器维持连接的观察者，用来给服务器传递数据
     */
    private StreamObserver<InParam> mRequestObserver;

    public void start(String host, int port) {
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress(host, port).usePlaintext();
        mChannel = channelBuilder.build();
        mStub = SingleLoginGrpc.newStub(mChannel);
    }

    public void shutdown() {
        try {
            mChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LogUtil.e("断开服务器连接时出错！", e);
            e.printStackTrace();
        }
    }

//    public synchronized void invokeServer(int funcNo, HashMap<String, String> params, ICallBack callBack) {
//        invokeServer(funcNo, params, callBack, false);
//    }

    public synchronized void invokeServer(int funcNo, HashMap<String, String> params, ICallBack callBack) {
        mICallBack = callBack;
//        if (mRequestObserver == null || isNewRequestObserver) {
        if (mRequestObserver == null) {
            mRequestObserver = mStub.serviceFunc(new StreamObserver<OutParam>() {
                @Override
                public void onNext(OutParam outParam) {
                    ResultVo resultVo = new ResultVo();
                    if (outParam != null) {
                        resultVo.setError_no(outParam.getErrorNo());
                        resultVo.setError_info(outParam.getErrorInfo());
                        resultVo.setOutPutsMap(outParam.getResultMapMap());
                    }
                    mICallBack.callBack(resultVo);
                }

                @Override
                public void onError(Throwable throwable) {
                    throwable.printStackTrace();
                    ResultVo resultVo = new ResultVo();
                    resultVo.setError_no(-2);
                    resultVo.setError_info("网络请求出错，原因是：" + throwable.getMessage());
                    mICallBack.callBack(resultVo);
                    mRequestObserver = null; // 下次将重新建立连接
                }

                @Override
                public void onCompleted() {
                    LogUtil.i("服务器断开连接。");
                    mRequestObserver = null; // 下次将重新建立连接
                }
            });
        }

        // 构造传给服务器的入参
        InParam inParam = InParam.newBuilder()
                .setFuncNo(funcNo)
                .putAllParams(params)
                .build();
        // 调用服务器接口，传入参给服务器
        mRequestObserver.onNext(inParam);
    }

    /**
     * gRPC单点登录代理类的回调接口
     */
    public interface ICallBack {

        /**
         * 回调给外界gRPC接口返回的信息
         *
         * @param resultVo 服务器返回的信息数据
         */
        void callBack(ResultVo resultVo);
    }
}
