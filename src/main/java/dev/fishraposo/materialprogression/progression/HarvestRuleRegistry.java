package dev.fishraposo.materialprogression.progression;

import java.util.List;

public final class HarvestRuleRegistry {
    private static final HarvestRuleRegistry DEFAULTS =
            new HarvestRuleRegistry(List.of(
                    new LogHarvestRule(),
                    new KnifePlantHarvestRule(),
                    new StoneHarvestRule()
            ));

    private final List<HarvestRule> rules;

    public HarvestRuleRegistry(List<HarvestRule> rules) {
        this.rules = List.copyOf(rules);
    }

    public static HarvestRuleRegistry defaults() {
        return DEFAULTS;
    }

    public void evaluate(HarvestContext context) {
        for (HarvestRule rule : rules) {
            rule.evaluate(context);
        }
    }
}
