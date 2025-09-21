package com.devansh.repo;

import com.devansh.model.User;
import com.devansh.model.UserDoc;
import com.devansh.model.UserDocId;
import com.devansh.model.enums.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserDocRepository extends JpaRepository<UserDoc, UserDocId> {

    @Query("""
    select ud.doc from UserDoc ud
    where ud.user.username = :username
""")
    List<UserDoc> getDocsByUser_Username(@Param("username") String username);

    @Modifying
    @Query("""
    delete from UserDoc ud
    where ud.userDocId.username = ?1
    and ud.userDocId.docId = ?2
    and (select d.owner.username from Doc d where d.id = ?2) = ?3
""")
    @Transactional
    int deleteUserDocBy(String username, Long docId, String owner);

    @Modifying
    @Transactional
    @Query("""
    update UserDoc ud set ud.permission = ?4
    where ud.userDocId.username = ?1
    and ud.userDocId.docId = ?2
    and (select d.owner.username from Doc d where d.id = ?2) = ?3
    
""")
    int updateUserDocBy(String username, Long docId, String owner, Permission permission);
}
