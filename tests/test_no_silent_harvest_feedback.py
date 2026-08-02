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
GAME_TESTS = (
    ROOT
    / "src"
    / "gameTest"
    / "java"
    / "dev"
    / "fishraposo"
    / "materialprogression"
    / "gametest"
)


class NoSilentHarvestFeedbackContractTests(unittest.TestCase):
    def test_every_raw_stone_drop_denial_has_a_start_feedback_route(self):
        mining = (JAVA / "stone" / "GeologyMiningEvents.java").read_text(
            encoding="utf-8"
        )
        feedback = (JAVA / "stone" / "GeologyFeedbackEvents.java").read_text(
            encoding="utf-8"
        )

        # The final clear replaces vanilla loot with Rocks; the two early
        # returns are the only true no-drop branches.
        self.assertEqual(3, mining.count("event.getDrops().clear();"))
        self.assertIn("GeologyToolCapability.canMine", mining)
        self.assertIn("player.hasCorrectToolForDrops", mining)
        self.assertIn("Action.START", feedback)
        self.assertIn("GeologyToolCapability.canMine", feedback)
        self.assertIn(
            "GeologyMiningEvents.requiresCorrectToolForRockDrops", feedback
        )
        self.assertIn("FeedbackMessages.insufficientGeology", feedback)
        self.assertIn("FeedbackMessages.correctToolRequired", feedback)

    def test_feedback_messages_and_live_tests_cover_denials_and_valid_tools(self):
        messages = (JAVA / "progression" / "FeedbackMessages.java").read_text(
            encoding="utf-8"
        )
        feedback_tests = (
            GAME_TESTS / "GeologyFeedbackGameTests.java"
        ).read_text(encoding="utf-8")
        discoverability_tests = (
            GAME_TESTS / "DiscoverabilityGameTests.java"
        ).read_text(encoding="utf-8")

        self.assertIn("correctToolRequired", messages)
        self.assertIn("hardnessDisabled", feedback_tests)
        self.assertIn("capableTool", feedback_tests)
        self.assertIn("logFeedbackThrottleHonorsToolConfigAndCreative", discoverability_tests)
