package contract.util;

public class Money {

    private long value;

    public static Money ONE = Money.valueOf(100000000L);
    public static Money ZERO = Money.valueOf(0L);

    public Money() {
    }

    public static Money valueOf(long i) {
        return new Money(i);
    }

    private Money(long value) {
        this.value = value;
    }

    public Money plus(Money na){
        return Money.valueOf(this.value + na.value);
    }

    public Money plus(long value){
        return Money.valueOf(this.value + value);
    }

    public Money minus(Money na){
        return Money.valueOf(this.value - na.value);
    }

    public Money minus(long value){
        return Money.valueOf(this.value - value);
    }

    public Money multiply(Money na){
        return Money.valueOf(this.value * na.value);
    }

    public Money multiply(long value){
        return Money.valueOf(this.value * value);
    }

    public Money divide(Money na){
        return Money.valueOf(this.value / na.value);
    }

    public Money divide(long value){
        return Money.valueOf(this.value / value);
    }


    public long value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Money na = (Money) o;

        return value == na.value;
    }

    @Override
    public int hashCode() {
        return (int) (value ^ (value >>> 32));
    }

    @Override
    public String toString() {
        return "Money{" +
                "value=" + value +
                '}';
    }
}
