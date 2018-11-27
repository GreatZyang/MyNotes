package com.yuanqiao.rabbitmq.consumerconfirm;

import com.rabbitmq.client.*;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ClientConsumerAck {

	private static final String EXCHANGE_NAME = "direct_cc_confirm_1";

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
		String queueName = "consumer_confirm";
		channel.queueDeclare(queueName, false, false, false, null);
//		DeclareOk queueDeclarePassive = channel.queueDeclarePassive(queueName);
		// 声明随机队列
		String tempqueueName = channel.queueDeclare().getQueue();
		String server = "error";
		channel.queueBind(queueName, EXCHANGE_NAME, server);
		System.out.println("Waiting message.......");

		Consumer callback = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println("Accept:" + envelope.getRoutingKey() + ":" + message);
				// 消费者处理完消息后，手动应答。
				this.getChannel().basicAck(envelope.getDeliveryTag(), false);
				//basicReject就是拒绝回答，第二个参数表示拒绝后，是否该消息继续投递到其他消费者
//            	this.getChannel().basicReject(envelope.getDeliveryTag(),false);
			}
		};

//		channel.basicGet(queueName, false);
		channel.basicConsume(queueName, false, callback);
	}

}
