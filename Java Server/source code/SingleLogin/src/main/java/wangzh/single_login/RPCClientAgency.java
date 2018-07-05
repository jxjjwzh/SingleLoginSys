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

package wangzh.single_login;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import single_login.android.wangzh.proto.InParam;
import single_login.android.wangzh.proto.OutParam;
import single_login.android.wangzh.proto.SingleLoginGrpc;
import wangzh.single_login.bean.ResultVo;

import java.util.HashMap;
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

    public void start(String host, int port) {
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress(host, port).usePlaintext();
        mChannel = channelBuilder.build();
        mStub = SingleLoginGrpc.newStub(mChannel);
    }

    public void shutdown() throws InterruptedException {
        mChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void invokeServer(int funcNo, HashMap<String, String> params, final ICallBack callBack) {
        StreamObserver<InParam> requestObserver = mStub.serviceFunc(new StreamObserver<OutParam>() {
            @Override
            public void onNext(OutParam outParam) {
                ResultVo resultVo = new ResultVo();
                if (outParam != null) {
                    resultVo.setError_no(outParam.getErrorNo());
                    resultVo.setError_info(outParam.getErrorInfo());
                    resultVo.setOutPutsMap(outParam.getResultMapMap());
                }
                callBack.callBack(resultVo);
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                ResultVo resultVo = new ResultVo();
                resultVo.setError_no(-2);
                resultVo.setError_info("网络请求出错，原因是：" + throwable.getMessage());
                callBack.callBack(resultVo);
            }

            @Override
            public void onCompleted() {
            }
        });

        // 构造传给服务器的入参
        InParam inParam = InParam.newBuilder()
                .setFuncNo(funcNo)
                .putAllParams(params)
                .buildPartial();
        // 调用服务器接口，传入参给服务器
        requestObserver.onNext(inParam);
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
