package org.efire.net.entity;

import lombok.*;

import java.util.concurrent.ThreadLocalRandom;

@Data
@AllArgsConstructor
@Builder(builderMethodName = "internalBuilder")
public class Image {
    private static int counter = 0;
    private String name;
    private long size;
    @NonNull
    private String type;

    public static ImageBuilder builder(String type) {
        counter++;
        return internalBuilder()
                .name("image-"+counter)
                .size(ThreadLocalRandom.current().nextLong())
                .type(type);
    }
}
