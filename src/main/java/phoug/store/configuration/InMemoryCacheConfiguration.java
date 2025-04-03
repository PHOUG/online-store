package phoug.store.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import phoug.store.model.Product;
import phoug.store.utils.InMemoryCache;

@Configuration
public class InMemoryCacheConfiguration {

    @Bean
    public InMemoryCache<Long, Product> productCache() {
        return new InMemoryCache<>(300_000, 100);
    }
}
