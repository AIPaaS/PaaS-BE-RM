/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2016/3/10 9:37:13                            */
/*==============================================================*/


drop table if exists res_cluster_info;

drop table if exists res_instance_props;

drop table if exists res_job_detail;

drop table if exists res_req_info;

drop table if exists res_task_log;

drop table if exists res_task_type;

/*==============================================================*/
/* Table: res_cluster_info                                      */
/*==============================================================*/
create table res_cluster_info
(
   cluster_id           varchar(255) not null,
   cluster_name         varchar(255) not null,
   marathon_addr        varchar(512) not null,
   chronos_addr         varchar(512),
   mesos_domain         varchar(128) not null,
   external_domain      varchar(128),
   consul_address       varchar(128),
   primary key (cluster_id)
);

alter table res_cluster_info comment '资源中心集群定义表';

/*==============================================================*/
/* Table: res_instance_props                                    */
/*==============================================================*/
create table res_instance_props
(
   key_id               int not null,
   key_code             varbinary(100) not null,
   key_value            varchar(4000) comment '可以是json格式，如对于用户，用一条标识用户名和密码',
   cluster_id           int not null,
   state                int comment '0: 可用，1 不可用',
   primary key (key_id)
);

alter table res_instance_props comment '用于记录资源中心的各种信息，如用户名，用户密码，mesos master地址';

/*==============================================================*/
/* Table: res_job_detail                                        */
/*==============================================================*/
create table res_job_detail
(
   task_id              int not null,
   cluster_id           varchar(255) not null,
   type_id              int not null,
   task_playbook        varchar(10000) comment 'yam格式',
   task_state           int not null comment '0:未开始；1：执行中；2：执行成功；3： 执行失败',
   task_order           int,
   task_start_time      datetime,
   task_end_time        datetime,
   primary key (task_id)
);

alter table res_job_detail comment '作业的具体实例表，由请求和任务类型实例化 ';

/*==============================================================*/
/* Table: res_req_info                                          */
/*==============================================================*/
create table res_req_info
(
   req_id               int not null,
   cluster_id           varchar(255) not null comment '对于非查询类的的请求，每次生成一个新的jobId',
   req_type             int not null comment '1:资源中心初始化; 2: 资源中心初始化重试; 3: 任务进度查询',
   req_cnt              varchar(10000) comment '请求的内容，json格式',
   req_resp             varchar(10000) comment '返回给门户的响应内容, json格式',
   req_time             datetime,
   resp_time            datetime,
   req_state            int comment '0,成功，1：失败',
   primary key (req_id)
);

alter table res_req_info comment '与门户交互的http请求记录表';

/*==============================================================*/
/* Table: res_task_log                                          */
/*==============================================================*/
create table res_task_log
(
   log_id               bigint not null,
   job_id               int not null,
   task_id              int not null,
   log_cnt              varchar(4000),
   log_time             datetime not null,
   primary key (log_id)
);

alter table res_task_log comment '任务日志表，每个任务至少两条记录，任务开始，任务结束';

/*==============================================================*/
/* Table: res_task_type                                         */
/*==============================================================*/
create table res_task_type
(
   type_id              int not null,
   type_name            varchar(100) not null,
   parent_id            int not null comment '对于第一级，此id为-1',
   type_order           int not null comment '任务执行的顺序',
   type_desc            varchar(255),
   type_templete        varchar(255) comment '此模板为具体的文件名，模板文件保存在运行环境的工程目录下，或者在jar包里面',
   type_state           int not null comment '0:启用；1：禁用',
   primary key (type_id, parent_id)
);

alter table res_task_type comment '定义资源中心各种任务类型：如建用户，安装mesos等';

