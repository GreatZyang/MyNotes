package com.yuanqiao.rabbitmq.direct;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.GetResponse;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class BasicGetTest {

	private final static String EXCHANGE_NAME = "direct_exchange";

	public static void main(String[] args) throws IOException, TimeoutException {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("127.0.0.1");
		factory.setVirtualHost("/");
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setPort(5672);
		;
		Connection connection = factory.newConnection();// 连接
		Channel channel = connection.createChannel();// 信道
		channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);// direct交换器
		// 声明随机队列,为什么要用随机队列，而不自己声明队列呢？
		// String queueName = channel.queueDeclare().getQueue();
		String queueName = "directQueue";
		boolean durable = false;
		boolean exclusive = false;
		boolean autoDelete = false;
		Map<String, Object> arguments = null;
		DeclareOk queueDeclarePassive = channel.queueDeclare(queueName, durable, exclusive, autoDelete, arguments);

		String[] serverities = { "error", "info", "warning" };
		for (String server : serverities) {
			// 队列和交换器的绑定
			channel.queueBind(queueName, EXCHANGE_NAME, server);
		}
		System.out.println("Waiting message.......");

		Consumer callback = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println("Accept:" + envelope.getRoutingKey() + ":" + message);
				this.getChannel().basicAck(envelope.getDeliveryTag(), false);
			}
		};

		boolean autoAck = false;     //消息不自动确认，看消息是否会被删除
		GetResponse basicGet = channel.basicGet(queueName, autoAck); // 消费后断开连接。
		System.out.println("获取第一条消息：交换器："+basicGet.getEnvelope().getExchange()); // 获取到交换器
		System.out.println(basicGet.getEnvelope().getRoutingKey()); // 获取到路由建
		System.out.println(new String(basicGet.getBody())); // 获取到消息体
		basicGet = channel.basicGet(queueName, autoAck); // 消费后断开连接。
		System.out.println("获取第二条消息：交换器："+basicGet.getEnvelope().getExchange()); // 获取到交换器
		System.out.println(basicGet.getEnvelope().getRoutingKey()); // 获取到路由建
		System.out.println(new String(basicGet.getBody())); // 获取到消息体
		boolean multiple=true;
		//注意DeliveryTag是自增的
		channel.basicAck(basicGet.getEnvelope().getDeliveryTag(), multiple);//手动确认（第一条消息不确认，第二条消息批量确认）
		channel.close();
		connection.close();
		// channel.basicConsume(queueName,true,callback); //消费后不断开连接
		// channel.basicConsume(queueName,false,callback);

	}

}
