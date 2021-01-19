package com.eloam.process.connectionsMqtt;

/**
 * @Title:
 * @Description:
 * @Author:
 * @Date: ${date}
 * @Version: V1.0
 */

public class PushBean {
    public Boolean isConnect = false;
    public String pushData;
    public String terminalInfo;

    public String getTerminalInfo() {
        return terminalInfo;
    }

    public PushBean setTerminalInfo(String info) {
        terminalInfo = info;
        return this;
    }

    public PushBean setConnect(Boolean connect) {
        isConnect = connect;
        return this;
    }

    public PushBean setPushData(String pushData) {
        this.pushData = pushData;
        return this;
    }
}
