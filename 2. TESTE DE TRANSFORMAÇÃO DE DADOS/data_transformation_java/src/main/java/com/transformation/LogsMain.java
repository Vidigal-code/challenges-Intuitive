package com.transformation;

public class LogsMain {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";

    public static void logError(String message) {
        System.out.println(ANSI_RED + "[ERRO] " + message + ANSI_RESET);
    }

    public static void logInfo(String message) {
        System.out.println(ANSI_GREEN + "[INFO] " + message + ANSI_RESET);
    }

    public static void logWarning(String message) {
        System.out.println(ANSI_YELLOW + "[AVISO] " + message + ANSI_RESET);
    }

    public static void logDebug(String message) {
        System.out.println(ANSI_BLUE + "[DEBUG] " + message + ANSI_RESET);
    }
}
