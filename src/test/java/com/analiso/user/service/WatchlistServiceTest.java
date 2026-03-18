package com.analiso.user.service;

import com.analiso.exception.BadRequestException;
import com.analiso.user.model.UserWatchlistItemEntity;
import com.analiso.user.model.UserWatchlistItemId;
import com.analiso.user.repository.UserWatchlistItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WatchlistServiceTest {

    @Mock
    private UserWatchlistItemRepository userWatchlistItemRepository;

    @InjectMocks
    private WatchlistService watchlistService;

    @Test
    void shouldNormalizeTickerToUpperCaseOnCreate() {
        when(userWatchlistItemRepository.existsByIdUserIdAndIdTicker(10L, "petr4".toUpperCase()))
            .thenReturn(false);
        when(userWatchlistItemRepository.save(any(UserWatchlistItemEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        watchlistService.add(10L, "petr4");

        ArgumentCaptor<UserWatchlistItemEntity> captor = ArgumentCaptor.forClass(UserWatchlistItemEntity.class);
        verify(userWatchlistItemRepository).save(captor.capture());
        assertThat(captor.getValue().getId().getTicker()).isEqualTo("PETR4");
    }

    @Test
    void shouldRejectDuplicateTicker() {
        when(userWatchlistItemRepository.existsByIdUserIdAndIdTicker(10L, "PETR4")).thenReturn(true);

        assertThatThrownBy(() -> watchlistService.add(10L, "PETR4"))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Ticker is already on the user's watchlist");
    }

    @Test
    void shouldRemoveTickerUsingNormalizedValue() {
        watchlistService.remove(10L, "vale3");
        verify(userWatchlistItemRepository).deleteByIdUserIdAndIdTicker(10L, "VALE3");
    }
}
