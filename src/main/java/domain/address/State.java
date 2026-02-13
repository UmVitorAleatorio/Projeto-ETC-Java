package domain.address;

import lombok.Getter;

@Getter
public enum State {
    AC(1, "AC"),
    AL(2, "AL"),
    AP(3, "AP"),
    AM(4, "AM"),
    BA(5, "BA"),
    CE(6, "CE"),
    DF(7, "DF"),
    ES(8, "ES"),
    GO(9, "GO"),
    MA(10, "MA"),
    MT(11, "MT"),
    MS(12, "MS"),
    MG(13, "MG"),
    PA(14, "PA"),
    PB(15, "PB"),
    PR(16, "PR"),
    PE(17, "PE"),
    PI(18, "PI"),
    RJ(19, "RJ"),
    RN(20, "RN"),
    RS(21, "RS"),
    RO(22, "RO"),
    RR(23, "RR"),
    SC(24, "SC"),
    SP(25, "SP"),
    SE(26, "SE"),
    TO(27, "TO"),;

    private final int code;
    private final String uf;

    State(int code, String uf) {
        this.code = code;
        this.uf = uf;
    }

    public static State fromCode(int code) {
        for (State state : State.values()) {
            if (state.code == code) return state;
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
