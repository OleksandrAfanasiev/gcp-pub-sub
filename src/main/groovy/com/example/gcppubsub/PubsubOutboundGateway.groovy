package com.example.gcppubsub

import org.springframework.integration.annotation.MessagingGateway


@MessagingGateway(defaultRequestChannel = 'pubsubOutputChannel')
interface PubsubOutboundGateway {

    void sendToPubsub(String text)
}