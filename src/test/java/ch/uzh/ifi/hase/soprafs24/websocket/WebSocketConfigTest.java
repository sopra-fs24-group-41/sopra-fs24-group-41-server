package ch.uzh.ifi.hase.soprafs24.websocket;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class WebSocketConfigTest {

    @Test
    void testConfigureClientInboundChannel() {
        // Arrange
        WebSocketConfig webSocketConfig = new WebSocketConfig();
        MessageChannel mockChannel = mock(MessageChannel.class);
        ChannelRegistration channelRegistration = spy(new ChannelRegistration());

        // Act
        webSocketConfig.configureClientInboundChannel(channelRegistration);

        // Capture the ChannelInterceptor
        ArgumentCaptor<ChannelInterceptor> argumentCaptor = ArgumentCaptor.forClass(ChannelInterceptor.class);
        verify(channelRegistration).interceptors(argumentCaptor.capture());
        ChannelInterceptor interceptor = argumentCaptor.getValue();

        // Assert for /topic/* destination
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setDestination("/topic/test");
        Message<?> message = MessageBuilder.createMessage(new byte[0], headerAccessor.getMessageHeaders());
        Message<?> returnedMessage = interceptor.preSend(message, mockChannel);
        assertNull(returnedMessage);
    }
}