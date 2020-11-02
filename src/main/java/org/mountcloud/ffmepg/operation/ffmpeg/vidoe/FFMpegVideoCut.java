package org.mountcloud.ffmepg.operation.ffmpeg.vidoe;


import org.mountcloud.ffmepg.annotation.FFCmd;
import org.mountcloud.ffmepg.operation.ffmpeg.FFMpegOperationBase;

/**
 * 视频剪切操作 com.ugirls.ffmepg.operation.ffmpeg.vidoe 2020/11/2.
 *
 * @author lyloou
 * @version V1.0
 */
public class FFMpegVideoCut extends FFMpegOperationBase {

    /**
     * 视频文件
     */
    @FFCmd(key = "-i")
    private String videoFileName;

    /**
     * 开始时间秒
     */
    @FFCmd(key = "-ss")
    private String startTime;

    /**
     * 结束时间秒
     */
    @FFCmd(key = "-to")
    private String endTime;

    /**
     * 输出文件
     */
    @FFCmd
    private String outputFileName;

    public String getVideoFileName() {
        return videoFileName;
    }

    public void setVideoFileName(String videoFileName) {
        this.videoFileName = videoFileName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }
}