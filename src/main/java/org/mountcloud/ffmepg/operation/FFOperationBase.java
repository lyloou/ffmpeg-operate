package org.mountcloud.ffmepg.operation;


import org.mountcloud.ffmepg.annotation.FFAnnotation;
import org.mountcloud.ffmepg.annotation.FFCmdBean;
import org.mountcloud.ffmepg.excption.FFMpegOperationExcption;
import org.mountcloud.ffmepg.util.FFAnnotationUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO:
 * com.ugirls.ffmepg.operation
 * 2018/6/6.
 *
 * @author zhanghaishan
 * @version V1.0
 */
public abstract class FFOperationBase {

    private String stringvalue = null;
    private String command = null;
    private List<String> commandParams = new ArrayList<>();

    /**
     * 返回命令
     *
     * @return 命令
     */
    public String getCommand() {
        if (command == null) {
            command = toString();
        }
        return command;
    }

    /**
     * 返回命令参数
     *
     * @return 参数
     */
    public List<String> getCommandParams() {
        if (commandParams.size() == 0) {
            toString();
        }
        return commandParams;
    }


    /**
     * 直接转成命令
     *
     * @return 命令
     */
    @Override
    public String toString() {

        String str = null;
        FFAnnotationUtil ffAnnotationUtil = new FFAnnotationUtil();
        try {
            FFCmdBean cmdBean = ffAnnotationUtil.getClassAnnocation(this);
            String execname = command = cmdBean.getCmdName().getKey();
            List<FFAnnotation> list = cmdBean.getCmdParameter();

            str = execname;

            for (int i = 0; i < list.size(); i++) {
                FFAnnotation annotation = list.get(i);
                String aKey = annotation.getKey();
                String aValue = annotation.getValue();

                if (aKey != null && aKey.length() > 0 && aValue != null) {
                    str = str + " " + aKey;
                    commandParams.add(aKey);
                }

                if (aValue != null && aValue.length() > 0) {
                    str = str + " " + addQuoteToValueIfNeeded(aKey, aValue);
                    commandParams.add(aValue);
                }
            }

        } catch (IllegalAccessException e) {
            str = null;
        }
        if (str == null) {
            throw new FFMpegOperationExcption("FFMpegOperation To String Is Null!");
        }

        stringvalue = str;

        return stringvalue;
    }

    /**
     * 给文件加双引号
     *
     * @param aKey   键
     * @param aValue 值
     * @return 可能加了双引号的值
     */
    private String addQuoteToValueIfNeeded(String aKey, String aValue) {
        if ("-i".equals(aKey) || "".equals(aKey)) {
            return "\"" + aValue + "\"";
        }
        return aValue;
    }
}
