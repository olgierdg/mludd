package edu.mbryla.andlogger.database.query;

public enum Type {
    KEY("INTEGER PRIMARY KEY AUTOINCREMENT"),
    INTEGER("INTEGER"),
    REAL("REAL"),
    TEXT("TEXT"),
    BLOB("BLOB"),
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
