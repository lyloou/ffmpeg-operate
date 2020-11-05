package org.mountcloud.ffmepg.task.bean;

import org.mountcloud.ffmepg.operation.FFOperationBase;
import org.mountcloud.ffmepg.task.bean.read.FFTaskReadState;
import org.mountcloud.ffmepg.task.bean.read.FFTaskReadThread;
import org.mountcloud.ffmepg.task.bean.read.FFTaskReadType;
import org.mountcloud.ffmepg.task.context.FFTaskContext;
import org.mountcloud.ffmepg.task.threads.FFThread;
import org.mountcloud.ffmepg.util.FFTerminalCreater;
import org.mountcloud.ffmepg.util.UUIDUtil;

import java.util.*;

/**
 * 任务父类
 * com.ugirls.ffmepg.task
 * 2018/6/11.
 *
 * @author zhanghaishan
 * @version V1.0
 */
public abstract class FFTask<T extends FFOperationBase> implements FFThread {

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 任务名称，给任务起个名
     */
    private String name = "FFTask";

    /**
     * 执行结果的全部内容
     */
    protected StringBuffer result = new StringBuffer("");

    /**
     * 内容操作
     */
    protected T operationBase;

    /**
     * 任务进度
     */
    private FFTaskProgress progress;

    /**
     * 任务命令行
     */
    protected FFTerminalCreater.FFTerminal terminal = null;

    /**
     * 任务创建时间
     */
    private Date createTime;

    /**
     * 读取任务的执行是否完成
     */
    private Map<FFTaskReadType, FFTaskReadState> readState = new HashMap<>();

    /**
     * 读取线程
     */
    protected List<FFTaskReadThread> ffTaskReadThreadList = new ArrayList<>();

    @Override
    public void run() {

        //任务执行状态
        boolean state = true;
        try {
            //执行开始前回调
            callExecStart();

            //任务开始
            progress.setState(FFTaskStateEnum.START);

            //执行的命令
            String cmd = operationBase.toString();

            terminal = FFTerminalCreater.getCreater().getTerminal(cmd);
            //初始化读取线程
            initReadThread();

            //等待执行完毕
            while (!this.checkReadIsEnd()) {
                Thread.sleep(500);
            }

            //执行完毕则获取执行状态
            state = this.checkReadState();
        } catch (Exception e) {
            state = false;
            e.printStackTrace();
        } finally {
            //执行结束回调
            callExecEnd();

            FFTaskContext.getContext().removeTask(this.getTaskId());

            //设置状态
            progress.setProgress(100);
            progress.setState(state ? FFTaskStateEnum.COMPLETE : FFTaskStateEnum.FAILED);
        }


    }

    public boolean isRunning() {
        switch (getProgress().getState()) {
            case FAILED:
            case COMPLETE:
                return false;
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 判断是否在读取中
     *
     * @return false 读取中，true 读取完成
     */
    private boolean checkReadIsEnd() {
        for (FFTaskReadState state : readState.values()) {
            if (!state.isEnd()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断读取状态
     *
     * @return false 读取报错，true 读取成功
     */
    private boolean checkReadState() {
        for (FFTaskReadState state : readState.values()) {
            if (!state.getState()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 初始化读取线程
     */
    private void initReadThread() {

        //创建读取线程，读取error和in
        FFTaskReadThread ffTaskReadErrorThread = new FFTaskReadThread(this, FFTaskReadType.ERROR_IN);
        FFTaskReadThread ffTaskReadThread = new FFTaskReadThread(this, FFTaskReadType.IN);
        readState.put(ffTaskReadErrorThread.getReadType(), new FFTaskReadState());
        readState.put(ffTaskReadThread.getReadType(), new FFTaskReadState());

        //加入线程数组，如果以后需要可以扩展
        ffTaskReadThreadList.add(ffTaskReadErrorThread);
        ffTaskReadThreadList.add(ffTaskReadThread);

        //开启这两个读取线程，此处不优雅，如果有更好的方案请告诉我
        new Thread(ffTaskReadErrorThread).start();
        new Thread(ffTaskReadThread).start();
    }


    /**
     * 传入一行结果
     *
     * @param line 一行日志
     */
    public synchronized void putResultLine(String line) {
        result.append(line);
        callResultLine(line);
    }


    /**
     * 执行开始
     */
    public abstract void callExecStart();

    /**
     * 正确结果行
     *
     * @param line 一行结果
     */
    public abstract void callResultLine(String line);

    /**
     * 执行结束
     */
    public abstract void callExecEnd();


    /**
     * 任务构造
     *
     * @param operation 操作
     */
    public FFTask(T operation) {
        this(operation, UUIDUtil.getUUIDSimpl());
    }

    public FFTask(T operation, String taskId) {
        this.operationBase = operation;
        this.taskId = taskId == null ? UUIDUtil.getUUIDSimpl() : taskId;
        this.createTime = new Date();
        this.progress = new FFTaskProgress();
    }

    public FFTask() {
        this.progress = new FFTaskProgress();
    }

    public String getTaskId() {
        return taskId;
    }

    public FFTaskProgress getProgress() {
        return progress;
    }

    public void setProgress(FFTaskProgress progress) {
        this.progress = progress;
    }

    public FFTerminalCreater.FFTerminal getTerminal() {
        return terminal;
    }

    public FFTaskReadState getReadState(FFTaskReadType ffTaskReadType) {
        return this.readState.get(ffTaskReadType);
    }

}
