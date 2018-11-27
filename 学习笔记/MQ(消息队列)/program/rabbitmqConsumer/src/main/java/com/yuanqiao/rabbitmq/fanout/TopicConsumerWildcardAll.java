package com.yuanqiao.rabbitmq.fanout;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

//使用通配符来实现，一个队列绑定多个路由建
public class TopicConsumerWildcardAll {
    private static final String EXCHANGE_NAME = "topic_logs";

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
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);//声明topic交换器
        //声明随机队列
//        String queueName = channel.queueDeclare().getQueue();
        String[]serverities = {"error.log","info.log","warning.log"};
        String queueName="queue1"; //
        boolean durable = false;
		boolean exclusive = false;  //是否是排外，队列是否自给自己使用。
		boolean autoDelete = false; //当最后一个消费者断开连接之后队列是否自动被删除
		Map<String, Object> arguments = null;
//		arguments.put("x-message-ttl", 10000); //延迟时间 （毫秒）
//		arguments.put("x-dead-letter-exchange", 10000); //延迟结束后指向交换机（死信收容交换机）
//		arguments.put("x-dead-letter-routing-key", 10000);//延迟结束后指向队列（死信收容队列），可直接设置queue name也可以设置routing-key
		 
		//声明队列
		channel.queueDeclare(queueName, durable, exclusive, autoDelete, arguments);
//		for(String server:serverities){
//            //队列和交换器的绑定,fanout交换器，同一个队列可以绑定多个交换器。
//            channel.queueBind(queueName,EXCHANGE_NAME,server);
//        }
		String routingKey = "*.log";   //使用通配符.和*和#来实现一个队列绑定多个路由建。而不用循环绑定了
		channel.queueBind(queueName,EXCHANGE_NAME,routingKey);
        System.out.println("Waiting message.......");

        Consumer callback = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body,"UTF-8");
                System.out.println("Accept:"+envelope.getRoutingKey()+":"+message);
            }
        };

        channel.basicConsume(queueName,true,callback);

    }
}
