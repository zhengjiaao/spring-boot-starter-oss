package com.dist.zja.oss;

import com.dist.zja.oss.common.annotations.AttributeComment;
import com.dist.zja.oss.common.annotations.ClassComment;

/**
 * Company: 上海数慧系统技术有限公司
 * Department: 数据中心
 * Date: 2021-05-08 14:09
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：设置范围
 */
@ClassComment("指定范围下载")
public class OSSRange {

    private final static Long INFINITE = Long.MAX_VALUE;

    @AttributeComment("起始字节的位置")
    private Long offset;
    @AttributeComment("要读取的长度 (可选，如果无值则代表读到对象结尾)")
    private Long length;

    public OSSRange(Long offset, Long length) {
        this.offset = offset;
        this.length = length;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }
}
