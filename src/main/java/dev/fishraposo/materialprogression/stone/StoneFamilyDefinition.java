package dev.fishraposo.materialprogression.stone;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public record StoneFamilyDefinition(
        TagKey<Block> sourceBlockTag,
        TagKey<Item> rockItemTag,
        Identifier cobbledBlock,
        Identifier rawBlock,
        TagKey<Block> looseRockSurfaceBlockTag,
        Resistance resistance
) {
    public static final Codec<StoneFamilyDefinition> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    TagKey.hashedCodec(Registries.BLOCK)
                            .fieldOf("source_block_tag")
                            .forGetter(StoneFamilyDefinition::sourceBlockTag),
                    TagKey.hashedCodec(Registries.ITEM)
                            .fieldOf("rock_item_tag")
                            .forGetter(StoneFamilyDefinition::rockItemTag),
                    Identifier.CODEC
                            .fieldOf("cobbled_block")
                            .forGetter(StoneFamilyDefinition::cobbledBlock),
                    Identifier.CODEC
                            .fieldOf("raw_block")
                            .forGetter(StoneFamilyDefinition::rawBlock),
                    TagKey.hashedCodec(Registries.BLOCK)
                            .fieldOf("loose_rock_surface_block_tag")
                            .forGetter(StoneFamilyDefinition::looseRockSurfaceBlockTag),
                    Resistance.CODEC
                            .fieldOf("resistance")
                            .forGetter(StoneFamilyDefinition::resistance)
            ).apply(instance, StoneFamilyDefinition::new));

    public record Resistance(StoneResistance tier, float modifier) {
        public static final Codec<Resistance> CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                        StoneResistance.CODEC
                                .fieldOf("tier")
                                .forGetter(Resistance::tier),
                        Codec.FLOAT
                                .fieldOf("modifier")
                                .forGetter(Resistance::modifier)
                ).apply(instance, Resistance::new));

        public Resistance {
            if (!Float.isFinite(modifier) || modifier <= 0.0F) {
                throw new IllegalArgumentException(
                        "Stone resistance modifier must be finite and positive"
                );
            }
        }
    }
}
