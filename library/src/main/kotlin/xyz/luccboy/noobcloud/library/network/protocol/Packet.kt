package xyz.luccboy.noobcloud.library.network.protocol

import io.netty.buffer.ByteBuf

abstract class Packet {
    abstract fun read(byteBuf: ByteBuf)
    abstract fun write(byteBuf: ByteBuf)

    fun writeString(string: String, byteBuf: ByteBuf) {
        byteBuf.writeInt(string.length)
        byteBuf.writeCharSequence(string, Charsets.UTF_8)
    }

    fun readString(byteBuf: ByteBuf): String {
        val length: Int = byteBuf.readInt()
        return byteBuf.readCharSequence(length, Charsets.UTF_8).toString()
    }

    fun writeStringArray(list: List<String>, byteBuf: ByteBuf) {
        byteBuf.writeInt(list.size)
        list.forEach { writeString(it, byteBuf) }
    }

    fun readStringArray(byteBuf: ByteBuf): List<String> {
        val length: Int = byteBuf.readInt()
        val list: MutableList<String> = mutableListOf()
        repeat(length) {
            list.add(readString(byteBuf))
        }

        return list
    }
}