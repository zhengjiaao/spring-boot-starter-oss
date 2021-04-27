package com.dist.zja.oss;

import com.dist.zja.oss.common.annotations.ClassComment;
import com.dist.zja.oss.common.annotations.MethodComment;
import com.dist.zja.oss.common.utils.IdWorker;

/**
 * Company: 上海数慧系统技术有限公司
 * Department: 数据中心
 * Date: 2019-11-22 13:39
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：自定义Id递增趋势策略
 */
@ClassComment(value = "OSS Id 生成器", author = "zhengja")
public abstract class OSSId {

    private static final IdWorker idWorker = new IdWorker();

    /**
     * 分布式：id生成器   采用jpa苏的id生成器
     * 描述：底层雪花算法，不是递增，是递增趋势
     */
    @MethodComment(
            function = "默认-分布式递增趋势-id生成器",
            description = "分布式：id生成器  100万个id大概3~8秒")
    public static long Id() {
        return idWorker.getId();
    }

}
