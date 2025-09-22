package com.devansh.config;

import com.devansh.exception.TokenInvalidException;
import com.devansh.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
public class WebSocketAuthenticationConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor acessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (acessor != null) {
                    String authHeader = acessor.getFirstNativeHeader("Authentication");
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String jwtToken = authHeader.substring("Bearer ".length()).trim();
                        String userEmail = jwtService.extractUsername(jwtToken);
                        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                        if (!jwtService.validateToken(jwtToken, userDetails)) {
                            throw new RuntimeException("JWT expired/invalid, closing ws connection!");
                        }

                        // attach auth if valid
                        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        acessor.setUser(authentication);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
                return message;
            }
        });
    }

}












