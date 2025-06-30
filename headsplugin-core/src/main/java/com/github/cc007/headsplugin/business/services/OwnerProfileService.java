package com.github.cc007.headsplugin.business.services;

import com.github.cc007.headsplugin.api.business.domain.Head;
import com.github.cc007.headsplugin.business.model.McVersion;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bukkit.Bukkit;
import org.bukkit.profile.PlayerProfile;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

/**
 * Class for decoding and traversing the head value
 */
@Log4j2
@RequiredArgsConstructor
public class OwnerProfileService {

    private final McVersion mcVersion;

    /**
     * Create a head owner's {@link PlayerProfile} based on a given head
     * <br>
     * Fix the name of the head owner when the server version is 1.21 or higher.
     * Since that version, the name can't contain characters of ASCII code lower than 33
     * or higher than 126 and can't be longer than 16 characters.
     *
     * @param head the head with the name, uuid and texture link
     * @return the head owner's player profile
     */
    public PlayerProfile createOwnerProfile(@NonNull Head head) {
        final var url = parseHeadValue(head.getValue());
        final var name = isNameFixNeeded() ? fixName(head.getName()) : head.getName();
        final var ownerProfile = Bukkit.createPlayerProfile(head.getHeadOwner(), name);
        url.ifPresent(skinUrl -> ownerProfile.getTextures().setSkin(skinUrl));
        return ownerProfile;
    }

    private boolean isNameFixNeeded() {
        return mcVersion.minor() >= 21 ||
               (mcVersion.minor() == 20 && mcVersion.patch() >= 6);
    }

    @NonNull
    private String fixName(@NonNull String rawName) {
        final var nameValidChars = rawName
                .replaceAll("\\s", "_")
                .replaceAll("[^!-~]", "*");
        return nameValidChars.substring(0, Math.min(nameValidChars.length(), 16));
    }

    @NonNull
    private Optional<URL> parseHeadValue(@NonNull String headValue) {
        return decodeBase64(headValue)
                .flatMap(OwnerProfileService::decodeJson)
                .flatMap(jsonObject -> getObject(jsonObject, "textures"))
                .flatMap(texturesObject -> getObject(texturesObject, "SKIN"))
                .flatMap(skinObject -> getString(skinObject, "url"))
                .flatMap(this::parseUrl);
    }

    @NonNull
    private static Optional<String> decodeBase64(@NonNull String encodedValue) {
        try {
            return Optional.of(new String(Base64.getDecoder().decode(encodedValue), StandardCharsets.UTF_8));
        } catch (IllegalArgumentException e) {
            log.warn("Unable to base64 decode \"" + encodedValue + "\": " + e.getMessage(), e);
            return Optional.empty();
        }
    }

    @NonNull
    private static Optional<JsonObject> decodeJson(@NonNull String decodedValue) {
        try {
            return Optional.of(JsonParser.parseString(decodedValue))
                    .filter(jsonElement -> {
                        final boolean isObject = jsonElement.isJsonObject();
                        if (!isObject) {
                            log.warn("Root element doesn't contain a JSON object: " + jsonElement);
                        }
                        return isObject;
                    })
                    .map(JsonElement::getAsJsonObject);
        } catch (JsonParseException e) {
            log.warn("Unable to parse \"" + decodedValue + "\" as JSON: " + e.getMessage(), e);
            return Optional.empty();
        }
    }

    @NonNull
    private static Optional<JsonObject> getObject(@NonNull JsonObject parent, @NonNull String key) {
        if (!parent.has(key)) {
            log.warn("Key \"" + key + "\" not found");
            return Optional.empty();
        }
        return Optional.of(parent.get(key))
                .filter(jsonElement -> {
                    final boolean isObject = jsonElement.isJsonObject();
                    if (!isObject) {
                        log.warn("Key \"" + key + "\" doesn't contain a JSON object: " + jsonElement);
                    }
                    return isObject;
                })
                .map(JsonElement::getAsJsonObject);
    }

    @NonNull
    private static Optional<JsonPrimitive> getPrimitive(@NonNull JsonObject parent, @NonNull String key) {
        if (!parent.has(key)) {
            log.warn("Key \"" + key + "\" not found");
            return Optional.empty();
        }
        return Optional.of(parent.get(key))
                .filter(jsonElement -> {
                    final boolean isObject = jsonElement.isJsonPrimitive();
                    if (!isObject) {
                        log.warn("Key \"" + key + "\" doesn't contain a JSON primitive: " + jsonElement);
                    }
                    return isObject;
                })
                .map(JsonElement::getAsJsonPrimitive);
    }

    @NonNull
    private static Optional<String> getString(@NonNull JsonObject parent, @NonNull String key) {
        return getPrimitive(parent, key)
                .map(JsonPrimitive::getAsString);
    }

    @NonNull
    private Optional<URL> parseUrl(@NonNull String urlString) {
        try {
            return Optional.of(new URL(urlString));
        } catch (MalformedURLException e) {
            log.warn("Unable to parse \"" + urlString + "\" as URL: " + e.getMessage(), e);
            return Optional.empty();
        }
    }
}
