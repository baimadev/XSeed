package com.holderzone.library.utils

import java.lang.StringBuilder

/**
 *
 * @Description:
 * @Author: leisiyu
 * @CreateDate: 2022/1/18
 */
class ParameterPrinter(tag: String, methodName: String?) {
    private val result = StringBuilder()
    private var paramIndex = 0
    private val divider = ", "
    private var tag = ""
    fun append(name: String?, arg0: Int): ParameterPrinter {
        if (paramIndex++ != 0) result.append(divider)
        result.append(String.format(PARAMETER_PRINT_FORMAT, name, arg0))
        return this
    }

    fun append(name: String?, agr0: Boolean): ParameterPrinter {
        if (paramIndex++ != 0) result.append(divider)
        result.append(String.format(PARAMETER_PRINT_FORMAT, name, agr0))
        return this
    }

    fun append(name: String?, arg0: Short): ParameterPrinter {
        if (paramIndex++ != 0) result.append(divider)
        result.append(String.format(PARAMETER_PRINT_FORMAT, name, arg0))
        return this
    }

    fun append(name: String?, arg0: Byte): ParameterPrinter {
        if (paramIndex++ != 0) result.append(divider)
        result.append(String.format(PARAMETER_PRINT_FORMAT, name, arg0))
        return this
    }

    fun append(name: String?, arg0: Char): ParameterPrinter {
        if (paramIndex++ != 0) result.append(divider)
        result.append(String.format(PARAMETER_PRINT_FORMAT, name, arg0))
        return this
    }

    fun append(name: String?, arg0: Long): ParameterPrinter {
        if (paramIndex++ != 0) result.append(divider)
        result.append(String.format(PARAMETER_PRINT_FORMAT, name, arg0))
        return this
    }

    fun append(name: String?, arg0: Double): ParameterPrinter {
        if (paramIndex++ != 0) result.append(divider)
        result.append(String.format(PARAMETER_PRINT_FORMAT, name, arg0))
        return this
    }

    fun append(name: String?, arg0: Float): ParameterPrinter {
        if (paramIndex++ != 0) result.append(divider)
        result.append(String.format(PARAMETER_PRINT_FORMAT, name, arg0))
        return this
    }

    fun append(name: String?, arg0: Any?): ParameterPrinter {
        if (paramIndex++ != 0) result.append(divider)
        if (arg0 != null && arg0.javaClass.isArray) {
            result.append(String.format(PARAMETER_PRINT_FORMAT, name, arrayToString(arg0)))
        } else {
            result.append(String.format(PARAMETER_PRINT_FORMAT, name, arg0))
        }
        return this
    }

    private fun arrayToString(args: Any): String {
        return if (args !is Array<*>) {
            when (args) {
                is IntArray -> {
                    args.contentToString()
                }
                is CharArray -> {
                    args.contentToString()
                }
                is BooleanArray -> {
                    args.contentToString()
                }
                is ByteArray -> {
                    args.contentToString()
                }
                is LongArray -> {
                    args.contentToString()
                }
                is DoubleArray -> {
                    args.contentToString()
                }
                is FloatArray -> {
                    args.contentToString()
                }
                is ShortArray -> {
                    args.contentToString()
                }
                else -> {
                    "Unknown type array"
                }
            }
        } else {
            (args as Array<Any?>).contentDeepToString()
        }
    }

    fun print() {
        result.append("]")
        println(result.toString())
    }

    companion object {
        const val PARAMETER_PRINT_FORMAT = "%s=\"%s\""
    }

    init {
        this.tag = tag
        result.append("入参统计 ").append(methodName).append("[")
    }
}