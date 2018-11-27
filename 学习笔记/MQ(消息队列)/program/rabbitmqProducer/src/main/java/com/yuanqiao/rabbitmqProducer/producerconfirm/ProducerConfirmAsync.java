package com.yuanqiao.rabbitmqProducer.producerconfirm;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 发送方确认异步模式
 */
public class ProducerConfirmAsync {

	private final static String EXCHANGE_NAME = "producer_confirm";

	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
		/**
		 * 创建连接连接到MabbitMQ
		 */
		ConnectionFactory factory = new ConnectionFactory();

		// 设置MabbitMQ所在主机ip或者主机名
		factory.setHost("127.0.0.1");
		factory.setVirtualHost("/");
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setPort(5672);
		// 创建一个连接
		Connection connection = factory.newConnection();
		// 连接被关闭的时候，生产者调用的方法，可以写一些重连逻辑
		// connection.addShutdownListener();

		// 创建一个信道
		Channel channel = connection.createChannel();
		// 指定转发
		channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

		// 将信道设置为发送方确认模式
		channel.confirmSelect();

		// 信道被关闭
		// channel.addShutdownListener();

		// deliveryTag代表了（channel）唯一的投递
		// multiple:false
		channel.addConfirmListener(new ConfirmListener() {
			public void handleAck(long deliveryTag, boolean multiple) throws IOException {
				// handleAck表示rabbitmq正确应答了。
				System.out.println("Ack deliveryTag=" + deliveryTag + "，multiple:" + multiple);
				//这里可以更新本地数据表的状态。
				System.out.println("\n");
			}

			// 只有rabbitmq发生内部错误的时候才会调用
			public void handleNack(long deliveryTag, boolean multiple) throws IOException {
				System.out.println("Ack deliveryTag=" + deliveryTag + "，multiple:" + multiple);
			}
		});

		//channel.basicPublish的第三个参数就是mandatory
		// 1、mandatory参数为true，投递消息时无法找到一个合适的队列，调用如下的方法addReturnListener。消息返回给生产者
		// false 丢弃消息(缺省)，这个时候不会调用如下的方法。
		channel.addReturnListener(new ReturnListener() {
			public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
				String msg = new String(body);
				System.out.println("replyText:" + replyText);
				System.out.println("exchange:" + exchange);
				System.out.println("routingKey:" + routingKey);
				System.out.println("msg:" + msg);
			}
		});
		
		
		//信道被关闭的时候调用的方法。
		channel.addShutdownListener(new ShutdownListener() {
			
			@Override
			public void shutdownCompleted(ShutdownSignalException cause) {
				// TODO Auto-generated method stub
				
			}
		});
		


		String[] severities = { "error", "info", "warning" };
		for (int i = 0; i < 3; i++) {
			String severity = severities[i % 3];
			// 发送的消息
			String message = "Hello World_" + (i + 1) + ("_" + System.currentTimeMillis());
			//第三个参数决定是否会调用channel.addReturnListener方法。
//			channel.basicPublish(EXCHANGE_NAME, severity, false, null, message.getBytes());
			channel.basicPublish(EXCHANGE_NAME, severity, true, null, message.getBytes());
			// channel.basicPublish(EXCHANGE_NAME,ROUTE_KEY,null,msg.getBytes());
			System.out.println("----------------------------------------------------");
			System.out.println(" Sent Message: [" + severity + "]:'" + message + "'");			
			Thread.sleep(200);
		}

		// 关闭频道和连接
		channel.close();
		connection.close();
	}

}
