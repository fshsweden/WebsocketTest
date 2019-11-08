package com.fsh.websocktest.models;

import com.fsh.websocktest.messages.InstrumentType;
import lombok.Data;
import lombok.NonNull;

@Data
public class Future implements  IInstrument {

    @NonNull
    private String name;
    @NonNull
    private String longname; /* descriptive name */

    @Override
    public String symbol() {
        return null;
    }

    @Override
    public InstrumentType type() {
        return InstrumentType.FUTURE;
    }
}
