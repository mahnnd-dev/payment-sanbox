package com.neo.cache;

import com.neo.modal.Partner;
import com.neo.repository.PmPartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class PmPartnerCache extends CacheSwapService<Partner> {
    private final PmPartnerRepository repository;

    @Override
    protected ConcurrentHashMap<String, Partner> fetchDataFromDB() {
        ConcurrentHashMap<String, Partner> map = new ConcurrentHashMap<>();
        List<Partner> partnerConfigList = repository.findAll();
        for (Partner partnerConfig : partnerConfigList) {
            map.put(partnerConfig.getTmnCode(), partnerConfig);
        }
        return map;
    }

    @Scheduled(fixedDelayString = "${app.sql.sync-time}")
    public void forceRefresh() {
        super.forceRefresh();
    }
}
