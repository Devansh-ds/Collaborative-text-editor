package com.devansh.service;

import com.devansh.exception.ResourceNotFoundException;
import com.devansh.exception.UnauthorizedUserException;
import com.devansh.exception.UserException;
import com.devansh.request.DocTitleDto;
import com.devansh.response.DocumentChangeDto;
import com.devansh.response.DocumentDto;
import com.devansh.response.UserDocDto;

import java.util.List;

public interface DocService {

    DocumentDto createDoc(DocTitleDto titleDto);
    Long deleteDoc(Long docId) throws ResourceNotFoundException, UnauthorizedUserException;
    String updateDocTitle(Long docId, DocTitleDto titleDto) throws ResourceNotFoundException, UnauthorizedUserException;
    UserDocDto addUser(Long docId, UserDocDto userDocDto) throws ResourceNotFoundException, UnauthorizedUserException, UserException;
    List<UserDocDto> getSharedUsers(Long docId) throws ResourceNotFoundException;
    String removeUser(Long docId, UserDocDto userDocDto) throws ResourceNotFoundException, UnauthorizedUserException;
    String updatePermission(Long id, UserDocDto userDocDto) throws ResourceNotFoundException, UnauthorizedUserException;
    List<DocumentDto> getAllDocs();
    List<DocumentChangeDto> getDocChanges(Long docId) throws ResourceNotFoundException;
    DocumentDto getDoc(Long docId) throws ResourceNotFoundException;

}
