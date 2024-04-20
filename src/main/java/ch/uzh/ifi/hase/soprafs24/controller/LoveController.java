package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.websocket.Message;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class LoveController {

    private final SimpMessagingTemplate messagingTemplate;

    private final UserService userService;

    LoveController(SimpMessagingTemplate messagingTemplate, UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
    }

    @PostMapping("/greetings")
    @ResponseStatus(HttpStatus.OK)
    public void greet(@RequestHeader String userToken) {
        User checkedUser = userService.checkToken(userToken);
        if (checkedUser == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        messagingTemplate.convertAndSend("/topic/greetings", new Message(checkedUser.getUsername() + " says I love you"));
    }

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Message forwardMessage(Message message) {
        return message;
    }

}
