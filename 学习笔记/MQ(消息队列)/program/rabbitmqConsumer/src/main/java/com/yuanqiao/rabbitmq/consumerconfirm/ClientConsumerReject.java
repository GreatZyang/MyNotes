package com.yuanqiao.rabbitmq.consumerconfirm;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class ClientConsumerReject {

    private static final String EXCHANGE_NAME = "direct_cc_confirm_1";

    public static void main(String[] argv) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
		factory.setVirtualHost("/");
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setPort(5672);
        Connection connection = factory.newConnection();//连接
        Channel channel = connection.createChannel();//信道
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);//交换器

        //声明队列
        String queueName = "consumer_confirm";
        channel.queueDeclare(queueName,false,false,
                false,null);

        String server = "error";
        channel.queueBind(queueName,EXCHANGE_NAME,server);
        System.out.println("Waiting message.......");

        Consumer consumerB = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
            	//basicReject就是拒绝回答，第二个参数表示拒绝后，是否该消息继续投递到其他消费者
            	this.getChannel().basicReject(envelope.getDeliveryTag(),false);
//                this.getChannel().basicReject(envelope.getDeliveryTag(),true);
                System.out.println("Reject:"+envelope.getRoutingKey()
                        +":"+new String(body,"UTF-8"));
            }
        };

        channel.basicConsume(queueName,false,consumerB);
    }

}
