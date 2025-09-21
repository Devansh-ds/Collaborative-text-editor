package com.devansh.service.impl;

import com.devansh.model.Doc;
import com.devansh.model.User;
import com.devansh.model.enums.Permission;
import com.devansh.repo.UserRepository;
import com.devansh.security.SecurityUtil;
import com.devansh.service.DocAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DocAuthorizationServiceImpl implements DocAuthorizationService {

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return user;
    }

    // todo: check for long or string docId
    @Override
    public boolean canAccess(Long docId) {
        User user = getCurrentUser();
        return user.getAccessDoc()
                .stream()
                .anyMatch(userDoc -> userDoc.getDoc().getId().equals(docId));
    }

    @Override
    public boolean canEdit(String username, Doc doc) {
        return doc.getOwner().getUsername().equals(username) ||
                doc.getSharedWith()
                        .stream()
                        .anyMatch(userDoc ->
                                userDoc.getUser().getUsername().equals(username) && userDoc.getPermission().equals(Permission.EDIT)
                        );
    }

    @Override
    public boolean fullAccess(String username, Doc doc) {
        return doc.getOwner().getUsername().equals(username);
    }
}
