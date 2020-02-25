package fr.pinguet62.meetall.credential;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
interface CredentialRepository extends ReactiveMongoRepository<Credential, String> {

    Flux<Credential> findByUserId(String userId);

}
