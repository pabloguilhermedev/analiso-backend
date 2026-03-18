package com.analiso.user.service;

import com.analiso.exception.BadRequestException;
import com.analiso.user.dto.AddWatchlistItemsBatchResponse;
import com.analiso.user.dto.WatchlistItemResponse;
import com.analiso.user.model.UserWatchlistItemEntity;
import com.analiso.user.model.UserWatchlistItemId;
import com.analiso.user.repository.UserWatchlistItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class WatchlistService {

    private static final Pattern TICKER_PATTERN = Pattern.compile("^[A-Z0-9.\\-]{1,20}$");

    private final UserWatchlistItemRepository userWatchlistItemRepository;

    public WatchlistService(UserWatchlistItemRepository userWatchlistItemRepository) {
        this.userWatchlistItemRepository = userWatchlistItemRepository;
    }

    @Transactional(readOnly = true)
    public List<WatchlistItemResponse> listByUserId(Long userId) {
        return userWatchlistItemRepository.findByIdUserIdOrderByCreatedAtDesc(userId).stream()
            .map(item -> new WatchlistItemResponse(item.getId().getTicker(), item.getCreatedAt()))
            .toList();
    }

    @Transactional
    public WatchlistItemResponse add(Long userId, String rawTicker) {
        String ticker = normalizeTicker(rawTicker);
        if (!TICKER_PATTERN.matcher(ticker).matches()) {
            throw new BadRequestException("invalid_ticker", "Ticker has invalid format");
        }
        if (userWatchlistItemRepository.existsByIdUserIdAndIdTicker(userId, ticker)) {
            throw new BadRequestException("watchlist_item_exists", "Ticker is already on the user's watchlist");
        }

        UserWatchlistItemEntity entity = userWatchlistItemRepository.save(
            new UserWatchlistItemEntity(new UserWatchlistItemId(userId, ticker))
        );
        return new WatchlistItemResponse(entity.getId().getTicker(), entity.getCreatedAt());
    }

    @Transactional
    public AddWatchlistItemsBatchResponse addAll(Long userId, List<String> rawTickers) {
        List<WatchlistItemResponse> added   = new ArrayList<>();
        List<String>                skipped = new ArrayList<>();
        List<String>                invalid = new ArrayList<>();

        for (String raw : rawTickers) {
            String ticker = normalizeTicker(raw);
            if (!TICKER_PATTERN.matcher(ticker).matches()) {
                invalid.add(raw);
                continue;
            }
            if (userWatchlistItemRepository.existsByIdUserIdAndIdTicker(userId, ticker)) {
                skipped.add(ticker);
                continue;
            }
            UserWatchlistItemEntity entity = userWatchlistItemRepository.save(
                new UserWatchlistItemEntity(new UserWatchlistItemId(userId, ticker))
            );
            added.add(new WatchlistItemResponse(entity.getId().getTicker(), entity.getCreatedAt()));
        }

        return new AddWatchlistItemsBatchResponse(added, skipped, invalid);
    }

    @Transactional
    public void remove(Long userId, String rawTicker) {
        String ticker = normalizeTicker(rawTicker);
        userWatchlistItemRepository.deleteByIdUserIdAndIdTicker(userId, ticker);
    }

    private String normalizeTicker(String rawTicker) {
        return rawTicker == null ? "" : rawTicker.trim().toUpperCase(Locale.ROOT);
    }
}
