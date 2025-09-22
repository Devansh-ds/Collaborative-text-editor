package com.devansh.controller;

import com.devansh.engine.Crdt;
import com.devansh.engine.CrdtManagerService;
import com.devansh.engine.Item;
import com.devansh.mapper.DocumentChangeMapper;
import com.devansh.request.CursorDto;
import com.devansh.response.DocumentChangeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class DocWebSocketController {

    private final CrdtManagerService crdtManagerService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/change/{id}")
    public void onChange(@DestinationVariable String id, DocumentChangeDto message) {
        Crdt crdt = crdtManagerService.getCrdt(Long.parseLong(id));
        if (message.getOperation().equals("delete")) {
            crdt.delete(message.getId());
        } else if (message.getOperation().equals("insert")) {
            Item item = Item.builder()
                    .id(message.getId())
                    .content(message.getContent())
                    .right(crdt.getItem(message.getRight()))
                    .left(crdt.getItem(message.getLeft()))
                    .operation(message.getOperation())
                    .isBold(message.isBold())
                    .isItalic(message.isItalic())
                    .isDeleted(message.isDeleted())
                    .build();
            crdt.insert(item);
        } else {
            crdt.format(message.getId(), message.isBold(), message.isItalic());
        }
        messagingTemplate.convertAndSend("/docs/broadcast/changes/" + id, message);
    }

    @MessageMapping("/cursor/{id}")
    public void cursor(@DestinationVariable String id, CursorDto message) {
        messagingTemplate.convertAndSend("/docs/broadcast/cursors/" + id, message);
    }

    @MessageMapping("/username/{id}")
    public void usernames(@DestinationVariable String id, String message) {
        messagingTemplate.convertAndSend("/docs/broadcast/usernames/" + id, message);
    }


}



















