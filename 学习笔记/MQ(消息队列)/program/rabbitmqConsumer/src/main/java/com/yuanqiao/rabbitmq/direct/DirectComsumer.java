package com.yuanqiao.rabbitmq.direct;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.GetResponse;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class DirectComsumer {

	private final static String EXCHANGE_NAME = "direct_exchange";

	public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setVirtualHost("/");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPort(5672);;
        Connection connection = factory.newConnection();//连接
        Channel channel = connection.createChannel();//信道
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);//direct交换器
        //声明随机队列,为什么要用随机队列，而不自己声明队列呢？
//        String queueName = channel.queueDeclare().getQueue();
        String queueName="directQueue";
        boolean durable = false;
        boolean exclusive = false;
        boolean autoDelete = false;
        Map<String, Object> arguments = null;
        DeclareOk queueDeclarePassive = channel.queueDeclare(queueName, durable, exclusive, autoDelete, arguments);
        
        String[]serverities = {"error","info","warning"};
        for(String server:serverities){
            //队列和交换器的绑定
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
                this.getChannel().basicAck(envelope.getDeliveryTag(), false);
            }
        };

        GetResponse basicGet = channel.basicGet(queueName, true);    //消费后断开连接。
        System.out.println(basicGet.getEnvelope().getExchange());     //获取到交换器
        System.out.println( basicGet.getEnvelope().getRoutingKey());     //获取到路由建
        System.out.println(new String(basicGet.getBody()));     //获取到消息体
        channel.close();
        connection.close();
//        channel.basicConsume(queueName,true,consumerA);     //消费后不断开连接
//        channel.basicConsume(queueName,false,consumerA);

    }

}
