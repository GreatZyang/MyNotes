package com.yuanqiao.rabbitmqProducer.consumerconfirm;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 发送方确认同步模式
 */
public class ConsumerConfirmProducer {

    private final static String EXCHANGE_NAME = "direct_cc_confirm_1";
    private final static String ROUTE_KEY = "error";

    public static void main(String[] args) throws IOException, TimeoutException,
            InterruptedException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
/*        factory.setUsername(..);
        factory.setPort();
        factory.setVirtualHost();*/


        Connection connection = factory.newConnection();//连接

        Channel channel = connection.createChannel();//信道

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);//交换器

        for(int i=0;i<10;i++){
            String message = "Hello world_"+(i+1);

            channel.basicPublish(EXCHANGE_NAME,ROUTE_KEY,null,message.getBytes());
            System.out.println("Sent "+ROUTE_KEY+":"+message);

        }

        channel.close();
        connection.close();

    }

}
