package sheva.singapp.utils.audio.core;

public interface Callback {
    void onBufferAvailable(byte[] buffer);
}