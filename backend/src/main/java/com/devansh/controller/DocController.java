package com.devansh.controller;

import com.devansh.exception.ResourceNotFoundException;
import com.devansh.exception.UnauthorizedUserException;
import com.devansh.exception.UserException;
import com.devansh.request.DocTitleDto;
import com.devansh.response.DocumentChangeDto;
import com.devansh.response.DocumentDto;
import com.devansh.response.UserDocDto;
import com.devansh.service.DocService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/docs")
@RequiredArgsConstructor
public class DocController {

    private final DocService docService;

    @PostMapping("/create")
    public ResponseEntity<DocumentDto> createDoc(@RequestBody DocTitleDto docTitleDto) {
        return new ResponseEntity<>(docService.createDoc(docTitleDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{docId}")
    public ResponseEntity<Long> deleteDoc(@PathVariable Long docId) throws UnauthorizedUserException, ResourceNotFoundException {
        return ResponseEntity.ok(docService.deleteDoc(docId));
    }

    @PatchMapping("/rename/{docId}")
    public ResponseEntity<String> updateDocTitle(@PathVariable Long docId,
                                                 @RequestBody DocTitleDto docTitleDto) throws UnauthorizedUserException, ResourceNotFoundException {
        return ResponseEntity.ok(docService.updateDocTitle(docId, docTitleDto));
    }

    @PatchMapping("/users/add/{docId}")
    public ResponseEntity<UserDocDto> addUser(@PathVariable Long docId,
                                              @RequestBody UserDocDto userDocDto) throws UnauthorizedUserException, ResourceNotFoundException, UserException {
        return ResponseEntity.ok(docService.addUser(docId, userDocDto));
    }

    @GetMapping("/users/shared/{docId}")
    public ResponseEntity<List<UserDocDto>> getSharedUsers(@PathVariable Long docId) throws UnauthorizedUserException, ResourceNotFoundException {
        return ResponseEntity.ok(docService.getSharedUsers(docId));
    }

    @DeleteMapping("/users/remove/{docId}")
    public ResponseEntity<String> removeUser(@PathVariable Long docId,
                                             @RequestBody UserDocDto userDocDto) throws UnauthorizedUserException, ResourceNotFoundException {
        return ResponseEntity.ok(docService.removeUser(docId, userDocDto));
    }

    @PatchMapping("/users/permission/{docId}")
    public ResponseEntity<String> updatePermission(@PathVariable Long docId,
                                                   @RequestBody UserDocDto userDocDto) throws UnauthorizedUserException, ResourceNotFoundException {
        return ResponseEntity.ok(docService.updatePermission(docId, userDocDto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<DocumentDto>> getAllDocs() {
        return ResponseEntity.ok(docService.getAllDocs());
    }

    @GetMapping("/changes/{docId}")
    public ResponseEntity<List<DocumentChangeDto>> getDocChanges(@PathVariable Long docId) throws UnauthorizedUserException, ResourceNotFoundException {
        return ResponseEntity.ok(docService.getDocChanges(docId));
    }

    @GetMapping("/{docId}")
    public ResponseEntity<DocumentDto> getDoc(@PathVariable Long docId) throws UnauthorizedUserException, ResourceNotFoundException {
        return ResponseEntity.ok(docService.getDoc(docId));
    }

}

























