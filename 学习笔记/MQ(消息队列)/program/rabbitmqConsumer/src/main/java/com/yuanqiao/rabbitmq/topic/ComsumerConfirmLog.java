package com.yuanqiao.rabbitmq.topic;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

//处理error.log和error.email消息
public class ComsumerConfirmLog {
	public static String EXCHANGE = "topic_exchange";
	public static BuiltinExchangeType EXCHANGE_TYPE = BuiltinExchangeType.TOPIC;
	private static final String queueName = "allLogQueue";

	public static void main(String[] argv) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("127.0.0.1");
		factory.setVirtualHost("/");
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setPort(5672);
		// 打开连接和创建频道，与发送端一样
		Connection connection = factory.newConnection();
		final Channel channel = connection.createChannel();

		channel.exchangeDeclare(EXCHANGE, EXCHANGE_TYPE);

		channel.queueDeclare(queueName, false, false, false, null);

		String routingKey = "#.log";// 只关注log级别的日志
		channel.queueBind(queueName, EXCHANGE, routingKey); // 绑定路由建

		System.out.println("  Waiting for messages......");

		// 创建队列消费者
		final Consumer consumerB = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println("Received [" + envelope.getRoutingKey()
						+ "] " + message);
			}
		};
		channel.basicConsume(queueName, true, consumerB);
	}
}
