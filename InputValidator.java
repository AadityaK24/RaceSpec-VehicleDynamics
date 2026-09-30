public class InputValidator
{
    public boolean isPositive(double value)
    {
        return value > 0;
    }

    public boolean isNonNegative(double value)
    {
        return value >= 0;
    }

    public boolean isValidText(String value)
    {
        return value != null && !value.trim().isEmpty();
    }

    public boolean isValidGearRatio(double ratio)
    {
        return ratio > 0;
    }

    public boolean isValidPercentage(double value)
    {
        return value >= 0 && value <= 1;
    }
}