package org.mountcloud.ffmepg.util;

import org.mountcloud.ffmepg.operation.FFOperationBase;
import org.mountcloud.ffmepg.task.bean.FFTask;

/**
 * <p>类的详细说明</p>
 *
 * @author lyloou
 * @author 其他作者姓名
 * @version 1.00 2020/11/3 , 星期二 lyloou 创建
 * <p>1.01 YYYY/MM/DD 修改者姓名 修改内容说明</p>
 */
public class FFTaskUtil {
    public static <T extends FFOperationBase> void defaultMonitor(FFTask<T> task) {
        while (task.isRunning()) {
            System.out.println(task.getName() + "-" + task.getTaskId() + ": " + task.getProgress().getProgress());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
