package org.mzj.test;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.alibaba.rocketmq.client.consumer.DefaultMQPullConsumer;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.PullResult;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.hook.ConsumeMessageContext;
import com.alibaba.rocketmq.client.hook.ConsumeMessageHook;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendCallback;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.common.protocol.route.BrokerData;
import com.alibaba.rocketmq.common.protocol.route.TopicRouteData;
import com.alibaba.rocketmq.remoting.RPCHook;
import com.alibaba.rocketmq.remoting.protocol.RemotingCommand;

public class RocketmqClientTest {
	String nameSrvAddr = "180.107.140.229:9876";//"127.0.0.1:9876";
	//消息主题，一个Producer实例只能对于一个topic，一条消息也必须属于一个topic。
	String topic1 = "Topic1";
	String topic2 = "tcrbs";//"Topic2";
	//消息标签，可用来做服务端消息过滤。一个topic下可以有很多tags，一般都通过topic+tags来消费自己想要的结果。
	String tags = "Tag1";
	//消息关键词，查询消息使用
	String keys = "沐紫剑测试";
	@Test
	public void 生产者() {
		try {
			RPCHook hook = new RPCHook(){
				public void doBeforeRequest(String remoteAddr, RemotingCommand request) {
				}
				public void doAfterResponse(String remoteAddr, RemotingCommand request, RemotingCommand response) {
					System.out.println("消息被接受" + remoteAddr + response);
				}
			};
			final DefaultMQProducer producer = new DefaultMQProducer("ProducerGroup1", null);
			producer.setNamesrvAddr(nameSrvAddr);
			producer.setInstanceName("Producer");
			producer.setRetryTimesWhenSendFailed(3);// 失败的 情况发送3次
			producer.setSendMessageWithVIPChannel(false);
			producer.start();
			System.out.println("------------生产者已启动------------");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			{
				String content = "这是一条同步消息" + sdf.format(new Date());
				byte[] body = content.getBytes();
				Message msg = new Message(topic1, tags, keys, body);
				SendResult sendResult = producer.send(msg);//同步
				System.out.println("发送消息：" + sendResult);
			}
			
			{
				String content = "这是一条异步消息" + sdf.format(new Date());
				byte[] body = content.getBytes();
				Message msg = new Message(topic2, tags, keys, body);
				SendCallback callback = new SendCallback() {
					public void onSuccess(SendResult sendResult) {
						System.out.println("发送消息成功：" + sendResult);
					}
					public void onException(Throwable e) {
						System.out.println("发送消息失败：" + e.getMessage());
					}
				};
				producer.send(msg, callback);//异步
			}
			
			sleep(5);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void 消费者_推送方式() {
		try {
			DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("ConsumerGroup1");
			consumer.setNamesrvAddr(nameSrvAddr);
			consumer.setInstanceName("PushConsumber");
			consumer.subscribe(topic1, tags);
			consumer.subscribe(topic2, tags);
			consumer.registerMessageListener(new MessageListenerConcurrently() {
				//consumeMessage异常被ConsumeMessageConcurrentlyService(414行)捕获造成spring声明式事务不能自动回滚
				public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
						ConsumeConcurrentlyContext context) {
					MessageExt msgExt = null;
					try {
						for (MessageExt msg : msgs) {
							msgExt = msg;
							MessageQueue mq = context.getMessageQueue();
							System.out.println("PushConsumber从" + mq + " 收到消息：" + msg.getTopic() + " "
									+ msg.getTags() + " " + new String(msg.getBody()));
							//业务逻辑处理，这里需要手工控制事务
						}
					} catch (Exception e) {
						e.printStackTrace();
						if(msgExt.getReconsumeTimes() <= 3) {
							return ConsumeConcurrentlyStatus.RECONSUME_LATER;// 失败重试3次
						}
					}
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				}
			});
			ConsumeMessageHook hook = new ConsumeMessageHook(){
				public String hookName() {
					return null;
				}
				public void consumeMessageBefore(ConsumeMessageContext context) {
				}
				public void consumeMessageAfter(ConsumeMessageContext context) {
					if(!context.isSuccess()) {
						System.err.println("后台处理异常");
//						throw new RuntimeException("后台处理异常");//抛异常的话不能失败重试了
					}
				}
			};
			consumer.getDefaultMQPushConsumerImpl().registerConsumeMessageHook(hook);
			consumer.start();
			System.out.println("------------消费者已启动------------");
			
			sleep(5);
		} catch (MQClientException e) {
			e.printStackTrace();
		}
	}

	private final Map<MessageQueue, Long> offseTable = new HashMap<MessageQueue, Long>();
	//如果nameserv返回的broker地址不符合则做映射关系对应
	private static Map<String,String> brokerMapping = new HashMap<String,String>();
	static {
		brokerMapping.put("172.16.100.221:10911", "180.107.140.229:10911");
	}
	@Test
	public void 消费者_拉取方式() {
		try {
			RPCHook hook = new RPCHook(){
				public void doBeforeRequest(String remoteAddr, RemotingCommand request) {
				}
				public void doAfterResponse(String remoteAddr, RemotingCommand request, RemotingCommand response) {
					try {
						if(response!= null && response.getBody() != null) {
							System.out.println("消息被接收" + remoteAddr + " " +  new String(response.getBody()));
							TopicRouteData trd = TopicRouteData.decode(response.getBody(), TopicRouteData.class);
							System.out.println("TopicRouteData:" + trd);
							// 修改nameserv返回的broker地址
							boolean hasChanged = false;
							if(trd != null) {
								for(BrokerData bd : trd.getBrokerDatas()) {
									HashMap<Long, String> idAddrMap = bd.getBrokerAddrs();
									for(Long id : idAddrMap.keySet()) {
										if(brokerMapping.containsKey(idAddrMap.get(id))) {
											idAddrMap.put(id, brokerMapping.get(idAddrMap.get(id)));
											hasChanged = true;
										}
									}
								}
							}
							if(hasChanged) {
								System.out.println("修改后的TopicRouteData:" + trd);
							}
							response.setBody(TopicRouteData.encode(trd));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("ConsumerGroup1", hook);
			consumer.setNamesrvAddr(nameSrvAddr);
			consumer.setInstanceName("PullConsumber");
			consumer.setVipChannelEnabled(false);
			consumer.setConsumerPullTimeoutMillis(1000*30);
			consumer.start();

			Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues(topic2);
			END_FETCH:
			for (MessageQueue mq : mqs) {
				SINGLE_MQ: while (true) {
					Long offset = offseTable.get(mq);
					System.out.println("offset=" + offset);
					PullResult pullResult = consumer.pullBlockIfNotFound(mq, null, offset == null ? 0 : offset, 32);
					offseTable.put(mq, pullResult.getNextBeginOffset());
					switch (pullResult.getPullStatus()) {
					case FOUND:
						List<MessageExt> msgs = pullResult.getMsgFoundList();
						for (MessageExt msg : msgs) {
							System.out.println("PullConsumber从" + mq + " 收到消息：[" + msg.getMsgId() + " " + msg.getTopic()
									+ " " + msg.getTags() + " " + msg.getKeys() + "] " + new String(msg.getBody()));
						}
						break;
					case NO_MATCHED_MSG:
						break;
					case NO_NEW_MSG:
						break SINGLE_MQ;//继续监听拉取本队列
//						break END_FETCH;
					case OFFSET_ILLEGAL:
						break;
					default:
						break;
					}// end switch
				}// while
			}//end for
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 休眠几分钟
	 * @param n
	 */
	private void sleep(int n) {
		try {
			Thread.sleep(1000*60*n);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
