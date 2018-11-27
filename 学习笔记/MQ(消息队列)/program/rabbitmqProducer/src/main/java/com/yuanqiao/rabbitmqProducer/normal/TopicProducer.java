package com.yuanqiao.rabbitmqProducer.normal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class TopicProducer {
	public static String EXCHANGE="topic_exchange";
	public static BuiltinExchangeType EXCHANGE_TYPE=BuiltinExchangeType.TOPIC;
	public static void main(String[] args) throws Exception {
		ConnectionFactory connectionFactory=new ConnectionFactory();
		connectionFactory.setHost("127.0.0.1");
		connectionFactory.setVirtualHost("/");
		connectionFactory.setUsername("guest");
		connectionFactory.setPassword("guest");
		connectionFactory.setPort(5672);
		Connection connection = connectionFactory.newConnection();
		Channel channel = connection.createChannel();
		
		//声明交换机和交换机类型
		channel.exchangeDeclare(EXCHANGE, EXCHANGE_TYPE);

		// String queue = null;
		// boolean durable = false;
		// boolean exclusive = false;
		// boolean autoDelete = false;
		// Map<String, Object> arguments = null;
		// channel.queueDeclare(queue, durable, exclusive, autoDelete,
		// arguments);
		
		
		//channel.addConfirmListener方法想要执行，必须是生产者确认模式
		channel.confirmSelect();
		channel.addConfirmListener(new ConfirmListener() {
			
			@Override
			public void handleNack(long deliveryTag, boolean multiple)
					throws IOException {
				// TODO Auto-generated method stub
				System.out.println("deliveryTag:"+deliveryTag+",消息成功发送到rabbitmq了，但是rabbitmq接受异常了");
			}
			
			@Override
			public void handleAck(long deliveryTag, boolean multiple)
					throws IOException {
				// TODO Auto-generated method stub
				System.out.println("deliveryTag:"+deliveryTag+",消息成功发送到rabbitmq了，并且rabbitmq确认消息了");
			}
		});
		boolean mandatory = false;
		BasicProperties props = null;
		Set<String> routingKeys=new HashSet<String>();
		routingKeys.add("error.log");
		routingKeys.add("info.log");
		routingKeys.add("warning.log");
		routingKeys.add("error.email");
		routingKeys.add("info.email");
		routingKeys.add("warning.email");
		// channel.basicPublish(exchange, routingKey, mandatory, immediate, props, body);
		// channel.basicPublish(exchange, routingKey, props, body);
		String nowDateTime=new TopicProducer().getDateTime();
		for (Iterator iterator = routingKeys.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			byte[] body = new String("["+nowDateTime+"] a log message of "+string).getBytes();
			channel.basicPublish(EXCHANGE, string, mandatory, props, body);
			Thread.sleep(200);       //为了让上面的方法执行完：handleAck
		}
		channel.close();
		connection.close();
	}
	
	
	public String getDateTime(){
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat();
		simpleDateFormat.applyPattern("YYYYMMDDHHmmss");
		String format = simpleDateFormat.format(new Date());
		return format;
		
	}
}
