package org.efire.net.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class Image {
    private static int counter = 0;
    private String name;
    private long size;
    @NonNull
    private String type;

}
