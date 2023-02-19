package io.github.mystievous.towerchallenge.eventspecific.feb2023.eviltower.gallery;

public class GalleryAlreadyActiveException extends Exception {

    public GalleryAlreadyActiveException() {
        super("Gallery is already active and cannot be activated until it is done.");
    }
}
