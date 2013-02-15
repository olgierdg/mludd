package edu.mbryla.andlogger.database;

public enum Type {
    KEY("INTEGER PRIMARY KEY AUTOINCREMENT"), // remove autoincrement ?? (a tad bit slower)
    INTEGER("INTEGER"),
    REAL("REAL"),
    STRING("STRING"),
    TIMESTAMP("TIMESTAMP");

    private String str;

    private Type(String s) {
        str = s;
    }

    @Override
    public String toString() {
        return str;
    }

    public enum Nullable {
        NULL("NULL"),
        NOTNULL("NOT NULL");

        private String str;

        private Nullable(String s) {
            str = s;
        }

        @Override
        public String toString() {
            return str;
        }
    }
}
