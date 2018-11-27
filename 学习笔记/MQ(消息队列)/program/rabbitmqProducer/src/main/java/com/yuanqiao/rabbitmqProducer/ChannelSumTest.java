package com.yuanqiao.rabbitmqProducer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


//测试一个连接最多可以创建多少个信道
public class ChannelSumTest {
	private final static String EXCHANGE_NAME = "direct_logs";

	public static void main(String[] args) throws IOException, TimeoutException {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("127.0.0.1");
		factory.setVirtualHost("/");
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setPort(5672);

		Connection connection = factory.newConnection();// 连接
		int i=1;
		while(i<10000){
			Channel channel = connection.createChannel();// 信道
			channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);//交换器
			String message = "Hello world_"+(i+1);
			String routingKey="error";
			channel.basicPublish(EXCHANGE_NAME,routingKey,null,message.getBytes());
			System.out.println("this is the number "+i);
			i++;
		}
	}
}
