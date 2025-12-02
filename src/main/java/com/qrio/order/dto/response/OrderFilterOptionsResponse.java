package com.qrio.order.dto.response;

import java.util.List;

import com.qrio.shared.response.OptionResponse;

public record OrderFilterOptionsResponse(
    List<OptionResponse> tables,
    List<OptionResponse> customers
) {}
