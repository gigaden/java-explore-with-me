package ru.practicum.ewm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestsAfterChangesDto {

    private List<RequestResponseDto> confirmedRequests;

    private List<RequestResponseDto> rejectedRequests;
}
