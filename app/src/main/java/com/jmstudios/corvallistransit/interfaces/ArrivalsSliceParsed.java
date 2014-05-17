package com.jmstudios.corvallistransit.interfaces;

import com.jmstudios.corvallistransit.models.Stop;

import java.util.List;

public interface ArrivalsSliceParsed {
    void onSliceParsed(List<Stop> slice);

    void onSliceParseFailed();
}
