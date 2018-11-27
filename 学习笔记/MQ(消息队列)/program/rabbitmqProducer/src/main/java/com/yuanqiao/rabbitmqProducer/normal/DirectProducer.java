package com.yuanqiao.rabbitmqProducer.normal;

import com.rabbitmq.client.AMQP.Queue.BindOk;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class DirectProducer {

	private final static String EXCHANGE_NAME = "direct_exchange";

	public static void main(String[] args) throws IOException, TimeoutException {

		com.rabbitmq.client.ConnectionFactory factory = new ConnectionFactory(); // 连接工厂
		factory.setHost("127.0.0.1");
		factory.setVirtualHost("/");
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setPort(5672);

		com.rabbitmq.client.Connection connection = factory.newConnection();// 连接

		com.rabbitmq.client.Channel channel = connection.createChannel();// 信道

		channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);// 交换器
//		String queueName = "directQueue";
//		DeclareOk queueDeclarePassive = null;
//		// try {
//		// queueDeclarePassive = channel.queueDeclarePassive(queueName);
//		// //看队列是否声明过
//		// } catch (Exception e) {
//		// }
//		String queue = queueName;
//		boolean durable = false;
//		boolean exclusive = false;
//		boolean autoDelete = false;
//		Map<String, Object> arguments = null;
//		queueDeclarePassive = channel.queueDeclare(queue, durable, exclusive, autoDelete, arguments);
		// System.out.println(queueDeclarePassive.getConsumerCount());
		// //获取当前队列有多少个消费者
		// System.out.println(queueDeclarePassive.getMessageCount());
		// //获取当前队列有多少个消息
		String[] serverities = { "defaultRoutingKey1", "defaultRoutingKey2", "defaultRoutingKey3" };
//		for (int i = 0; i < 3; i++) {
//			BindOk queueBind = channel.queueBind(queue, EXCHANGE_NAME, serverities[i], null);
//		}

		for (int i = 0; i < 3; i++) {
			String server = serverities[i];
			String message = "Hello world_" + (i + 1);
			channel.basicPublish(EXCHANGE_NAME, server, null, message.getBytes()); // 发布消息
			System.out.println("Sent " + server + ":" + message);
		}

		channel.close();
		connection.close();

	}

}
