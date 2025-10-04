package com.neo.cache;

import com.neo.modal.Banker;
import com.neo.modal.Partner;
import com.neo.repository.PmPartnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class PmPartnerCache {
    private final PmPartnerRepository repository;
    private final CacheManager cacheManager;

    @Cacheable("pmPartner")
    public Partner getPmPartnerByTmnCode(String tmnCode) {
        return repository.findAllByTmnCode(tmnCode); // chỉ gọi khi cache miss
    }

    @Scheduled(fixedRate = 10000)
    public void refreshPmBankerCache() {
        List<Partner> partnerList = repository.findAll();
        Cache cache = cacheManager.getCache("pmPartner");
        for (Partner partner : partnerList) {
            cache.put(partner.getTmnCode(), partner);
        }
        String formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        log.info("Đã cập nhật cache pmPartner lúc {}, Cache size: {}", formattedDate,((ConcurrentMap<?, ?>) cache.getNativeCache()).size());
    }
}
