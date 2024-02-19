package com.icofound.Notification;

public enum NotificationType {

    request("request"),
    accept("accept"),
    postupdate("postupdate"),
    message("message");

    private final String notificationType;

    /**
     * @param text
     */
    NotificationType(final String text) {
        this.notificationType = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return notificationType;
    }
}
