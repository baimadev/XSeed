package com.holderzone.library.xseed_plugin.plugin.preload

/**
 *
 * @Description:
 * @Author: leisiyu
 * @CreateDate: 2022/1/13
 */
class Parameter(name: String, desc: String, index: Int) {
    val name: String
    val desc: String
    val index: Int
    override fun toString(): String {
        return "Parameter{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", index=" + index +
                '}'
    }

    init {
        this.name = name
        this.desc = desc
        this.index = index
    }
}
