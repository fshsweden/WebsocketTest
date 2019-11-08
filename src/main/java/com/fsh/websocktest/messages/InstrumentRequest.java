package com.fsh.websocktest.messages;

import lombok.Data;
import lombok.NonNull;

@Data
public class InstrumentRequest implements IMessage {
    @NonNull
    private InstrumentType instrumentType;
}
