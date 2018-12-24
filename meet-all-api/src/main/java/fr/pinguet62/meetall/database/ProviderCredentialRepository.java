package fr.pinguet62.meetall.database;

import fr.pinguet62.meetall.provider.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderCredentialRepository extends JpaRepository<ProviderCredential, Integer> {

    List<ProviderCredential> findByUserId(String userId);

    Optional<ProviderCredential> findByUserIdAndProvider(String userId, Provider provider);

}
