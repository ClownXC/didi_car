package com.cartravel.hbase.hutils

import org.apache.commons.lang3.StringUtils
import org.apache.hadoop.hbase.util.{Bytes, MD5Hash}

/**
  * Created by zxc
  */
object RowkeyUtils {

  //000X|+12wei
  def getRowkey(pk:String):String = {
    val prefix = md5SwithLong(pk)
    //id_createTime
    //190817
    val split: Array[String] = pk.split("_")
    val rk = s"${split(0)}_${split(1).substring(2 , split(1).length)}"
    val suffix = MD5Hash.getMD5AsHex(Bytes.toBytes(rk)).substring(0 , 12)
    prefix + suffix
  }



  //保证rowkey的前缀是000x|
  //16 -->000x| +


  //获取 rowkey的前缀
  private def md5SwithLong(pk:String ):String = {
    val rkMd5 = MD5Hash.getMD5AsHex(Bytes.toBytes(pk)).substring(0 , 15)
    //0~9 a~f -->  0~15
    val hash: Long = md5ToLong(rkMd5)

    val str = (hash % 9).toString
    val prefix: String = StringUtils.leftPad(str , 4 , "0")
    prefix

  }

  private def md5ToLong(strs:String):Long = {
    var x:Long = 0 ; //最后返回的值
    var tmp:Long = 1 ;
    val split: Array[String] = strs.split("")
    for(i <- 0 until split.length){
      split(i) match{
        case "a" => tmp = 10
        case "b" => tmp = 11
        case "c" => tmp = 12
        case "d" => tmp = 13
        case "e" => tmp = 14
        case "f" => tmp = 15
        case _ => tmp = Integer.valueOf(split(i)).toLong
      }
      x += (tmp * Math.pow(8 , split.length - i - 1)).toLong
    }
    x
  }
}
