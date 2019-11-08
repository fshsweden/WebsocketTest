package com.fsh.websocktest.models;

import com.fsh.websocktest.messages.InstrumentType;

public interface IInstrument {
    public String symbol();
    public InstrumentType type();
}
