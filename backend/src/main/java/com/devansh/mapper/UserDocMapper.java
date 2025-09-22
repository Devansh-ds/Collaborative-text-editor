package com.devansh.mapper;

import com.devansh.model.UserDoc;
import com.devansh.response.UserDocDto;
import org.springframework.stereotype.Service;

@Service
public class UserDocMapper {

    public UserDocDto toUserDocDto(UserDoc userDoc) {
        return UserDocDto.builder()
                .username(userDoc.getUser().getDisplayName())
                .permission(userDoc.getPermission())
                .build();
    }

}
