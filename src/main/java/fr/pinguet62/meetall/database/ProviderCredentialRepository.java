package fr.pinguet62.meetall.database;

import fr.pinguet62.meetall.provider.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProviderCredentialRepository extends JpaRepository<ProviderCredential, Integer> {

    Optional<ProviderCredential> findByUserIdAndProvider(int userId, Provider provider);

}
