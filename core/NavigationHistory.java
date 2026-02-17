package de.lorberry.arsen.core;

import java.util.Stack;

public class NavigationHistory {
    private final Stack<Long> backStack;
    private final Stack<Long> forwardStack;
    private long currentAddress;

    public NavigationHistory() {
        this.backStack = new Stack<>();
        this.forwardStack = new Stack<>();
        this.currentAddress = 0;
    }

    public void navigate(long address) {
        if (currentAddress != 0) {
            backStack.push(currentAddress);
        }
        currentAddress = address;
        forwardStack.clear();
    }

    public boolean canGoBack() {
        return !backStack.isEmpty();
    }

    public boolean canGoForward() {
        return !forwardStack.isEmpty();
    }

    public long goBack() {
        if (!canGoBack()) {
            return currentAddress;
        }
        forwardStack.push(currentAddress);
        currentAddress = backStack.pop();
        return currentAddress;
    }

    public long goForward() {
        if (!canGoForward()) {
            return currentAddress;
        }
        backStack.push(currentAddress);
        currentAddress = forwardStack.pop();
        return currentAddress;
    }

    public long getCurrentAddress() {
        return currentAddress;
    }

    public void clear() {
        backStack.clear();
        forwardStack.clear();
        currentAddress = 0;
    }
}
