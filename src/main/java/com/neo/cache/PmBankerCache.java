package com.neo.cache;

import com.neo.modal.Banker;
import com.neo.repository.PmBankerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class PmBankerCache extends CacheSwapService<Banker> {
    private final PmBankerRepository repository;

    @Override
    protected ConcurrentHashMap<String, Banker> fetchDataFromDB() {
        ConcurrentHashMap<String, Banker> map = new ConcurrentHashMap<>();
        List<Banker> partnerConfigList = repository.findAll();
        for (Banker partnerConfig : partnerConfigList) {
            map.put(partnerConfig.getCardNumber(), partnerConfig);
        }
        return map;
    }

    @Scheduled(fixedDelayString = "${app.sql.sync-time}")
    public void forceRefresh() {
        super.forceRefresh();
    }
}
