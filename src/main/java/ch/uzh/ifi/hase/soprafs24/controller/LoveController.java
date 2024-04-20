package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
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

        messagingTemplate.convertAndSend("/topic/greetings", "{'message': '" + checkedUser.getUsername() + " says I love you'}");
        System.out.println("greeted");
    }

    @Scheduled(fixedRate = 10000)
    public void scheduledTest() {
        messagingTemplate.convertAndSend("/topic/greetings", "{\"message\": \"Hello World\"}");
        messagingTemplate.convertAndSend("/topic/greetings2", "{\"message\": \"Hello World2\"}");
    }
}
