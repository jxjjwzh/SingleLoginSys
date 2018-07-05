package wangzh.single_login.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 接口调用的结果集
 * Announcements：
 *
 * @author 王志鸿
 * @corporation Thinkive
 * @date 2018/6/29
 */
public class ResultVo {

    /**
     * 大于等于100：调用成功，并保持连接
     * 小于等于100：调用失败，并保持连接
     *
     * 小于0：调用失败并结束
     * 等于0：调用成功并结束
     * 等于2：服务器通知客户端被踢下线
     */
    private int error_no;
    private String error_info;
    private Map<String, String> outPutsMap;

    public ResultVo() {
        this.outPutsMap = new HashMap<String, String>();
    }

    public int getError_no() {
        return error_no;
    }

    public void setError_no(int error_no) {
        this.error_no = error_no;
    }

    public String getError_info() {
        return error_info;
    }

    public void setError_info(String error_info) {
        this.error_info = error_info;
    }

    public Map<String, String> getOutPutsMap() {
        return outPutsMap;
    }

    public void setOutPutsMap(Map<String, String> outPutsMap) {
        this.outPutsMap = outPutsMap;
    }

    @Override
    public String toString() {
        return "error_no：" + error_no + "，error_info：" + error_info + "\noutPutsMap:" + outPutsMap.toString();
    }
}
