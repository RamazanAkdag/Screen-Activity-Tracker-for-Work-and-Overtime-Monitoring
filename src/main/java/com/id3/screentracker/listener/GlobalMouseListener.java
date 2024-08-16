package com.id3.screentracker.listener;

import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;

public class GlobalMouseListener implements NativeMouseInputListener {
    public void nativeMouseClicked(NativeMouseEvent e) {
        System.out.println("Mouse Clicked: " + e.getClickCount());
        LastActivityTime.resetLastActivityTime();
    }

    public void nativeMousePressed(NativeMouseEvent e) {
        System.out.println("Mouse Pressed: " + e.getButton());
        LastActivityTime.resetLastActivityTime();
    }

    public void nativeMouseReleased(NativeMouseEvent e) {
        System.out.println("Mouse Released: " + e.getButton());
        LastActivityTime.resetLastActivityTime();
    }

    public void nativeMouseMoved(NativeMouseEvent e) {
        System.out.println("Mouse Moved: " + e.getX() + ", " + e.getY());
        LastActivityTime.resetLastActivityTime();
    }

    public void nativeMouseDragged(NativeMouseEvent e) {
        System.out.println("Mouse Dragged: " + e.getX() + ", " + e.getY());
        LastActivityTime.resetLastActivityTime();
    }
}