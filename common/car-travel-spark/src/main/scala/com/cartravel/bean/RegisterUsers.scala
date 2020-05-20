package com.cartravel.bean

/**
  * Created by zxc
  */
case class RegisterUsers(
                          table: String,
                          id: String,
                          operatorType: String, //操作类型
                          create_time: String, //用户注册时间
                          last_login_city: String, //上一次登录城市
                          last_logon_time: String, //上一次登录时间
                          city_name: String //注册城市名称
                        )
