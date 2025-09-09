package hi_light.user.entity;

public enum UserRank {
    A1(1),
    A2(2),
    B1(3),
    B2(4),
    C1(5),
    C2(6);

    private final int rankValue;

    UserRank(int rankValue) {
        this.rankValue = rankValue;
    }

    public int getRankValue() {
        return rankValue;
    }
}
