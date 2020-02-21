package fr.pinguet62.meetall.credential;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface CredentialRepository extends JpaRepository<Credential, Integer> {

    List<Credential> findByUserId(String userId);

}
