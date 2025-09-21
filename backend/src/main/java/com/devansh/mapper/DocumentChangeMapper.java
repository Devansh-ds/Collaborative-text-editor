package com.devansh.mapper;

import com.devansh.engine.Item;
import com.devansh.response.DocumentChangeDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentChangeMapper {

    public DocumentChangeDto toDocumentChangeDto(Item item) {
        return DocumentChangeDto.builder()
                .id(item.getId())
                .left(item.getLeft().getId())
                .right(item.getRight().getId())
                .content(item.getContent())
                .operation(item.getOperation())
                .isBold(item.isBold())
                .isItalic(item.isItalic())
                .isDeleted(item.isDeleted())
                .build();
    }
}
