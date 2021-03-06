package com.dist.zja.oss.common.enums;

import com.dist.zja.oss.common.annotations.ClassComment;

/**
 * Company: 上海数慧系统技术有限公司
 * Department: 数据中心
 * Date: 2021-05-14 16:02
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：下载模式
 */
@ClassComment("下载模式 默NONE不合并")
public enum DownloadModeEnum {
    MERGE_DOWNLOAD, NONE;  //合并下载 , 默认不合并
}
