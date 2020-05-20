package com.cartravel.utils

import com.typesafe.config.ConfigFactory

/**
  * Created by zxc
  */
class GlobalConfigUtils {

  private def conf = ConfigFactory.load()
  def  heartColumnFamily = "MM"
  val getProp = (argv:String) => conf.getString(argv)
}

object GlobalConfigUtils extends GlobalConfigUtils
