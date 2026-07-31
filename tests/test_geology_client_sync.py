import unittest
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
JAVA = (
    ROOT
    / "src"
    / "main"
    / "java"
    / "dev"
    / "fishraposo"
    / "materialprogression"
)


class GeologyClientSyncContracts(unittest.TestCase):
    def test_common_network_boundary_has_no_client_class_references(self):
        common_files = [
            JAVA / "stone" / "GeologyMiningTarget.java",
            JAVA / "stone" / "GeologyMiningSnapshotRequest.java",
            JAVA / "stone" / "GeologyMiningSnapshotPayload.java",
            JAVA / "stone" / "GeologyMiningSnapshotService.java",
            JAVA / "stone" / "GeologyMiningPredictionCache.java",
            JAVA / "stone" / "GeologyMiningNetwork.java",
        ]
        for path in common_files:
            self.assertTrue(path.is_file(), f"missing {path.name}")
            self.assertNotIn(
                "net.minecraft.client",
                path.read_text(encoding="utf-8"),
                f"{path.name} references physical-client classes",
            )

    def test_client_lifecycle_and_interaction_actions_are_wired(self):
        client = (
            JAVA / "client" / "ClientGeologyMiningEvents.java"
        ).read_text(encoding="utf-8")
        entrypoint = (JAVA / "MaterialProgressionClient.java").read_text(
            encoding="utf-8"
        )
        common_entrypoint = (JAVA / "MaterialProgression.java").read_text(
            encoding="utf-8"
        )

        for action in ("START", "CLIENT_HOLD", "STOP", "ABORT"):
            self.assertIn(action, client)
        self.assertIn("ClientPlayerNetworkEvent.LoggingOut", client)
        self.assertIn("ClientPlayerNetworkEvent.Clone", client)
        self.assertIn("PlayerEvent.BreakSpeed", client)
        self.assertIn("ModTags.STONE_SOURCES", client)
        self.assertIn("RegisterClientPayloadHandlersEvent", entrypoint)
        self.assertIn("ClientGeologyMiningEvents.register()", entrypoint)
        self.assertIn("GeologyMiningNetwork.register(modBus)", common_entrypoint)

    def test_server_break_speed_remains_authoritative(self):
        mining = (
            JAVA / "stone" / "GeologyMiningEvents.java"
        ).read_text(encoding="utf-8")
        self.assertIn("instanceof ServerLevel level", mining)
        self.assertIn("tier.speedDivisor()", mining)
        self.assertNotIn("net.minecraft.client", mining)

    def test_every_family_source_must_join_synchronized_parent(self):
        tags = (
            JAVA / "registry" / "ModTags.java"
        ).read_text(encoding="utf-8")
        catalog = (
            JAVA / "stone" / "StoneFamilyCatalog.java"
        ).read_text(encoding="utf-8")
        self.assertIn("STONE_SOURCES", tags)
        self.assertIn("ModTags.STONE_SOURCES", catalog)
        self.assertIn("synchronized source parent tag", catalog)

    def test_docs_record_authority_timeout_and_manual_client_check(self):
        geology = (ROOT / "docs" / "GEOLOGY.md").read_text(encoding="utf-8")
        testing = (ROOT / "docs" / "TESTING.md").read_text(encoding="utf-8")
        combined = geology + "\n" + testing
        self.assertIn("server-authoritative", combined)
        self.assertIn("20 ticks", combined)
        self.assertIn("runClient", combined)
        self.assertIn("mining-crack", combined)


if __name__ == "__main__":
    unittest.main()
