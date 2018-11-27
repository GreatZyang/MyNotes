package com.yuanqiao.rabbitmqProducer.message;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class DirectProducerNotComfirm {

    private final static String EXCHANGE_NAME = "direct_not_confirm";

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
		factory.setVirtualHost("/");
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setPort(5672);


        Connection connection = factory.newConnection();//连接

        Channel channel = connection.createChannel();//信道

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);//交换器

        String[]serverities = {"error","info","warning"};

        for(int i=0;i<3;i++){
            String server = serverities[i];
            String message = "Hello world_"+(i+1);
            //BasicProperties props=new BasicProperties(contentType, contentEncoding, headers, deliveryMode, priority, correlationId, replyTo, expiration, messageId, timestamp, type, userId, appId, clusterId)
            MessageProperties.BASIC.getDeliveryMode();
            MessageProperties.MINIMAL_BASIC.getDeliveryMode();
            MessageProperties.TEXT_PLAIN.getDeliveryMode();
            //后面三种模式的的deliveryMode都是2，表示消息可以持久化
            MessageProperties.MINIMAL_PERSISTENT_BASIC.getDeliveryMode();
            MessageProperties.PERSISTENT_BASIC.getDeliveryMode();
            MessageProperties.PERSISTENT_TEXT_PLAIN.getDeliveryMode(); //获取投递模式
            channel.basicPublish(EXCHANGE_NAME,server,MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());
            System.out.println("Sent "+server+":"+message);

        }

        channel.close();
        connection.close();




    }

}
