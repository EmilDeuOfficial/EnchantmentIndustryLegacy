package plus.dragons.createenchantmentindustry.foundation.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import plus.dragons.createenchantmentindustry.EnchantmentIndustry;
import plus.dragons.createenchantmentindustry.dragonLibLegacy.advancement.AdvancementEntry;

/**
 * Feeds the hand written {@code lang/default/*.json} partials and the advancement titles into
 * Registrate's generated {@code en_us.json}, the way Create's lang merger used to.
 */
public class CeiLangGen {
    private static final String[] PARTIALS = { "interface", "tooltips" };

    public static void register() {
        EnchantmentIndustry.REGISTRATE.addDataGenerator(ProviderType.LANG, CeiLangGen::generate);
    }

    private static void generate(RegistrateLangProvider provider) {
        for (String partial : PARTIALS)
            addAll(provider, readPartial(partial));
        addAll(provider, AdvancementEntry.provideLangEntries(EnchantmentIndustry.ID));
    }

    private static void addAll(RegistrateLangProvider provider, JsonObject object) {
        for (var entry : object.entrySet()) {
            if (entry.getKey().startsWith("_"))
                continue;
            provider.add(entry.getKey(), entry.getValue().getAsString());
        }
    }

    private static JsonObject readPartial(String name) {
        String path = "/assets/" + EnchantmentIndustry.ID + "/lang/default/" + name + ".json";
        try (InputStream stream = CeiLangGen.class.getResourceAsStream(path)) {
            if (stream == null) {
                EnchantmentIndustry.LOGGER.warn("Missing lang partial {}", path);
                return new JsonObject();
            }
            return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to read lang partial " + path, exception);
        }
    }
}
