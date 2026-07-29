package dev.fishraposo.materialprogression.progression;

@FunctionalInterface
public interface HarvestRule {
    void evaluate(HarvestContext context);
}
