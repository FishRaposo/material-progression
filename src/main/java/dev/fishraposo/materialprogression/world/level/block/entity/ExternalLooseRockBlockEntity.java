package dev.fishraposo.materialprogression.world.level.block.entity;

import dev.fishraposo.materialprogression.registry.ModBlockEntities;
import dev.fishraposo.materialprogression.stone.StoneFamilyCatalog;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public final class ExternalLooseRockBlockEntity extends BlockEntity {
    private @Nullable Identifier family;
    private ItemStack rock = ItemStack.EMPTY;
    private long observedCatalogVersion = Long.MIN_VALUE;

    public ExternalLooseRockBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EXTERNAL_LOOSE_ROCKS.get(), pos, state);
    }

    public void initialize(StoneFamilyCatalog.Entry entry) {
        if (entry.builtInFamily().isPresent()) {
            throw new IllegalArgumentException(
                    "Built-in family " + entry.id()
                            + " must use the compact Loose Rocks block state"
            );
        }
        family = entry.id();
        rock = new ItemStack(entry.rockItem());
        observedCatalogVersion = StoneFamilyCatalog.version();
        setChanged();
        sync();
    }

    public static void serverTick(
            Level level,
            BlockPos pos,
            BlockState state,
            ExternalLooseRockBlockEntity rocks
    ) {
        if (!(level instanceof ServerLevel serverLevel)
                || rocks.observedCatalogVersion
                        == StoneFamilyCatalog.version()) {
            return;
        }
        rocks.observedCatalogVersion = StoneFamilyCatalog.version();
        if (rocks.family == null || rocks.rock.isEmpty()) {
            serverLevel.removeBlock(pos, false);
            return;
        }

        var current = StoneFamilyCatalog.get().byId(rocks.family);
        if (current.isEmpty()
                || current.orElseThrow().builtInFamily().isPresent()) {
            serverLevel.destroyBlock(pos, true);
            return;
        }
        StoneFamilyCatalog.Entry entry = current.orElseThrow();
        ItemStack updatedRock = new ItemStack(entry.rockItem());
        if (!ItemStack.isSameItemSameComponents(rocks.rock, updatedRock)) {
            rocks.rock = updatedRock;
            rocks.setChanged();
            rocks.sync();
        }
        if (!state.canSurvive(serverLevel, pos)) {
            serverLevel.destroyBlock(pos, true);
        }
    }

    public Optional<Identifier> familyId() {
        return Optional.ofNullable(family);
    }

    public ItemStack rock() {
        return rock.copy();
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        family = input.read("Family", Identifier.CODEC).orElse(null);
        rock = input.read("Rock", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        observedCatalogVersion = Long.MIN_VALUE;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (family != null) {
            output.store("Family", Identifier.CODEC, family);
        }
        if (!rock.isEmpty()) {
            output.store("Rock", ItemStack.CODEC, rock);
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public net.minecraft.nbt.CompoundTag getUpdateTag(
            HolderLookup.Provider registries
    ) {
        return saveWithoutMetadata(registries);
    }

    private void sync() {
        if (level != null) {
            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    3
            );
        }
    }
}
