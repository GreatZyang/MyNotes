package com.yuanqiao.rabbitmq.direct;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;


public class DefaultDirect3 {

    public static void main(String[] argv) throws IOException,
            InterruptedException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
		factory.setVirtualHost("/");
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setPort(5672);
        Connection connection = factory.newConnection();//连接
        Channel channel = connection.createChannel();//信道
        String exchange="direct_exchange";
		channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT);
        String queue = "defaultQueue3";     //默认的交换机，名称为空白字符
		boolean durable = false;
		boolean exclusive = false;
		boolean autoDelete = false;
		Map<String, Object> arguments = null;
		channel.queueDeclare(queue, durable, exclusive, autoDelete, arguments);
        String routingKey="defaultRoutingKey3";
		//声明随机队列,为什么要用随机队列，而不自己声明队列呢？
//        String queueName = channel.queueDeclare().getQueue();
		channel.queueBind(queue, exchange, routingKey);
        System.out.println("Waiting message.......");

        Consumer consumerA = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body,"UTF-8");
                System.out.println("Accept:"+envelope.getRoutingKey()+":"+message);
            }
        };

        channel.basicConsume(queue,true,consumerA);

    }
}
