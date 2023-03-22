package com.example.gcppubsub

import com.google.cloud.spring.pubsub.core.PubSubTemplate
import com.google.cloud.spring.pubsub.integration.AckMode
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.channel.DirectChannel
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.MessageHandler

@Slf4j
@Configuration
class PubSubConfig {

    @Value('${gcp.pubsub.topic}')
    String pubSubTopic

    @Value('${gcp.pubsub.subscription}')
    String pubSubSubscription

    @Bean
    MessageChannel pubsubInputChannel() {
        return new DirectChannel()
    }

    @Bean
    PubSubInboundChannelAdapter messageChannelAdapter(@Qualifier('pubsubInputChannel') MessageChannel inputChannel,
                                                      PubSubTemplate pubSubTemplate) {

        PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, pubSubSubscription)
        adapter.setOutputChannel(inputChannel)
        adapter.setAckMode(AckMode.MANUAL)

        return adapter
    }

    @Bean
    @ServiceActivator(inputChannel = 'pubsubInputChannel')
    MessageHandler messageReceiver() {
        return (message) -> {
            log.info('Message arrived! Payload: ' + new String((byte[]) message.getPayload()))
            BasicAcknowledgeablePubsubMessage originalMessage = message.getHeaders()
                    .get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class)
            originalMessage.ack()
        }
    }

    @Bean
    @ServiceActivator(inputChannel = 'pubsubOutputChannel')
    MessageHandler messageSender(PubSubTemplate pubsubTemplate) {
        return new PubSubMessageHandler(pubsubTemplate, pubSubTopic)
    }

}
