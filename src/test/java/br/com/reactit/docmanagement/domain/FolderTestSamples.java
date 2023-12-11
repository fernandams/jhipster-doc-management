package br.com.reactit.docmanagement.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FolderTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Folder getFolderSample1() {
        return new Folder().id(1L).title("title1");
    }

    public static Folder getFolderSample2() {
        return new Folder().id(2L).title("title2");
    }

    public static Folder getFolderRandomSampleGenerator() {
        return new Folder().id(longCount.incrementAndGet()).title(UUID.randomUUID().toString());
    }
}
