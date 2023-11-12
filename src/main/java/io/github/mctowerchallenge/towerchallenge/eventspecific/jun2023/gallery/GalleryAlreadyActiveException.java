package io.github.mctowerchallenge.towerchallenge.eventspecific.jun2023.gallery;

public class GalleryAlreadyActiveException extends Exception {

    public GalleryAlreadyActiveException() {
        super("Gallery is already active and cannot be activated until it is done.");
    }

}