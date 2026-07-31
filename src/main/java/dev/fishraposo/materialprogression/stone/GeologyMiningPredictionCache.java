package dev.fishraposo.materialprogression.stone;

import java.util.Optional;

/**
 * Client prediction state with no physical-client class dependencies.
 *
 * <p>Only a snapshot for the current request generation and exact block
 * identity is accepted. A proven raw-family candidate uses L3 as the safe
 * pending divisor; unrelated blocks remain at normal speed while waiting.
 */
public final class GeologyMiningPredictionCache {
    public static final long SNAPSHOT_TTL_TICKS = 20L;

    private long nextRequestId = 1L;
    private GeologyMiningTarget target;
    private boolean sourceCandidate;
    private long activeRequestId;
    private long requestedAt;
    private GeologyMiningSnapshotPayload snapshot;
    private long snapshotAcceptedAt;

    public GeologyMiningSnapshotRequest beginTarget(
            GeologyMiningTarget newTarget,
            boolean newSourceCandidate,
            long now
    ) {
        target = newTarget;
        sourceCandidate = newSourceCandidate;
        snapshot = null;
        activeRequestId = allocateRequestId();
        requestedAt = now;
        return new GeologyMiningSnapshotRequest(
                activeRequestId,
                newTarget
        );
    }

    public Optional<GeologyMiningSnapshotRequest> continueTarget(
            GeologyMiningTarget currentTarget,
            boolean currentSourceCandidate,
            long now
    ) {
        if (target == null || !target.equals(currentTarget)) {
            return Optional.of(beginTarget(
                    currentTarget,
                    currentSourceCandidate,
                    now
            ));
        }
        sourceCandidate = currentSourceCandidate;

        long freshnessOrigin = snapshot == null
                ? requestedAt
                : snapshotAcceptedAt;
        if (now - freshnessOrigin < SNAPSHOT_TTL_TICKS) {
            return Optional.empty();
        }
        snapshot = null;
        activeRequestId = allocateRequestId();
        requestedAt = now;
        return Optional.of(new GeologyMiningSnapshotRequest(
                activeRequestId,
                currentTarget
        ));
    }

    public boolean accept(
            GeologyMiningSnapshotPayload candidate,
            long now
    ) {
        if (target == null
                || candidate.requestId() != activeRequestId
                || !candidate.target().equals(target)
                || now - requestedAt >= SNAPSHOT_TTL_TICKS) {
            return false;
        }
        snapshot = candidate;
        snapshotAcceptedAt = now;
        return true;
    }

    public float adjustSpeed(
            float currentSpeed,
            GeologyMiningTarget currentTarget,
            boolean currentSourceCandidate,
            long now
    ) {
        if (target == null) {
            return currentSpeed;
        }
        if (!target.equals(currentTarget)) {
            clear();
            return currentSpeed;
        }
        sourceCandidate = currentSourceCandidate;
        if (!sourceCandidate) {
            return currentSpeed;
        }
        if (snapshot != null
                && now - snapshotAcceptedAt < SNAPSHOT_TTL_TICKS) {
            return currentSpeed / snapshot.speedDivisor();
        }
        return currentSpeed / GeologyTier.LEVEL_3.speedDivisor();
    }

    public void clear() {
        target = null;
        sourceCandidate = false;
        activeRequestId = 0L;
        requestedAt = 0L;
        snapshot = null;
        snapshotAcceptedAt = 0L;
    }

    public void clearUnlessDimension(
            net.minecraft.resources.Identifier dimension
    ) {
        if (target != null && !target.dimension().equals(dimension)) {
            clear();
        }
    }

    public boolean hasTarget() {
        return target != null;
    }

    private long allocateRequestId() {
        long allocated = nextRequestId;
        if (nextRequestId == Long.MAX_VALUE) {
            nextRequestId = 1L;
        } else {
            nextRequestId++;
        }
        return allocated;
    }
}
