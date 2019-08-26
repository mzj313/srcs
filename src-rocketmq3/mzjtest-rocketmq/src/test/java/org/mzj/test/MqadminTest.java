package org.mzj.test;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.alibaba.rocketmq.broker.BrokerStartup;
import com.alibaba.rocketmq.common.MixAll;
import com.alibaba.rocketmq.common.UtilAll;
import com.alibaba.rocketmq.namesrv.NamesrvStartup;
import com.alibaba.rocketmq.tools.command.MQAdminStartup;

public class MqadminTest {
	@Before
	public void setUp() {
		//设置环境变量
		System.setProperty(MixAll.ROCKETMQ_HOME_PROPERTY, "D:\\Java\\opensource\\rocketmq\\alibaba-rocketmq");
	}
	
	@Test
	public void ServerStart() {
		NamesrvStartup.main(null);
		BrokerStartup.main("-n 127.0.0.1:9876 -c src/main/resources/broker.properties".split(" "));
		
		try {
			TimeUnit.MINUTES.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void updateTopic() {
		String args = "updateTopic -b rocketmq-cluster -n fme.dev.hubpd.com:9876 -t tcrbs";
		MQAdminStartup.main(args.split(" "));
	}
	
	@Test
	public void printMsg() {
    	String args = "printMsg -n 127.0.0.1:9876 -t tcrbs -b 2018-01-24#12:00:00:000 -e 2018-01-25#17:10:00:000";
//    	args = "queryMsgByKey -n 127.0.0.2:9876 -k 001 -t Topic1".split(" ");
		MQAdminStartup.main(args.split(" "));
	}
	
	@Test
	public void queryMsgById() {
		String msgId = "C0A8211200002A9F0000000000022F13";
		int port = ByteBuffer.wrap(UtilAll.string2bytes(msgId.substring(8, 16))).getInt(0);
		System.out.println(port);
		//调用queryMsgById传的并不是msgid而是UNIQ_KEY
    	String args = "queryMsgById -n 127.0.0.1:9876 -i C0A8211200002A9F0000000000022F13";
		MQAdminStartup.main(args.split(" "));
	}
}
