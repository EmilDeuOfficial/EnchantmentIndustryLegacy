package plus.dragons.createenchantmentindustrylegacy.foundation.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.createmod.ponder.foundation.PonderIndex;
import plus.dragons.createenchantmentindustrylegacy.EnchantmentIndustry;
import plus.dragons.createenchantmentindustrylegacy.dragonLibLegacy.advancement.AdvancementEntry;
import plus.dragons.createenchantmentindustrylegacy.foundation.ponder.CeiPonderPlugin;

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
        providePonderLang(provider);
    }

    /**
     * Scene titles and texts only exist inside the storyboards, so Ponder has to build every scene to
     * collect them. {@code provideLang} does that itself, it just needs the plugin to be registered -
     * which during data generation has not necessarily happened yet.
     */
    private static void providePonderLang(RegistrateLangProvider provider) {
        if (PonderIndex.streamPlugins().noneMatch(plugin -> plugin instanceof CeiPonderPlugin))
            PonderIndex.addPlugin(new CeiPonderPlugin());
        PonderIndex.getLangAccess().provideLang(EnchantmentIndustry.ID, provider::add);
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
