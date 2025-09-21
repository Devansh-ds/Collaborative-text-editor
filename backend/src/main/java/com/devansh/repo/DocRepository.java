package com.devansh.repo;

import com.devansh.model.Doc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DocRepository extends JpaRepository<Doc, Long> {


    @Query("""
    select d from Doc d
    where d.owner.username = ?1 or
    d in (select ud.doc from UserDoc ud where ud.user.username = ?1)
""")
    List<Doc> findByUsername(String username);
}
