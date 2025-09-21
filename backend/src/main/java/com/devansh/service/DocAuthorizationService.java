package com.devansh.service;

import com.devansh.model.Doc;

public interface DocAuthorizationService {
    boolean canAccess(Long docId);
    boolean canEdit(String username, Doc doc);
    boolean fullAccess(String username, Doc doc);
}
