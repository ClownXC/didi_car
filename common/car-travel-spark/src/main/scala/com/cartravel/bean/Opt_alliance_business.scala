package com.cartravel.bean

/**
  * Created by zxc
  */
case class Opt_alliance_business(
                                  table:String ,
                                  operatorType:String ,
                                  id_ :String , //主键
                                  alliance_name :String , //加盟商名称
                                  organization_code :String , //组织机构代码
                                  alliance_role :String , //加盟商角色，0、车管 1、司管 ，多种角色英文逗号
                                  linkman :String , //联系人姓名
                                  contact_number:String , //联系电话
                                  create_user :String , //创建人
                                  create_time :String , //创建时间
                                  update_user :String , //修改人
                                  update_time :String , //修改时间
                                  state :String , //状态，0、正常 1、停用
                                  del_state :String//删除标识 0：未删除 1：已删除

                                )
