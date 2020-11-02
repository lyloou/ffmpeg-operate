package org.mountcloud.ffmepg.task.bean.tasks;

import org.mountcloud.ffmepg.excption.FFMpegOperationExcption;
import org.mountcloud.ffmepg.operation.ffmpeg.vidoe.FFMpegVideoCut;
import org.mountcloud.ffmepg.task.bean.FFVideoTask;

import java.io.File;

/**
 * 视频转m3u8任务
 * com.ugirls.ffmepg.task.bean.tasks
 * 2018/6/11.
 *
 * @author zhanghaishan
 * @version V1.0
 */
public class FFMepgVideoCutTask extends FFVideoTask<FFMpegVideoCut> {
    FFMpegVideoCut cut;

    @Override
    public void callExecStart() {
        String outputFileName = cut.getOutputFileName();
        File outputFile = new File(outputFileName);
        System.out.println(outputFileName);
        if (outputFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            outputFile.delete();
        }
        throw new FFMpegOperationExcption(" not found FFCmd.");
    }

    @Override
    public void callExecEnd() {
    }

    /**
     * 任务构造
     *
     * @param format 操作
     */
    public FFMepgVideoCutTask(FFMpegVideoCut format) {
        super(format);
        this.cut = format;
    }

    /**
     * 回调
     *
     * @param line 一行结果
     */
    @Override
    public void callBackResultLine(String line) {
//        System.out.println(line);

//        if (super.getTimeLengthSec() != null) {
//            //获取视频信息
//            Matcher m = framePattern.matcher(line);
//            if (m.find()) {
//                try {
//                    String execTimeStr = m.group(5);
//                    int execTimeInt = FFVideoUtil.getTimelen(execTimeStr);
//                    double devnum = FFBigDecimalUtil.div(execTimeInt, super.getTimeLengthSec(), 5);
//                    double progressDouble = FFBigDecimalUtil.mul(devnum, 100);
//                    super.getProgress().setProgress((int) progressDouble);
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }
}
