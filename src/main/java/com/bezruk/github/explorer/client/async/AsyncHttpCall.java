package com.bezruk.github.explorer.client.async;

import okhttp3.Call;

public record AsyncHttpCall<ID>(ID parentRecordId, Call asyncCall) { }
