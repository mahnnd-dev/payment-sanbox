package com.neo.cache;

import com.neo.modal.Banker;
import com.neo.repository.PmBankerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PmBankerCache {
    private final PmBankerRepository repository;
    private final CacheManager cacheManager;

    @Cacheable("pmBanker")
    public Banker getPmBankerByCardName(String cardNumber) {
        return repository.findAllByCardNumber(cardNumber); // chỉ gọi khi cache miss
    }

    @Scheduled(fixedRate = 10000)
    public void refreshPmBankerCache() {
        List<Banker> bankers = repository.findAll();
        Cache cache = cacheManager.getCache("pmBanker");
        for (Banker banker : bankers) {
            cache.put(banker.getCardNumber(), banker);
        }
        log.info("Đã cập nhật cache pmBanker lúc {}", LocalDateTime.now());
    }
}
