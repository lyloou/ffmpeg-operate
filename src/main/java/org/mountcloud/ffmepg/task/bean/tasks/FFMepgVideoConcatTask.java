package org.mountcloud.ffmepg.task.bean.tasks;

import org.mountcloud.ffmepg.operation.ffmpeg.video.FFMpegVideoConcat;
import org.mountcloud.ffmepg.task.bean.FFVideoTask;
import org.mountcloud.ffmepg.util.FFBigDecimalUtil;
import org.mountcloud.ffmepg.util.FFConcatUtil;
import org.mountcloud.ffmepg.util.FFVideoUtil;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 视频转m3u8任务
 * com.ugirls.ffmepg.task.bean.tasks
 * 2018/6/11.
 *
 * @author zhanghaishan
 * @author lyloou
 * @version V1.0
 */
public class FFMepgVideoConcatTask extends FFVideoTask<FFMpegVideoConcat> {
    FFMpegVideoConcat concat;
    private String textFile;

    /**
     * 任务构造
     *
     * @param format 操作
     */
    public FFMepgVideoConcatTask(FFMpegVideoConcat format) {
        super(format);
        this.concat = format;
    }

    /**
     * 进度正则查询
     */
    private final String frameRegexDuration = "frame=([\\s,\\d]*) fps=(.*?) q=(.*?) size=([\\s\\S]*) time=(.*?) bitrate=([\\s\\S]*) speed=(.*?)x";

    /**
     * 正则模式
     */
    private final Pattern framePattern = Pattern.compile(frameRegexDuration);

    @Override
    public void callExecStart() {
        String concatFilename = FFConcatUtil.getConcatFilename(concat.getVideoPathList());
        concat.setOutputFileName(concatFilename);
        textFile = FFConcatUtil.generateTextFile(concat.getVideoPathList(), concatFilename);
        concat.setVideoListTextFileName(textFile);

        System.out.println(concat.getCommand());
    }


    @Override
    public void callExecEnd() {
        if (textFile != null) {
            //noinspection ResultOfMethodCallIgnored
            new File(textFile).delete();
        }
    }


    /**
     * 回调
     *
     * @param line 一行结果
     */
    @Override
    public void callBackResultLine(String line) {
        if (super.getTimeLengthSec() != null) {
            //获取视频信息
            Matcher m = framePattern.matcher(line);
            if (m.find()) {
                try {
                    String execTimeStr = m.group(5);
                    int execTimeInt = FFVideoUtil.getTimelen(execTimeStr);
                    double devnum = FFBigDecimalUtil.div(execTimeInt, super.getTimeLengthSec(), 5);
                    double progressDouble = FFBigDecimalUtil.mul(devnum, 100);
                    super.getProgress().setProgress((int) progressDouble);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
