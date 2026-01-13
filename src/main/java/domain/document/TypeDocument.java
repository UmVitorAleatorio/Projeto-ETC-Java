package domain.document;

public enum TypeDocument {
    CPF(1, "CPF"),
    CNPJ(2, "CNPJ");

    private final int code;
    private final String desc;

    TypeDocument(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TypeDocument fromCode(int code) {
        for (TypeDocument type : TypeDocument.values()) {
            if (type.code == code) return type;
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
