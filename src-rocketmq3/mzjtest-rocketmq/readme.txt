set path=%path%;D:\Java\opensource\rocketmq\alibaba-rocketmq\bin

mqadmin brokerStatus -b 127.0.0.1:10911
mqadmin brokerConsumeStats -b 127.0.0.1:10911

mqadmin clusterList -n 127.0.0.1:9876

mqadmin producerConnection -g ProducerGroup1 -n 127.0.0.1:9876 -t tcrbs
mqadmin consumerConnection -g ConsumerGroup1 -n 127.0.0.1:9876
mqadmin consumerStatus -g ConsumerGroup1 -n 127.0.0.1:9876

mqadmin topicList -n 127.0.0.1:9876
mqadmin statsAll -n 127.0.0.1:9876
mqadmin topicRoute -n 127.0.0.1:9876 -t tcrbs
mqadmin topicStatus -n 127.0.0.1:9876 -t tcrbs
mqadmin deleteTopic -c DefaultCluster -n 127.0.0.1:9876 -t tcrbs
mqadmin updateTopic -c rocketmq-cluster -n 127.0.0.1:9876 -t tcrbs

mqadmin printMsg -n 127.0.0.1:9876 -t tcrbs -b 2018-01-25#07:00:00:000 -e 2018-01-25#17:10:00:000
mqadmin queryMsgById -g ConsumerGroup1 -n 127.0.0.1:9876 -i C0A8211200002A9F00000000000251C7
mqadmin startMonitoring -n 127.0.0.1:9876

com.alibaba.rocketmq.tools.command.MQAdminStartup
---------------------------------------------
远程调试，mqbroker.xml里加入
<-Xdebug></-Xdebug>
<-agentlib:jdwp>transport=dt_socket,server=y,address=9999</-agentlib:jdwp>
---------------------------------------------
The most commonly used mqadmin commands are:
   updateTopic          Update or create topic
   deleteTopic          Delete topic from broker and NameServer.
   updateSubGroup       Update or create subscription group
   deleteSubGroup       Delete subscription group from broker.
   updateBrokerConfig   Update broker's config
   updateTopicPerm      Update topic perm
   topicRoute           Examine topic route info
   topicStatus          Examine topic Status info
   topicClusterList     get cluster info for topic
   brokerStatus         Fetch broker runtime status data
   queryMsgById         Query Message by Id
   queryMsgByKey        Query Message by Key
   queryMsgByUniqueKey  Query Message by Unique key
   queryMsgByOffset     Query Message by offset
   queryMsgByUniqueKey  Query Message by Unique key
   printMsg             Print Message Detail
   sendMsgStatus        send msg to broker.
   brokerConsumeStats   Fetch broker consume stats data
   producerConnection   Query producer's socket connection and client version
   consumerConnection   Query consumer's socket connection, client version and subscription
   consumerProgress     Query consumers's progress, speed
   consumerStatus       Query consumer's internal data structure
   cloneGroupOffset     clone offset from other group.
   clusterList          List all of clusters
   topicList            Fetch all topic list from name server
   updateKvConfig       Create or update KV config.
   deleteKvConfig       Delete KV config.
   wipeWritePerm        Wipe write perm of broker in all name server
   resetOffsetByTime    Reset consumer offset by timestamp(without client restart).
   updateOrderConf      Create or update or delete order conf
   cleanExpiredCQ       Clean expired ConsumeQueue on broker.
   cleanUnusedTopic     Clean unused topic on broker.
   startMonitoring      Start Monitoring
   statsAll             Topic and Consumer tps stats
   syncDocs             Synchronize wiki and issue to github.com
   allocateMQ           Allocate MQ
   checkMsgSendRT       check message send response time
   clusterRT            List All clusters Message Send RT
---------------------------------------------------------
问题：RemotingConnectException: connect to <172.17.42.1:10911> failed
解决：在broker.properties里添加：
brokerIP1=180.107.140.229