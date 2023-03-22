package com.example.gcppubsub


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

@RestController
class PubSubController {

    @Autowired
    private PubsubOutboundGateway messagingGateway

    @PostMapping("/publishMessage")
    RedirectView publishMessage(@RequestParam("message") String message) {
        messagingGateway.sendToPubsub(message)
        return new RedirectView("/")
    }
}
