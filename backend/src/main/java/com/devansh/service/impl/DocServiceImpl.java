package com.devansh.service.impl;

import com.devansh.engine.Crdt;
import com.devansh.engine.CrdtManagerService;
import com.devansh.exception.ResourceNotFoundException;
import com.devansh.exception.UnauthorizedUserException;
import com.devansh.exception.UserException;
import com.devansh.mapper.DocumentChangeMapper;
import com.devansh.mapper.DocumentMapper;
import com.devansh.mapper.UserDocMapper;
import com.devansh.mapper.UserMapper;
import com.devansh.model.Doc;
import com.devansh.model.User;
import com.devansh.model.UserDoc;
import com.devansh.model.UserDocId;
import com.devansh.repo.DocRepository;
import com.devansh.repo.UserDocRepository;
import com.devansh.repo.UserRepository;
import com.devansh.request.DocTitleDto;
import com.devansh.response.DocumentChangeDto;
import com.devansh.response.DocumentDto;
import com.devansh.response.UserDocDto;
import com.devansh.security.SecurityUtil;
import com.devansh.service.DocAuthorizationService;
import com.devansh.service.DocService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocServiceImpl implements DocService {

    private final UserRepository userRepository;
    private final DocRepository docRepository;
    private final UserDocRepository userDocRepository;

    private final UserMapper userMapper;
    private final DocumentMapper documentMapper;
    private final DocumentChangeMapper documentChangeMapper;
    private final UserDocMapper userDocMapper;

    private final DocAuthorizationService docAuthorizationService;
    private final CrdtManagerService crdtManagerService;

    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found with email: " + email));
        return user;
    }

    private Doc getDocById(Long docId) throws ResourceNotFoundException {
        return docRepository.findById(docId)
                .orElseThrow(() -> new ResourceNotFoundException("Doc not found with id: " + docId));
    }

    @Transactional
    @Override
    public DocumentDto createDoc(DocTitleDto titleDto) {
        String title = titleDto.getTitle();
        User user = getCurrentUser();

        Doc doc = Doc.builder()
                .owner(user)
                .title(title)
                .content(new byte[0])
                .sharedWith(new ArrayList<>())
                .build();
        Doc savedDoc = docRepository.save(doc);

        return documentMapper.toDocumentDto(savedDoc);
    }

    @Transactional
    @Override
    public Long deleteDoc(Long docId) throws ResourceNotFoundException, UnauthorizedUserException {
        Doc doc = getDocById(docId);
        User user = getCurrentUser();
        if (!docAuthorizationService.fullAccess(user.getDisplayName(), doc)) {
            throw new UnauthorizedUserException("You are not authorized to delete this document.");
        }
        docRepository.deleteById(docId);
        return docId;
    }

    @Transactional
    @Override
    public String updateDocTitle(Long docId, DocTitleDto titleDto) throws ResourceNotFoundException, UnauthorizedUserException {
        Doc doc = getDocById(docId);
        String username = getCurrentUser().getDisplayName();
        if (!docAuthorizationService.canEdit(username, doc)) {
            throw new UnauthorizedUserException("You are not authorized to edit the title.");
        }
        doc.setTitle(titleDto.getTitle());
        docRepository.save(doc);
        return "Title updated successfully";
    }

    // anyone with edit permission can add user with the same permission.
    @Transactional
    @Override
    public UserDocDto addUser(Long docId, UserDocDto userDocDto) throws ResourceNotFoundException, UnauthorizedUserException, UserException {
        Doc doc = getDocById(docId);
        User user = userRepository.findByUsername(userDocDto.getUsername())
                .orElseThrow(() -> new UserException("User not found with username: " + userDocDto.getUsername()));
        String username = getCurrentUser().getDisplayName();
        if (!docAuthorizationService.canEdit(username, doc)) {
            throw new UnauthorizedUserException("You are not authorized to share this document");
        }
        UserDocId userDocId = UserDocId.builder()
                .docId(docId)
                .username(user.getDisplayName())
                .build();
        UserDoc userDoc = UserDoc.builder()
                .userDocId(userDocId)
                .user(user)
                .doc(doc)
                .permission(userDocDto.getPermission())
                .build();
        userDocRepository.save(userDoc);
        return userDocDto;
    }

    @Transactional
    @Override
    public List<UserDocDto> getSharedUsers(Long docId) throws ResourceNotFoundException {
        Doc doc = getDocById(docId);

        return doc.getSharedWith().stream()
                .map(userDocMapper::toUserDocDto)
                .toList();
    }

    // only owner of the document can remove the user.
    @Transactional
    @Override
    public String removeUser(Long docId, UserDocDto userDocDto) throws ResourceNotFoundException, UnauthorizedUserException {
        String username = getCurrentUser().getDisplayName();
        Doc doc = getDocById(docId);
        if (!docAuthorizationService.fullAccess(username, doc)) {
            throw new UnauthorizedUserException("Only owner of the document can remove users from this document");
        }
        int isDeleted = userDocRepository.deleteUserDocBy(userDocDto.getUsername(), docId, username);
        return isDeleted != 0 ? "User removed Successfully!" : "User not found";
    }

    // only owner of the document can update the permission
    @Transactional
    @Override
    public String updatePermission(Long docId, UserDocDto userDocDto) throws ResourceNotFoundException, UnauthorizedUserException {
        validatePermission(userDocDto);
        String username = getCurrentUser().getDisplayName();
        Doc doc = getDocById(docId);

        if (!docAuthorizationService.canEdit(username, doc)) {
            throw new UnauthorizedUserException("You are not allowed to update permission for this document");
        }
        int isUpdated = userDocRepository.updateUserDocBy(
                userDocDto.getUsername(),
                docId,
                username,
                userDocDto.getPermission()
        );
        if (isUpdated != 0) {
            return "Permission updated successfully";
        } else {
            throw new UnauthorizedUserException("You are not authorized to update the permission");
        }
    }

    @Transactional
    @Override
    public List<DocumentDto> getAllDocs() {
        String username = getCurrentUser().getDisplayName();

        List<Doc> myDocs = docRepository.findByUsername(username);
        return myDocs.stream()
                .map(documentMapper::toDocumentDto)
                .toList();
    }

    @Transactional
    @Override
    public List<DocumentChangeDto> getDocChanges(Long docId) throws ResourceNotFoundException {
        Crdt crdt = crdtManagerService.getCrdt(docId);
        if (crdt == null) {
            Doc doc = getDocById(docId);
            crdt = new Crdt(doc.getContent());
        }
        return crdt.getItems().stream()
                .map(documentChangeMapper::toDocumentChangeDto)
                .toList();
    }

    @Override
    public DocumentDto getDoc(Long docId) throws ResourceNotFoundException {
        Doc doc = getDocById(docId);
        return documentMapper.toDocumentDto(doc);
    }

    private void validatePermission(UserDocDto userDocDto) {
        int permission = userDocDto.getPermission().ordinal();
        if (permission < 0 || permission > 2) {
            throw new IllegalArgumentException("Invalid permission: " + permission);
        }
    }
}
