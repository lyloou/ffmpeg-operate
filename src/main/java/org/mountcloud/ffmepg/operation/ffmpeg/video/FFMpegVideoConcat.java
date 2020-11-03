package org.mountcloud.ffmepg.operation.ffmpeg.video;


import org.mountcloud.ffmepg.annotation.FFCmd;
import org.mountcloud.ffmepg.excption.FFMpegOperationExcption;
import org.mountcloud.ffmepg.operation.ffmpeg.FFMpegOperationBase;

import java.util.List;

/**
 * 视频剪切操作 com.ugirls.ffmepg.operation.ffmpeg.vidoe 2020/11/2.
 *
 * @author lyloou
 * @version V1.0
 */
public class FFMpegVideoConcat extends FFMpegOperationBase {

    /**
     * force fomat
     */
    @FFCmd(key = "-f")
    private String fmt = "concat";

    /**
     * https://trac.ffmpeg.org/wiki/Concatenate
     * The -safe 0 above is not required if the paths are relative.
     */
    @FFCmd(key = "-safe")
    private Integer safe = 0;

    /**
     * 视频列表text文件
     */
    @FFCmd(key = "-i")
    private String videoListTextFileName;

    /**
     * 编码模式
     */
    @FFCmd(key = "-c")
    private String codec = "copy";

    /**
     * 输出文件
     */
    @FFCmd
    private String outputFileName;

    private List<String> videoPathList;

    public FFMpegVideoConcat(List<String> videoPathList) {
        super();
        this.videoPathList = videoPathList;
    }


    public String getFmt() {
        return fmt;
    }

    public void setFmt(String fmt) {
        this.fmt = fmt;
    }

    public Integer getSafe() {
        return safe;
    }

    public void setSafe(Integer safe) {
        this.safe = safe;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }


    public String getVideoListTextFileName() {
        return videoListTextFileName;
    }

    public void setVideoListTextFileName(String videoListTextFileName) {
        this.videoListTextFileName = videoListTextFileName;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public List<String> getVideoPathList() {
        return videoPathList;
    }

    public void setVideoPathList(List<String> videoPathList) {
        this.videoPathList = videoPathList;
    }
}