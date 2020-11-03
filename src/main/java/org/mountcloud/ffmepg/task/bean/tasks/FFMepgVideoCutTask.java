package org.mountcloud.ffmepg.task.bean.tasks;

import org.mountcloud.ffmepg.excption.FFMpegOperationExcption;
import org.mountcloud.ffmepg.operation.ffmpeg.video.FFMpegVideoCut;
import org.mountcloud.ffmepg.operation.ffmpeg.video.FFMpegVideoInfo;
import org.mountcloud.ffmepg.result.defaultResult.FFVideoInfoResult;
import org.mountcloud.ffmepg.task.bean.FFVideoTask;
import org.mountcloud.ffmepg.task.context.FFTaskContext;
import org.mountcloud.ffmepg.util.FFBigDecimalUtil;
import org.mountcloud.ffmepg.util.FFVideoUtil;

import java.io.File;
import java.sql.Time;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
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
public class FFMepgVideoCutTask extends FFVideoTask<FFMpegVideoCut> {
    private final FFMpegVideoCut cut;
    private final CyclicBarrier cyclicBarrier;
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
        // 参数验证

        String inputFilename = cut.getVideoFileName();
        File inputFile = new File(inputFilename);
        if (!inputFile.exists()) {
            throw new FFMpegOperationExcption("inputFile is not existed:" + inputFilename);
        }

        File outputFile = new File(cut.getOutputFileName());
        if (outputFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            outputFile.delete();
        }

        FFVideoInfoResult result = new FFVideoInfoResult();
        FFMpegVideoInfo ffMpegVideoInfo = new FFMpegVideoInfo();
        ffMpegVideoInfo.setVideoUrl(inputFilename);
        FFMepgVideoInfoTask videoInfoTask = new FFMepgVideoInfoTask(result, ffMpegVideoInfo);
        FFTaskContext.getContext().submit(videoInfoTask, null);
        Integer timeLengthSec = result.getTimeLengthSec();
        long startTime = Time.valueOf(cut.getStartTime()).getTime();
        long endTime = Time.valueOf(cut.getEndTime()).getTime();
        if ((endTime - startTime) > timeLengthSec * 1000) {
            throw new FFMpegOperationExcption(
                    String.format("截取时间不合法：start:%s, end:%s, duration:%s, 因为截取时间大于视频的时长"
                            , cut.getStartTime(), cut.getEndTime(), result.getTimeLength()));
        }
    }

    @Override
    public void callExecEnd() {
        if (cyclicBarrier != null) {
            try {
                System.out.println(getTaskId() + "：我已经完成任务，等你们...");
                cyclicBarrier.await();
                System.out.println(getTaskId() + "：都完成了啊，我起床了...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 任务构造
     *
     * @param format        操作
     * @param cyclicBarrier
     */
    public FFMepgVideoCutTask(FFMpegVideoCut format, CyclicBarrier cyclicBarrier) {
        super(format);
        this.cut = format;
        this.cyclicBarrier = cyclicBarrier;
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
