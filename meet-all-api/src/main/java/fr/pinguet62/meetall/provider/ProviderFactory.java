package fr.pinguet62.meetall.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ProviderFactory {

    private final List<ProviderService> providerServices;

    /**
     * @see ProviderService#getId()
     */
    public ProviderService getProviderService(Provider provider) {
        return providerServices.stream().filter(p -> p.getId().equals(provider)).findFirst()
                .orElseThrow(() -> new NoProviderFoundException(provider));
    }

}
