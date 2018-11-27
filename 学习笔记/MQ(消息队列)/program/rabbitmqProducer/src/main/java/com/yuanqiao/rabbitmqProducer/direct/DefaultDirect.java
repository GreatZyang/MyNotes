package com.yuanqiao.rabbitmqProducer.direct;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class DefaultDirect {

	private final static String EXCHANGE_NAME = "directExchange";

	public static void main(String[] args) throws IOException, TimeoutException {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("127.0.0.1");
		factory.setVirtualHost("/");
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setPort(5672);

		Connection connection = factory.newConnection();// 连接

		Channel channel = connection.createChannel();// 信道

		channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);// 交换器

		String message = "Hello world";
		String routingKey="defaultRoutingKey";
		channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
		System.out.println("Sent "+routingKey+":" + message);

		channel.close();
		connection.close();

	}

}
