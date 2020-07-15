package com.huchonglin.core;

import java.util.Set;

/**
 * @author: hcl
 * @date: 2020/7/9 09:58
 */
public interface Reader {

    /**
     * 从指定的类读取相关信息
     * @param location 指定的类
     * @return 一个ClassSet集合
     */
    Set<Class<?>> read(Class<?> location);
}
