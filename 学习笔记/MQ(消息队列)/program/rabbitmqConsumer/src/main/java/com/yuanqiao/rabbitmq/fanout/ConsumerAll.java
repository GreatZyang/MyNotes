package com.yuanqiao.rabbitmq.fanout;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class ConsumerAll {
    //private static final String EXCHANGE_NAME = "direct_logs";
    private static final String EXCHANGE_NAME = "fanout_logs_1";

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
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);//交换器
        //声明随机队列
        String queueName = channel.queueDeclare().getQueue();
        String[]serverities = {"error","info","warning"};
        for(String server:serverities){
            //队列和交换器的绑定,fanout交换器，同一个队列可以绑定多个交换器。
            channel.queueBind(queueName,EXCHANGE_NAME,server);
        }
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

        channel.basicConsume(queueName,true,consumerA);

    }
}
