package com.yuanqiao.rabbitmq.direct;

import com.rabbitmq.client.*;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 测试消费者autoACK设置为false，而且不手动应答的情况下，发送到Rabbtimq的消息是否会被删除？
 * @author yuanqiao
 *
 */
public class ClientConsumerNotAck {

	private static final String EXCHANGE_NAME = "direct_not_confirm";

	public static void main(String[] argv) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("127.0.0.1");
		factory.setVirtualHost("/");
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setPort(5672);
		Connection connection = factory.newConnection();// 连接
		Channel channel = connection.createChannel();// 信道
		channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);// 交换器

		// 声明队列
		String queueName = "consumer_not_confirm";
		channel.queueDeclare(queueName, false, false, false, null);
//		DeclareOk queueDeclarePassive = channel.queueDeclarePassive(queueName);
		// 声明随机队列
//		String tempqueueName = channel.queueDeclare().getQueue();
		String server = "error";
		channel.queueBind(queueName, EXCHANGE_NAME, server);
		System.out.println("Waiting message.......");

		Consumer consumerB = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println("Accept:" + envelope.getRoutingKey() + ":" + message);
				// 消费者处理完消息后，手动应答,如果我不应答，消息是否会一直存在rabbitmq里面？
				this.getChannel().basicAck(envelope.getDeliveryTag(), false);
			}
		};

		channel.basicConsume(queueName, false, consumerB);
	}

}
