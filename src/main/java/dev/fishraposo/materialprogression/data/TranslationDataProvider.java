package dev.fishraposo.materialprogression.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.fishraposo.materialprogression.MaterialProgression;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.data.PackOutput;

final class TranslationDataProvider extends GeneratedResourceProvider {
    TranslationDataProvider(PackOutput output) {
        super(
                "Material Progression translations",
                output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                        .resolve(MaterialProgression.MOD_ID)
        );
    }

    @Override
    protected Map<String, JsonElement> resources() {
        Map<String, JsonElement> resources = orderedResources();
        resources.put("lang/en_us.json", language(false));
        resources.put("lang/pt_br.json", language(true));
        return resources;
    }

    private static JsonObject language(boolean portuguese) {
        Map<String, String> translations = new TreeMap<>();
        translations.put(
                "itemGroup.material_progression",
                portuguese ? "Progressão de Materiais" : "Material Progression"
        );
        translations.put(
                "container.material_progression.crusher",
                portuguese ? "Britador de Pedra" : "Stone Crusher"
        );
        translations.put(
                "gui.material_progression.recipebook.toggle_crushable",
                portuguese ? "Mostrar Trituráveis" : "Show Crushable"
        );
        translations.put(
                "config.material_progression.server.requireAxeForLogs",
                portuguese
                        ? "Exigir Machado para Troncos"
                        : "Require Axes for Logs"
        );

        translations.put(
                "block.material_progression.crusher",
                portuguese ? "Britador de Pedra" : "Stone Crusher"
        );
        translations.put(
                "block.material_progression.loose_rocks",
                portuguese ? "Pedras Soltas" : "Loose Rocks"
        );
        translations.put(
                "block.material_progression.ground_stick",
                portuguese ? "Graveto no Chão" : "Ground Stick"
        );

        MaterialFamily tin = MaterialFamilies.TIN;
        translations.put(
                blockKey(tin.name() + "_ore"),
                portuguese ? "Minério de Estanho" : "Tin Ore"
        );
        translations.put(
                blockKey("deepslate_" + tin.name() + "_ore"),
                portuguese
                        ? "Minério de Estanho de Ardósia"
                        : "Deepslate Tin Ore"
        );
        translations.put(
                itemKey("raw_" + tin.name()),
                portuguese ? "Estanho Bruto" : "Raw Tin"
        );
        translations.put(
                itemKey(tin.name() + "_ingot"),
                portuguese ? "Barra de Estanho" : "Tin Ingot"
        );
        translations.put(
                itemKey(tin.name() + "_dust"),
                portuguese ? "Pó de Estanho" : "Tin Dust"
        );

        MaterialFamily bronze = MaterialFamilies.BRONZE;
        translations.put(
                itemKey(bronze.name() + "_dust"),
                portuguese ? "Pó de Bronze" : "Bronze Dust"
        );
        translations.put(
                itemKey(bronze.name() + "_ingot"),
                portuguese ? "Barra de Bronze" : "Bronze Ingot"
        );
        translations.put(
                itemKey("copper_dust"),
                portuguese ? "Pó de Cobre" : "Copper Dust"
        );
        translations.put(
                itemKey("rock"),
                portuguese ? "Pedra" : "Rock"
        );
        translations.put(
                itemKey(MaterialFamilies.FLINT.name() + "_shard"),
                portuguese ? "Lasca de Sílex" : "Flint Shard"
        );

        for (MaterialFamily family : MaterialFamilies.ALL) {
            for (ToolKind tool : family.tools()) {
                translations.put(
                        itemKey(family.itemPath(tool)),
                        portuguese
                                ? family.portugueseToolName(tool)
                                : family.englishToolName(tool)
                );
            }
        }

        JsonObject language = new JsonObject();
        translations.forEach(language::addProperty);
        return language;
    }

    private static String itemKey(String path) {
        return "item.material_progression." + path;
    }

    private static String blockKey(String path) {
        return "block.material_progression." + path;
    }
}
