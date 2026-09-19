package com.github.ysbbbbbb.kaleidoscopecookery.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LootTableFormatTest {
    @Test
    void lootTablesDoNotContainIgnoredLegacyFields() throws Exception {
        var resource = getClass().getResource("/data/kaleidoscope_cookery/loot_table");
        assertNotNull(resource);
        try (var paths = Files.walk(Path.of(resource.toURI()))) {
            var tables = paths.filter(path -> path.toString().endsWith(".json")).toList();
            assertFalse(tables.isEmpty());
            for (Path table : tables) {
                try (var reader = Files.newBufferedReader(table)) {
                    assertModernFormat(JsonParser.parseReader(reader), table.toString());
                }
            }
        }
    }

    private static void assertModernFormat(JsonElement element, String path) {
        if (element.isJsonObject()) {
            for (var entry : element.getAsJsonObject().entrySet()) {
                String key = entry.getKey();
                String childPath = path + "/" + key;
                assertFalse(key.equals("conditions") || key.equals("functions") || key.equals("function"),
                        childPath + " uses a pre-26.3 loot field");
                if (key.equals("type")) {
                    assertTrue(entry.getValue().isJsonPrimitive(), childPath);
                    assertFalse(entry.getValue().getAsString().equals("minecraft:block_state_property"),
                            childPath + " uses a removed condition type");
                }
                assertModernFormat(entry.getValue(), childPath);
            }
        } else if (element.isJsonArray()) {
            int index = 0;
            for (var child : element.getAsJsonArray()) {
                assertModernFormat(child, path + "/" + index++);
            }
        }
    }
}
