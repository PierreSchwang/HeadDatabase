package de.pierreschwang.headdatabase.api;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.UUID;

public interface AvatarApi {

    ListenableFuture<String> getTexture(String username);

    ListenableFuture<String> getTexture(UUID uuid);

}
