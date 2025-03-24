package org.example;

import java.util.List;

public record GPTRequest(
        String model,
        List<ReqMessage> messages
) {}
