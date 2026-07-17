package model;

public final class DisplayIds {
    private DisplayIds() {}

    public static String shortId(String prefix, String rawId) {
        if (rawId == null || rawId.isBlank()) return "—";
        if (rawId.matches("[a-z]+-[0-9]{1,6}")) return rawId;
        prefix = switch (prefix == null ? "" : prefix.toLowerCase()) {
            case "checkout" -> "co";
            case "copy" -> "c";
            case "title", "book" -> "t";
            case "user", "member" -> "u";
            case "hold" -> "h";
            case "fine" -> "f";
            case "audit", "audit_log" -> "log";
            default -> prefix == null || prefix.isBlank() ? "id" : prefix.toLowerCase();
        };
        String compact = rawId.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
        return prefix + "-" + compact.substring(0, Math.min(5, compact.length()));
    }
}
