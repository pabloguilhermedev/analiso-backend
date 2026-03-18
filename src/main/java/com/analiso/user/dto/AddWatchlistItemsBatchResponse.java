package com.analiso.user.dto;

import java.util.List;

public record AddWatchlistItemsBatchResponse(
    List<WatchlistItemResponse> added,
    List<String> skipped,
    List<String> invalid
) {
}
